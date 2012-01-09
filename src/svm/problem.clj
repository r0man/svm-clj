(ns svm.problem
  (:refer-clojure :exclude (replace))
  (:import [libsvm svm_node svm_parameter svm_problem svm])
  (:use [clojure.string :only (replace)]
        svm.data))

(def default-params
  {:C 1
   :cache-size 100
   :coef0 0
   :degree 3
   :eps 1e-3
   :gamma 0
   :kernel-type svm_parameter/RBF
   :nr-weight 0
   :nu 0.5
   :p 0.1
   :probability 0
   :shrinking 1
   :svm-type svm_parameter/C_SVC
   :weight (double-array 0)
   :weight-label (int-array 0)})

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

(defn make-params [& {:as options}]
  (let [params (svm_parameter.)]
    (doseq [[key val] (merge default-params options)]
      (clojure.lang.Reflector/setInstanceField params (replace (name key) "-" "_") val))
    params))

(defn make-problem
  "Make a LibSVM problem."
  [dataset]
  (let [problem (svm_problem.)]
    (set! (. problem l) (count dataset))
    (set! (. problem y) (double-array (map first dataset)))
    (set! (. problem x) (into-array (map into-array (map make-nodes dataset))))
    problem))

;; TODO: Obsolete?
(defn read-problem
  "Read the dataset from url and make a SVM problem."
  [url] (make-problem (read-dataset url)))

(defn train-model
  "Train a model with dataset according to options."
  [dataset & options]
  (let [problem (make-problem dataset)
        params (apply make-params options)]
    (svm/svm_check_parameter problem params)
    (svm/svm_train problem params)))

(defn save-model
  "Save the model to filename."
  [model filename]
  (svm/svm_save_model filename model)
  filename)
