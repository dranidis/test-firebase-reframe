(ns test-firebase.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [test-firebase.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 1))))
