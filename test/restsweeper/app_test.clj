(ns restsweeper.app-test
  (:use [restsweeper.app] :reload-all)
  (:use [clojure.test]))

; TODO:
; * Rewrite tests for new bitmask definition

(def *testboard*
  [[{}, {:mine true}, {}]
   [{:mine true :flag true}, {:mine true}, {}]
   [{:mine true}, {:flag true}, {:mine true}]])

(deftest parse-boardsize-test
  (are [string result] (= (next (parse-boardsize string)) result)
    "3x4x5"  ["3" "4" "5"]
    "3x4"    ["3" "4" nil]
    "3x4x"   nil
    "foobar" nil))
