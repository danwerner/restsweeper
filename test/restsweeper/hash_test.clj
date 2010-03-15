(ns restsweeper.hash-test
  (:use [restsweeper.hash] :reload-all)
  (:use [clojure.test]))

(def *empty-board*
  [[{} {} {}]
   [{} {} {}]
   [{} {} {}]])

(def *testboard*
  [[{}, {:mine true}, {}]
   [{:mine true :flag true}, {:mine true}, {}]
   [{:mine true}, {:flag true}, {:mine true}]])

(deftest cell->num-test
  (are [cell bitval] (= (cell->num cell) bitval)
    {}                 0
    {:flag false}      0
    {:mine true}       1
    {:flag true}       2
    {:mine true,
     :flag true}       3))

(deftest hash-board-test
  (are [board hash] (= (hash-board board) hash)
    *testboard*    "9d51e9"
    *empty-board*  "0"))

(deftest unhash-board-test
  (are [hash board] (= (unhash-board 3 3 hash) board)
    "9d51e9"  *testboard*
    "0"       *empty-board*))

(deftest hash-and-unhash-test
  (are [board] (= board (unhash-board 3 3 (hash-board board)))
    *testboard*
    *empty-board*))
