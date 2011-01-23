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

The easiest way to start RESTsweeper is to run it as a Java class:

    $ lein compile
    $ java -cp "classes:lib/*" restsweeper.Main

If you want to experiment with the source, change things and then immediately see the effect:

    $ lein repl
    user=> (run)
    ; Then, every time after you've changed the source:
    user=> (require '[restsweeper.run :reload-all true])

Now point your browser to http://localhost:8080/ and enjoy.

Leiningen has much improved the Java classpath and dependency handling already.
However, if after an extended amout of fiddling you still can't seem to get
this software to work, just send me a message and I'll try to help you.

## Hacking

Sure, please do! I wrote this software for the learning experience, so if you
learn something by reading, using or even improving this code, it has served
its purpose.

Some enhancements that could provide a challenge:

* Switch from Compojure HTML to Enlive templating.
* Use Gaka or csslj to construct CSS styles. Or simply move them out to a static file.
* The reversible hashing algorithm used to compress the game board into URLs is
  ugly and breaks for larger board sizes. There's probably an easier way to do this.
* Almost all unit tests are outdated because I stopped updating them halfway
  through development. Please fix and extend them at your leisure :-)

## License

Copyright (C) 2010  Daniel D. Werner

Distributed under the 2-clause BSD License. See the file COPYING.
