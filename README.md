# clj-extplane

A small Clojure library designed to interface with [ExtPlane](https://github.com/vranki/ExtPlane) a TCP based interface to the X-Plane flight simulator.

## Usage

You need the following software already working to use clj-extplane

- [X-Plane](www.x-plane.com/) - The flight simulator
- [ExtPlane](https://github.com/vranki/ExtPlane) - TCP interface to X-Plane

If you run X-Plane and you can connect to `localhost:51000` (e.g. using netcat), you're in good shape. ExtPlane can be tricky to install on Mac OS X. See below for some hints.

### Setting values

To get started fire up x-plane and make sure the ExtPlane plugin is running. Then open a REPL and set some data:

    (def xplane-conn (connect xplane))
    (set-dataref xplane-conn "sim/cockpit/radios/nav2_freq_hz" 11500)

### Getting values

To get values (and do s.th. with them) you have to define handler functions that gets called, if one data-ref changes:

	(subscribe-data-ref "sim/cockpit/radios/nav1_freq_hz")
    (defn handle-nav1-freq-change [freq]
		(println freq))
	(add-dataref-handler! "sim/cockpit/radios/nav1_freq_hz" handle-nav1-freq-change)

Now go into X-Plane and turn the NAV 1 knob...

### Pressing buttons or command keys

The buttons or keys are symbols referencing the names in [XPLMUtilities](http://www.xsquawkbox.net/xpsdk/mediawiki/XPLMUtilities) (without the leading `xplm`).

    (press-down :joy_flapsdn)
    (release :joy_flapsdn)

Or you can just use the convienence funtion "toggle":

    (toggle :key_forward)

### Setting the update interval

    (set-update-interval 30)

## Internals

It makes use of the fantastic [instaparse](https://github.com/Engelberg/instaparse) and [core.match](https://github.com/clojure/core.match) libraries.

## Getting ExtPlane to compile on OS X

This howto assumes you have the XCode 5.0 and the Xcode command line tools installed. It also assumes you use homebrew.

1. Download the X-Plan SDK
2. Download/Checkout ExtPlane code
3. `brew install qt`
4. Edit the `extplane-plugin.pro` file and change:
    - `INCLUDEPATH` to match your X-Plane SDK folder
    - Remove any `ppc` reference
    - Change `x86` to `x86_64` in the `CONFIG` line for macx
5. run `qmake`
6. run `make`

## License

Copyright © 2013 Hendrik Volkmer

Distributed under the Eclipse Public License, the same as Clojure.
