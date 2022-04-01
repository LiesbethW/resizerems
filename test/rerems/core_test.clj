(ns rerems.core-test
  (:require [clojure.test :refer :all]
            [rerems.core :refer :all]))

(deftest multiplying
  (testing "returns .-separated value"
    (is (= (times 0.75 "1") "0.75")))
  (testing "rounds to 3 decimals"
    (is (= (times 0.75 "1.375") "1.031"))
    (is (= (times 0.75 "0.25") "0.188")))
  (testing "doesn't have trailing zeros"
    (is (= (times 0.75 "4") "3")))
  (testing "does have trailing zeros before the dot"
    (is (= (times 0.75 "40") "30"))))

(deftest replacing
  (testing "find rem"
    (is (= (replace-rem-values "height: 3.0rem;") "height: 2.25rem;"))
    (is (= (replace-rem-values "height: 1.33rem;") "height: 0.998rem;"))
    (is (= (replace-rem-values "0 1rem") "0 0.75rem"))
    (is (= (replace-rem-values ".75rem") "0.563rem")))
  (testing "doesn't choke on '.rem' with no digits"
    (is (= (replace-rem-values "&.new .button.remove") "&.new .button.remove"))))
