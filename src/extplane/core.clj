;; by Hendrik Volkmer
;; November 13, 2013

;; Copyright (c) Hendrik Volkmer, November 13, 2013. All rights reserved. The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns extplane.core
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader))
  (:require [instaparse.core :as insta]
            [extplane.mappings :as mappings]
            [clojure.data.codec.base64 :as b64]
            [clojure.java.io :as io])
  (:use  [clojure.core.match :only (match)]))

(def xplane {:name "localhost" :port 51000})
(def ^:dynamic *debug* false)
(def dataref-handlers (ref {}))

(def extplane-parser
  (insta/parser "
extplane_protocol = version | data_ref
data_ref = integer_array_dataref | float_array_dataref | integer_dataref | float_dataref | double_dataref | data_dataref
integer_array_dataref = type_integer_array <' '> dataref_name <' '> integer_array
float_array_dataref = type_float_array <' '> dataref_name <' '> float_array
integer_dataref = type_integer <' '> dataref_name <' '> integer
float_dataref = type_float <' '> dataref_name <' '> float
double_dataref = type_double <' '> dataref_name <' '> float
data_dataref = type_data <' '> dataref_name <' '> base64data
float_array = <'['> float (<','> float)* <']'>
integer_array = <'['> integer (<','> integer)* <']'>
value = integer | float
dataref_name = #'[a-zA-Z/_0-9]+'
integer = '-'? #'[0-9]+'
float =  #'-?\\d+' '.'? #'\\d+'?
base64data = #'^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$'
version = <'EXTPLANE'> <' '> integer
type = type_integer_array | type_integer | type_float | type_double | type_float_array | type_data
type_integer_array = 'uia'
type_integer = 'ui'
type_float = 'uf'
type_double = 'ud'
type_float_array = 'ufa'
type_data = 'ub'
"))

(declare conn-handler)
(declare parse-input)

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))

(defn- write [conn msg]
  (doto (:out @conn)
    (.println (str msg "\r"))
    (.flush)))

(defn conn-handler [conn]
  (while (nil? (:exit @conn))
    (let [msg (.readLine (:in @conn))]
      (if *debug*
        (println (str "DEBUG: >" msg "<")))
      (if (nil? msg)
        (dosync (alter conn merge {:exit true}))
        (parse-input msg)))))

;;; This is the actual external API
(defn subscribe-dataref [conn refname]
  (write conn (str "sub " refname)))

(defn unsubscribe-dataref [conn refname]
  (write conn (str "unsub " refname)))

(defn set-update-interval [conn hz]
  (write conn (str "extplane-set update_interval " (float(/ 1 hz)))))

(defn press-down [conn button]
  (write conn (str "but " (mappings/get-button-mapping button))))

(defn release [conn button]
  (write conn (str "rel " (mappings/get-button-mapping button))))

(defn toggle [conn key]
  (write conn (str "key " (mappings/get-key-mapping key))))

(defn set-dataref [conn refname value]
  ;; It is only possible to write to a data-ref if it is subscribed. We don't keep track
  ;; of subscriptions so we just issue a subscription before writing
  (subscribe-dataref conn refname)
  (write conn (str "set " refname " " value)))

(defn disconnect [conn]
  (write conn "disconnect"))

(defn add-dataref-handler!
  "Adds a handler function for a data ref. The handler function will get called if
   the value inside X-Plane is changed and the the client has subscribed to
   this data-ref. This function does NOT subscribe to the dataref."
  [dataref handler-fn]
  (dosync (alter dataref-handlers assoc dataref handler-fn)))

(defn remove-dataref-handler!
  "Removes the handler function. This does not unsubscribe the data-ref"
  [dataref]
  (dosync (alter dataref-handlers dissoc dataref)))

(defn- handle-dataref
  "Handles the callback by calling the registered function with the changed value.
   If a data ref is subscribed and no handler is defined, a noop function will be called."
  [ref-name value]
  ((@dataref-handlers ref-name (fn [_])) value))

(defn- parse-number [value-as-string]
  (read-string (clojure.string/join  (rest value-as-string))))

(defn- value-array [array]
  (vec (map parse-number (rest array))))

(defn- decode64 [encoded]
  (String. (b64/decode (.getBytes encoded)) "UTF-8"))

(defn- parse-input
  "uses the extplane parser to parse the message and, if the message is data ref, calls the handler fn.
   The data-ref data will be converted to either int, float, double, a vector of float or int, or if it
   is base64 encoded to binary."
  [msg]
  (let [parsed-message (match (extplane-parser msg)
                              [:extplane_protocol [:data_ref [:data_dataref [:type_data "ub"] [:dataref_name data_ref] [:base64data content]]]] {:ref data_ref :content (decode64 content)}
                              [:extplane_protocol [:data_ref [:float_array_dataref [:type_float_array "ufa"] [:dataref_name data_ref] array]]] {:ref data_ref :content (value-array array)}
                              [:extplane_protocol [:data_ref [:integer_array_dataref [:type_integer_array "uia"] [:dataref_name data_ref] array]]] {:ref data_ref :content (value-array array)}
                              [:extplane_protocol [:data_ref [_ _ [:dataref_name data_ref] value]]] {:ref data_ref :content (parse-number value)}
                              [:extplane_protocol [:version [:integer version]]] {:version version}
                              )]
    (if (parsed-message :ref)
      (handle-dataref (parsed-message :ref) (parsed-message :content)))))
