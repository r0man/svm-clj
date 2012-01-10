(ns svm.test.problem
  (:import [libsvm svm_node svm_model svm_parameter svm_problem])
  (:use clojure.test
        svm.data
        svm.problem))

(def example-dataset (read-dataset "test-resources/heart_scale"))

(def example-model (train-model example-dataset))

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

(deftest test-make-params
  (let [dataset (read-dataset "test-resources/heart_scale")]
    (let [params (make-params dataset)]
      (is (instance? svm_parameter params))
      (is (= svm_parameter/C_SVC (.svm_type params)))
      (is (= svm_parameter/RBF (.kernel_type params)))
      (is (= 3 (.degree params)))
      (is (= 0.07692307692307691 (.gamma params)))
      (is (= 0.0 (.coef0 params)))
      (is (= 0.5 (.nu params)))
      (is (= 100.0 (.cache_size params)))
      (is (= 1.0 (.C params)))
      (is (= 1e-3 (.eps params)))
      (is (= 0.1 (.p params)))
      (is (= 1 (.shrinking params)))
      (is (= 0 (.probability params)))
      (is (= 0 (.nr_weight params)))
      (is (= 0 (count (.weight params))))
      (is (= 0 (count (.weight_label params)))))
    (let [params (make-params dataset :C 2)]
      (is (instance? svm_parameter params))
      (is (= svm_parameter/C_SVC (.svm_type params)))
      (is (= svm_parameter/RBF (.kernel_type params)))
      (is (= 3 (.degree params)))
      (is (= 0.07692307692307691 (.gamma params)))
      (is (= 0.0 (.coef0 params)))
      (is (= 0.5 (.nu params)))
      (is (= 100.0 (.cache_size params)))
      (is (= 2.0 (.C params)))
      (is (= 1e-3 (.eps params)))
      (is (= 0.1 (.p params)))
      (is (= 1 (.shrinking params)))
      (is (= 0 (.probability params)))
      (is (= 0 (.nr_weight params)))
      (is (= 0 (count (.weight params))))
      (is (= 0 (count (.weight_label params)))))))

(deftest test-read-problem
  (let [problem (read-problem "test-resources/heart_scale")]
    (is (instance? svm_problem problem))
    (is (= 270 (.l problem)))
    (is (= 270 (count (.x problem))))
    (is (every? #(instance? svm_node %1) (flatten (.x problem))))
    (is (= 270 (count (.y problem))))
    (is (every? number? (.y problem)))))

(deftest test-train-model
  (let [model (train-model example-dataset)]
    (is (instance? svm_model model))))

(deftest test-save-model
  (is (= "tmp/heart_scale.model"
         (save-model example-model "tmp/heart_scale.model"))))

(deftest test-predict
  (are [index label]
    (is (= label (predict example-model (last (nth example-dataset index)))))
    0 1.0
    1 -1.0
    2 -1.0
    3 1.0))
