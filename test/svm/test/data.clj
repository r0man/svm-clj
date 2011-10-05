(ns svm.test.data
  (:use clojure.test
        svm.data))

(def example-data
  [-1 {1 0.166667, 2 1.0, 3 1.0, 4 -0.132075, 5 -0.69863, 6 -1.0, 7 -1.0, 8 0.175573, 9 -1.0, 10 -0.870968, 12 -1.0, 13 0.5}])

(def example-line
  "-1 1:0.166667 2:1 3:1 4:-0.132075 5:-0.69863 6:-1 7:-1 8:0.175573 9:-1 10:-0.870968 12:-1 13:0.5")

(deftest test-format-libsvm-line
  (let [line (format-libsvm-line example-data)]
    (is (= "-1 1:0.166667 2:1.0 3:1.0 4:-0.132075 5:-0.69863 6:-1.0 7:-1.0 8:0.175573 9:-1.0 10:-0.870968 12:-1.0 13:0.5" line))
    (is (= example-data (parse-libsvm-line line)))))

(deftest test-parse-libsvm-line
  (let [[label data] (parse-libsvm-line example-line)]
    (is (= -1 label))
    (is (= 0.166667 (get data 1)))
    (is (nil? (get data 11)))
    (is (= 0.5 (get data 13)))))