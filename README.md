# RESTsweeper

RESTful implementation of the Minesweeper game. Written in Clojure and using
the excellent Compojure web framework.

## Installation

The only pre-dependencies are the Sun JDK/OpenJDK, Maven and Leiningen. The latter
will automatically download all other dependencies.

* Download the [lein shell script](http://github.com/technomancy/leiningen/raw/stable/bin/lein)
  and drop it into any directory on your PATH
* lein self-install
* cd /path/to/restsweeper
* lein deps

## Usage

Run with something like:

    > java -cp src:lib/clojure-1.1.0.jar:lib/clojure-contrib-1.1.0.jar:lib/commons-codec-1.3.jar:lib/commons-fileupload-1.2.1.jar:lib/commons-io-1.4.jar:lib/compojure-0.3.2.jar:lib/jetty-6.1.21.jar:lib/jetty-util-6.1.21.jar:lib/servlet-api-2.5-20081211.jar clojure.main src/restsweeper/run.clj

It's also possible to start a REPL with 'lein repl', then either
(require 'restsweeper.run) or play with the Clojure code instead :-)

If after an extended amout of fiddling you can't seem to get this to work,
just send me a message and I'll see what I can do.

## Hacking

Sure, please do! I wrote this software for the learning experience, so if you
learn something by reading, using or even improving this code, I'd feel honored.

Some things that still need attention:

* Flagging is 99% implemented in the backend, but there is currently no
  user-visible way to actually put flags on cells, the reason being that
  left-clicking on links is currently the only mechanism to advance to the
  next game step. I'd very much prefer to implement this user interaction
  without resorting to JavaScript, so: If you have any idea how to handle
  this, please drop me a message.
* The unit tests don't compile anymore.

## License

Copyright (C) 2010  Daniel D. Werner

Distributed under the 2-clause BSD License. See the file COPYING.
