(ns restsweeper.game
  (:use [restsweeper.utils :only [nand]])
  (:use [clojure.contrib.seq-utils :only [rand-elt indexed flatten]]
        [clojure.contrib.def :only [defn-memo]]))

(defn mine? [cell]
  (or (:mine cell) false))

(defn flag? [cell]
  (or (:flag cell) false))

(defn valid-flag? [cell]
  (or (and (mine? cell) (flag? cell))
      false))

(defn uncovered? [cell]
  (or (:uncovered cell) false))

(defn game-over? [board]
  "Has the player hit a mine?"
  (some #(and (:mine %) (:uncovered %)) (flatten board)))

(defn game-won? [board]
  "Has the player uncovered everything without hitting a mine?"
  (every?  #(or (and (:uncovered %) (not (:mine %))
                (and (:flag %) (:mine %))))
           (flatten board)))

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
    ((board ya) xa)))

;; Interaction

(defn uncover [y x h w board]
  "Returns the board with cell at [x y] uncovered."
  (let [cell (get-in board [y x])]
    (if (or (:uncovered cell) (:flag cell))
      board
      (let [board (assoc-in board [y x :uncovered] true)]
        (if (or (:mine cell) (:number cell))
          board
          (reduce
            (fn [brd [ya xa]]
              (let [cl (get-in brd [ya xa])]
                (if (or (:uncovered cl) (:mine cl)
                        (:flag cl) (:number cl))
                  brd
                  (uncover ya xa h w brd))))
            board
            (adjecent-coords y x h w)))))))

(defn flag [y x h w board]
  "Returns the board with cell at [x y] flagged."
  (let [cell (get-in board [y x])]
    (cond
      (:flag cell)
        (assoc-in board [y x :flag] false)
      (:uncovered cell)
        board
      :else
        (assoc-in board [x y :flag] true))))


;; Board construction

(defn place-numbers
  "Returns the board with the count of adjecent mines added to cells."
  [h w board]
  (for [[y row] (indexed board)]
    (for [[x cell] (indexed row)]
      (let [number (->> (adjecent-cells y x h w board)
                     (filter :mine)
                     (count))]
        (if (> number 0)
          (assoc cell :number number)
          cell)))))

(defn empty-cells
  "Returns a seq of coordinates [y x] that describe which cells on the
   given board don't contain anything."
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
    (filter (complement nil?))))

  ; NB: If we didn't need the coordinates, empty-cells could be much simpler:
  ; (filter (complement mine?) (flatten board))

(defn place-mines
  "Places m mines in randomly chosen locations on a board of size h×w."
  [h w m board]
  {:pre [(< m (* h w))]}
  (if (> m 0)
    (let [[y x] (rand-elt (empty-cells board))]
      (recur h w (dec m) (assoc-in board [y x :mine] true)))
    board))

(defn-memo make-empty-board
  "Creates a new, empty board w wide and h high."
  [h w]
    (vec (repeat h (vec (repeat w {})))))

(defn make-board
  "Generates a board whose width is w, height is h and number of mines is m."
  [h w m]
  (->> (make-empty-board h w)
    (place-mines h w m)))
