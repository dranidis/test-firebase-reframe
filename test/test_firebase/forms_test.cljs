(ns test-firebase.forms-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [test-firebase.forms :refer [string-list->map-list if-string-list?->map-list]]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 1))))

(deftest test-string-list->map-list
  (testing "empty list"
    (is (= [] (string-list->map-list []))))
  (testing "a list"
    (is (= [{:value "A"}] (string-list->map-list ["A"])))))

(deftest test-if-string-list?->map-list
  (testing "empty list"
    (is (= [] (if-string-list?->map-list []))))
  (testing "a list"
    (is (= [{:value "A"}] (if-string-list?->map-list ["A"]))))
  (testing "a map"
    (is (= [{:id "1"}] (if-string-list?->map-list [{:id "1"}])))))

