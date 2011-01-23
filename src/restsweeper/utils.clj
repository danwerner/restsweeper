(ns restsweeper.utils)

(defmacro nand [& body]
  `(not (and ~@body)))

(defmacro xor [a b]
  `(let [a# ~a b# ~b]
     (or (and a# (not b#)) (and b# (not a#)))))

(defn vectorize [s-o-s]
  "Turns a seq-of-seqs into a vector-of-vectors."
  (vec (map vec s-o-s)))

(defn badrequest [body]
  {:status 400
   :headers {}
   :body body})
