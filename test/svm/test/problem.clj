(ns svm.test.problem
  (:import [libsvm svm_node svm_problem])
  (:use clojure.test
        svm.problem))

(def example-dataset
  [[-1 {1 0.166667, 2 1.0, 3 1.0, 4 -0.132075, 5 -0.69863, 6 -1.0, 7 -1.0, 8 0.175573, 9 -1.0, 10 -0.870968, 12 -1.0, 13 0.5}]
   [-1 {1 0.166667, 2 1.0, 3 1.0, 4 -0.132075, 5 -0.69863, 6 -1.0, 7 -1.0, 8 0.175573, 9 -1.0, 10 -0.870968, 12 -1.0, 13 0.5}]])

(deftest test-make-problem
  (let [problem (make-problem example-dataset)]
    (is (instance? svm_problem problem))))

(deftest test-make-node
  (let [node (make-node 1 0.166667)]
    (is (instance? svm_node node))
    (is (= 1 (. node index)))
    (is (= 0.166667 (. node value)))))

(deftest test-make-nodes
  (let [nodes (make-nodes (first example-dataset))]
    (is (= 12 (count nodes)))))

(deftest test-read-problem
  (let [problem (read-problem "test-resources/heart_scale")]
    (is (instance? svm_problem problem))
    (is (= 270 (.l problem)))
    (is (= 270 (count (.x problem))))
    (is (every? #(instance? svm_node %1) (flatten (.x problem))))
    (is (= 270 (count (.y problem))))
    (is (every? number? (.y problem)))))
