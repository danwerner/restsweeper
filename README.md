# RESTsweeper

RESTsweeper is a purely functional, RESTful implementation of the Minesweeper
game. It is written in Clojure using the excellent Compojure web framework.

## Installation

You have to have the Sun JDK or Open JDK plus Maven pre-installed. If you do
not have a copy of Leiningen yet, download the
[lein shell script](http://github.com/technomancy/leiningen/raw/stable/bin/lein)
and drop it somewhere into your PATH.

Leiningen will deal with all other dependencies automatically. Just run:

    $ lein self-install
    $ cd /path/to/restsweeper
    $ lein deps

## Usage

The easiest way to run RESTsweeper is to execute:

    $ java -cp "src:lib/*" clojure.main src/restsweeper/run.clj

Now point your browser to http://localhost:8080/ and enjoy.

It's also possible to start a REPL with `lein repl`, then either
`(require 'restsweeper.run)` or play with the Clojure code instead :-)

Leiningen has much improved the Java classpath and dependency handling already.
However, if after an extended amout of fiddling you still can't seem to get
this software to work, just send me a message and I'll try to help you.

## Hacking

Sure, please do! I wrote this software for the learning experience, so if you
learn something by reading, using or even improving this code, it has served
its purpose.

Some enhancements that could provide a challenge:

* Flagging is 99% implemented in the backend, but there is currently no
  user-visible way to actually put flags on cells, the reason being that
  left-clicking on links is currently the only mechanism to advance to the next
  game step. I'd very much prefer to implement this user interaction without
  resorting to JavaScript, so: If you have any idea how to handle this, please
  drop me a message. Or better yet: Fork this repo and implement it yourself!
* The unit tests don't compile anymore because I stopped updating halfway
  through development.

## License

Copyright (C) 2010  Daniel D. Werner

Distributed under the 2-clause BSD License. See the file COPYING.
