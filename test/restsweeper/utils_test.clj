(ns restsweeper.utils-test
  (:use [restsweeper.utils]
        [clojure.test]))

(deftest nand-test
  (are [a b result] (= (nand a b) result)
    true  true   false
    true  false  true
    false true   true
    false false  true)
  (are [a b c result] (= (nand a b c) result)
    true true false  false
    true true true   false))

(deftest xor-test
  (are [a b result] (= (xor a b) result)
    false false  false
    true  false  true
    false true   false
    true  true   false))
