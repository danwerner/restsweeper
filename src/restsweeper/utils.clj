(ns restsweeper.utils)

(defmacro nand [& body]
  `(not (and ~@body)))

(defn vectorize [s-o-s]
  "Turns a seq-of-seqs into a vector-of-vectors."
  (vec (map vec s-o-s)))
