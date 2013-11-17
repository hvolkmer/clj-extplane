(ns extplane.core-test
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream InputStreamReader BufferedReader PrintWriter))
  (:use clojure.test
        extplane.core))

(defn read-fake-conn [conn]
  (.toString (conn :outstream)))

(defn fake-in-conn [input]
  (let [instream (ByteArrayInputStream. (.getBytes input))
        in (BufferedReader. (InputStreamReader. instream))]
    (ref {:in in})))

(defn fake-conn []
  (let [outstream (ByteArrayOutputStream.)
        out (PrintWriter. outstream)]
    (ref {:out out :outstream outstream})))

(defn conn-test-wrapper [fn-to-execute & args]
  (let [conn (fake-conn)]
    (apply fn-to-execute conn args)
    (read-fake-conn conn)))

(def test-ref (ref {}))

(defn ref-handling-wrapper [ref-name in-string]
  (dosync (ref-set test-ref {}))
  (add-dataref-handler! ref-name (fn [x] (dosync (alter test-ref assoc :test x)) ) )
  (conn-handler (fake-in-conn in-string)))

;; TODO: What to do when no ref supplied
(deftest test-subscribe-dataref
  (testing "subsciption"
    (is (= "sub some/dataref\r\n" (conn-test-wrapper subscribe-dataref "some/dataref")))))

(deftest test-unsubscribe-dataref
  (testing "remove subsciption"
    (is (= "unsub some/dataref\r\n" (conn-test-wrapper unsubscribe-dataref "some/dataref")))))

;; TODO: What to do on no interval
(deftest test-set-update-interval
  (testing "update interval"
    (is (= "extplane-set update_interval 0.02\r\n" (conn-test-wrapper set-update-interval 50)))))

(deftest test-press-down
  (testing "press down"
    (is (= "but 81\r\n" (conn-test-wrapper press-down :joy_TOGA)))))

;; TODO: What to do on unkown button
(deftest test-press-down-unkown
  (testing "unkown button"
    (is (= "but \r\n" (conn-test-wrapper press-down :unknown)))))

(deftest test-release
  (testing "release"
    (is (= "rel 81\r\n" (conn-test-wrapper release :joy_TOGA)))))

(deftest test-toggle
  (testing "toggle"
    (is (= "key 0\r\n" (conn-test-wrapper toggle :key_pause)))))

(deftest test-disconnect
  (testing "disconnect"
    (is (= "disconnect\r\n" (conn-test-wrapper disconnect)))))

(deftest test-set-dataref
  (testing "set-dataref"
    (is (= "sub some/dataref\r\nset some/dataref 100\r\n" (conn-test-wrapper set-dataref "some/dataref" 100)))))

(deftest test-connect-disconnect
  (testing "disconnect"
    (is (= nil (conn-handler (fake-in-conn "EXTPLANE 1"))))))

(deftest test-integer-ref
  (testing "integer-ref"
    (ref-handling-wrapper "acf_num_engines" "ui acf_num_engines 2")
    (is (= 2 (@test-ref :test)))))

(deftest test-float-ref
  (testing "float ref"
    (ref-handling-wrapper "heading" "uf heading 2.23")
    (is (= 2.23 (@test-ref :test)))))

(deftest test-float-with-int-ref
  (testing "float ref with int "
    (ref-handling-wrapper "heading" "uf heading 1")
    (is (= 1 (@test-ref :test)))))

(deftest test-integer-array-ref
  (testing "integer array ref"
    (ref-handling-wrapper "N1_percent" "uia N1_percent [99,97]")
    (is (= [99 97] (@test-ref :test)))))

(deftest test-float-array-ref
  (testing "float array ref"
    (ref-handling-wrapper "percent" "ufa percent [22.22,922.22]")
    (is (= [22.22,922.22] (@test-ref :test)))))

(deftest test-double-ref
  (testing "double ref"
    (ref-handling-wrapper "heading" "ud heading 223.23")
    (is (= 223.23 (@test-ref :test)))))

(deftest test-binary-ref
  (testing "binary ref"
    (ref-handling-wrapper "acf_descrip" "ub acf_descrip RXh0UGxhbmUgU2ltdWxhdGVkIENvbm5lY3Rpb24=")
    (is (= "ExtPlane Simulated Connection" (@test-ref :test)))))
