(ns svm.test.core
  (:import [libsvm svm_node svm_model svm_parameter svm_problem])
  (:use [clojure.java.io :only (reader)]
        clojure.java.shell
        clojure.test
        svm.core))

(def example-dataset (read-dataset "test-resources/heartscale"))

(def example-model (train-model example-dataset))

(def example-data
  [-1 {1 0.166667, 2 1.0, 3 1.0, 4 -0.132075, 5 -0.69863, 6 -1.0, 7 -1.0, 8 0.175573, 9 -1.0, 10 -0.870968, 12 -1.0, 13 0.5}])

(def example-line
  "-1 1:0.166667 2:1 3:1 4:-0.132075 5:-0.69863 6:-1 7:-1 8:0.175573 9:-1 10:-0.870968 12:-1 13:0.5")

(deftest test-format-line
  (let [line (format-line example-data)]
    (is (= "-1 1:0.166667 2:1.0 3:1.0 4:-0.132075 5:-0.69863 6:-1.0 7:-1.0 8:0.175573 9:-1.0 10:-0.870968 12:-1.0 13:0.5" line))
    (is (= example-data (parse-line line)))))

(deftest test-heart-scale
  (is (= 0 (:exit (sh "svm-train" "test-resources/heartscale" "/tmp/heartscale.model"))))
  (is (= 0 (:exit (sh "svm-predict" "test-resources/heartscale" "/tmp/heartscale.model" "/tmp/output"))))
  (is (= (map #(Double/parseDouble %1) (line-seq (reader "/tmp/output")))
         (map #(predict example-model %1) (map last example-dataset)))))

(deftest test-sparse-map
  (is (= {1 5 3 7} (sparse-map [5 nil 7]))))

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
  (let [dataset (read-dataset "test-resources/heartscale")]
    (let [params (make-params dataset)]
      (is (instance? svm_parameter params))
      (is (= svm_parameter/C_SVC (.svm_type params)))
      (is (= svm_parameter/RBF (.kernel_type params)))
      (is (= 3 (.degree params)))
      (is (= 0.07692307692307693 (.gamma params)))
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
      (is (= 0.07692307692307693 (.gamma params)))
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

(deftest test-parse-line
  (let [[label data] (parse-line example-line)]
    (is (= -1 label))
    (is (= 0.166667 (get data 1)))
    (is (nil? (get data 11)))
    (is (= 0.5 (get data 13)))))

(deftest test-predict
  (are [index label]
    (is (= label (predict example-model (last (nth example-dataset index)))))
    0 1.0
    1 -1.0
    2 -1.0
    3 1.0))

(deftest test-read-dataset
  (let [dataset (read-dataset "test-resources/heartscale")]
    (is (= 270 (count dataset)))
    (is (every? #(= 2 (count %1)) dataset))
    (is (every? #(number? (first %1)) dataset))
    (is (every? #(map? (second %1)) dataset))))

(deftest test-read-model
  (let [filename "/tmp/heartscale.model"]
    (write-model example-model filename)
    (let [model (read-model filename)]
      (is (instance? svm_model model))
      (is (= (.l example-model) (.l model)))
      (is (= (.nr_class example-model) (.nr_class model))))))

(deftest test-write-model
  (is (= "/tmp/heartscale.model"
         (write-model example-model "/tmp/heartscale.model"))))

(deftest test-train-model
  (let [model (train-model example-dataset)]
    (is (instance? svm_model model))))

(deftest test-write-dataset
  (let [dataset (read-dataset "test-resources/heartscale")
        filename (write-dataset dataset "/tmp/heartscale")]
    (is (= "/tmp/heartscale" filename))
    (is (= dataset (read-dataset filename)))))
