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

(defn make-params [options]
  (let [params (svm_parameter.)]
    (set! (.svm_type params) (or (:svm-type options) svm_parameter/C_SVC))
    (set! (.kernel_type params) (or (:kernel-type options) svm_parameter/RBF))
    (set! (.degree params) (or (:degree options) 3))
    (set! (.gamma params) (or (:gamma options) 0))
    (set! (.coef0 params) (or (:coef0 options) 0))
    (set! (.nu params) (or (:nu options) 0.5))
    (set! (.cache_size params) (or (:cache-size options) 100))
    (set! (.C params) (or (:c options) 1))
    (set! (.eps params) (or (:eps options) 1e-3))
    (set! (.p params) (or (:p options) 0.1))
    (set! (.shrinking params) (or (:shrinking options) 1))
    (set! (.probability params) (or (:probability options) 0))
    (set! (.nr_weight params) (or (:nr-weight options) 0))
    (set! (.weight params) (or (:weight options) (double-array 0)))
    (set! (.weight_label params) (or (:weight-label options) (int-array 0)))
    params))

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

(defn read-problem
  "Read the dataset from url and make a SVM problem."
  [url] (make-problem (read-dataset url)))

(defn train-model
  "Train a problem according to options."
  [problem & options]
  (let [params (apply make-params options)]
    (svm/svm_check_parameter problem params)
    (svm/svm_train problem params)))

(defn save-model
  "Save the model to filename."
  [model filename]
  (svm/svm_save_model filename model)
  filename)
