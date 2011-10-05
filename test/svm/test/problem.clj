(ns svm.test.problem
  (:import [libsvm svm_node svm_problem])
  (:use clojure.test
        svm.problem))

(def example-dataset
  [[-1 {1 0.166667, 2 1.0, 3 1.0, 4 -0.132075, 5 -0.69863, 6 -1.0, 7 -1.0, 8 0.175573, 9 -1.0, 10 -0.870968, 12 -1.0, 13 0.5}]
   [-1 {1 0.166667, 2 1.0, 3 1.0, 4 -0.132075, 5 -0.69863, 6 -1.0, 7 -1.0, 8 0.175573, 9 -1.0, 10 -0.870968, 12 -1.0, 13 0.5}]])

(deftest test-make-problem
  (let [problem (make-problem example-dataset)]
    (is (isa? (class problem) svm_problem))))

(deftest test-make-node
  (let [node (make-node 1 0.166667)]
    (is (isa? (class node) svm_node))
    (is (= 1 (. node index)))
    (is (= 0.166667 (. node value)))))

(deftest test-make-nodes
  (let [nodes (make-nodes (first example-dataset))]
    (is (= 12 (count nodes)))))
