(ns svm.test.data
  (:use clojure.test
        svm.data))

(def example-line
  "-1 1:0.166667 2:1 3:1 4:-0.132075 5:-0.69863 6:-1 7:-1 8:0.175573 9:-1 10:-0.870968 12:-1 13:0.5")

(deftest test-parse-libsvm-line
  (let [[label data] (parse-libsvm-line example-line)]
    (is (= -1 label))
    (is (= 0.166667 (get data 1)))
    (is (= 0.5 (get data 13)))))