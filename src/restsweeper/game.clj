(ns restsweeper.game
  (:use [restsweeper.utils :only [nand]]
        [clojure.contrib.core :only [dissoc-in]]
        [clojure.contrib.seq :only [indexed]]
        [clojure.contrib.def :only [defn-memo]]))

;; Predicates

(def mine? (comp boolean :mine))
(def numbered? (comp boolean :number))
(def flag? (comp boolean :flag))
(def uncovered? (comp boolean :uncovered))

(defn cascade-uncover? [cell]
  "After uncovering this cell, should adjecent cells be uncovered?"
  (not (or (mine? cell) (numbered? cell))))

(defn can-interact? [cell]
  "Is the player able to interact with this cell in the current turn,
  e.g. uncover it or flag it?"
  (not (or (uncovered? cell)
           (flag? cell))))

(defn game-lost? [board]
  "Has the player hit a mine?"
  (some #(and (:mine %) (:uncovered %)) (flatten board)))

(defn game-won? [board]
  "Has the player survived to reach a state where it is obvious which cells
   do contain mines?"
  (let [fboard (flatten board)]
    (every? #(or (and (uncovered? %) (not (mine? %)))
                 (and (not (uncovered? %)) (mine? %)))
            fboard)))

(defn max-mines [h w]
  "Returns the maximum number of mines allowed on a board of size h×w."
  (* (dec h) (dec w)))


;; Cell relations

(defn adjecent-coords [y x h w]
  "Returns a seq of coordinate pairs [ya xa] that specify which cells are
   adjecent to the one named by [y x]. Accounts for board boundaries."
  (filter
    (fn [[ya xa]]
      (and (<= 0 ya (dec h))
           (<= 0 xa (dec w))
           (nand (= xa x) (= ya y))))
    (for [ya [(dec y) y (inc y)]
          xa [(dec x) x (inc x)]]
      [ya xa])))

(defn adjecent-cells [y x h w board]
  (for [[ya xa] (adjecent-coords y x h w)]
    (get-in board [ya xa])))

;; Interaction

(defn uncover
  "Returns the board with cell at [x y] uncovered. If the cell is an empty one,
   will also cascade to uncover adjecent empty and numbered cells.
   If uncover-mine? is true, will also uncover cells containing mines."
  ([y x h w uncover-mine? board]
    (let [cell (get-in board [y x])]
      (if (or (uncovered? cell)
              (flag? cell)
              (and (mine? cell) (not uncover-mine?)))
        board
        (let [board (assoc-in board [y x :uncovered] true)]
          (if (not (cascade-uncover? cell))
            board
            (reduce
              (fn [brd [ya xa]]
                (uncover ya xa h w false brd))
              board
              (adjecent-coords y x h w)))))))
  ([y x h w board]
    (uncover y x h w true board)))

(defn flag
  "Returns the board with the flag at cell [x y] toggled."
  [y x h w board]
  (let [cell (get-in board [y x])]
    (cond
      (uncovered? cell)
        board
      (flag? cell)
        (dissoc-in board [y x :flag])
      :else
        (assoc-in board [y x :flag] true))))


;; Board construction

(defn place-numbers
  "Returns the board with the count of adjecent mines added to cells."
  [h w board]
  (for [[y row] (indexed board)]
    (for [[x cell] (indexed row)]
      (let [number (->> (adjecent-cells y x h w board)
                        (filter mine?)
                        (count))]
        (if (> number 0)
          (assoc cell :number number)
          cell)))))

(defn empty-cells
  "Returns a seq of coordinates [y x] that describe which cells on the
   given board don't contain mines."
  [board]
  (->> 
    (mapcat (fn [y row]
              (map (fn [x cell]
                     (if-not (mine? cell)
                       [y x]))
                   (iterate inc 0)
                   row))
            (iterate inc 0)
            board)
    (keep identity)))

  ; NB: If we didn't need the coordinates, empty-cells could be much simpler:
  ; (filter (complement mine?) (flatten board))

(defn place-mines
  "Places m mines in randomly chosen locations on a board of size h×w.
   Returns the new board, or nil if there is not enough room for m mines."
  [h w m board]
  (if (< m (max-mines h w))
    (if (> m 0)
      (let [[y x] (rand-nth (empty-cells board))]
        (recur h w (dec m) (assoc-in board [y x :mine] true)))
      board)))

(defn-memo make-empty-board
  "Creates a new, empty board w wide and h high."
  [h w]
    (vec (repeat h (vec (repeat w {})))))

(defn make-board
  "Generates a board whose width is w, height is h and number of mines is m."
  [h w m]
  (->>
    (make-empty-board h w)
    (place-mines h w m)))
