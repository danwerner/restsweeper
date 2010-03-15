(ns restsweeper.app-test
  (:use [restsweeper.app] :reload-all)
  (:use [clojure.test]))

; TODO:
; * Rewrite tests for new bitmask definition

(def *empty-board*
  [[{} {} {}]
   [{} {} {}]
   [{} {} {}]])

(def *testboard*
  [[{}, {:mine true}, {}]
   [{:mine true :flag true}, {:mine true}, {}]
   [{:mine true}, {:flag true}, {:mine true}]])

(deftest empty-cells-test
  (is (= (empty-cells *testboard*)
         #{[2 1] [0 0] [0 2]}))
  (is (= (empty-cells *empty-board*)
         #{[2 1] [1 0] [2 2], [0 0] [1 1] [0 1], [1 2] [0 2] [2 0]})))

(deftest parse-boardsize-test
  (are [string result] (= (next (parse-boardsize string)) result)
    "3x4x5"  ["3" "4" "5"]
    "3x4"    ["3" "4" nil]
    "3x4x"   nil
    "foobar" nil))
