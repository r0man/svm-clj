(ns svm.problem
  (:import [libsvm svm_node svm_problem])
  (:use svm.data))

(defn make-node
  "Make a LibSVM node."
  [index value]
  (let [node (svm_node.)]
    (set! (. node index) index)
    (set! (. node value) value)
    node))

(defn make-nodes
  "Make a seq of LibSVM nodes."
  [[label data]] (map #(apply make-node %) data))

(defn make-problem
  "Make a LibSVM problem."
  [dataset]
  (let [problem (svm_problem.)]
    (set! (. problem l) (count dataset))
    (set! (. problem y) (double-array (map first dataset)))
    (set! (. problem x) (into-array (map into-array (map make-nodes dataset))))
    problem))

