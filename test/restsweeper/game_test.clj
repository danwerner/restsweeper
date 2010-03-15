(ns restsweeper.app-test
  (:use [restsweeper.game] :reload-all)
  (:use [clojure.test]))

(def *testboard*
  [[{}, {:mine true}, {}]
   [{:mine true :flag true}, {:mine true}, {}]
   [{:mine true}, {:flag true}, {:mine true}]])


(deftest adjecent-coords-test
  (are [y x h w adjecents] (= (adjecent-coords y x h w) adjecents)
    0 0 5 5  '([0 0] [0 1] [1 0] [1 1])
    1 1 5 5  '([0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2])
    4 4 5 5  '([3 3] [3 4] [4 3] [4 4])
    3 3 5 5  '([2 2] [2 3] [2 4] [3 2] [3 3] [3 4] [4 2] [4 3] [4 4]))

(deftest adjecent-cells-test
  (are [y x h w board adjecents] (= (adjecent-cells y x h w) adjecents)
    1 1 3 3 *testboard*  '({} {:mine true} {}, {:mine true :flag true} #_cell {}, {:mine true} {:flag true} {:mine true})))
