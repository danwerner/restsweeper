(ns restsweeper.hash
  (:use [restsweeper.game :only [uncovered? mine? flag?]]))

(def bit-values {uncovered? 1, mine? 2, flag? 4})

; Note: Resulting Integer must be < 16 so hash-board doesn't break.
(defn cell->num
  "Represents the cell as a numeric value."
  [cell]
  (->>
    (map (fn [[test bitval]]
           (if (test cell) bitval))
         bit-values)
    (filter (complement nil?))
    (apply +)))

(def num->cell
  {0 {}
   1 {:uncovered true}
   2 {:mine true}
   4 {:flag true}
   ; permutations
   3 {:uncovered true, :mine true}
   5 {:uncovered true, :flag true}
   6 {:mine true, :flag true}
   7 {:uncovered true, :mine true, :flag true}})

(let [base 36]
  (defn hash-board [board]
    "Returns a reversibly hashed representation of the game board."
    (->> (flatten board)
      (map cell->num)
      (apply str)
      (BigInteger.)
      (#(.toString % base))))

  (defn unhash-board [h w hash]
    "Turns a hashed board back into a seq-of-seqs."
      (->> (BigInteger. hash base)
        (format (str "%0" (* h w) "d"))
        (map #(Integer/parseInt (str %)))
        (map num->cell)
        (partition h))))
