(ns svm.core
  (:refer-clojure :exclude (replace))
  (:import [libsvm svm_node svm_parameter svm_problem svm])
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [file reader writer]]
            [clojure.string :refer [join replace split]]))

(def kernel-types
  {:linear svm_parameter/LINEAR
   :poly svm_parameter/POLY
   :pre-computed svm_parameter/PRECOMPUTED
   :rbf svm_parameter/RBF
   :sigmoid svm_parameter/SIGMOID})

(def svm-types
  {:c-svc svm_parameter/C_SVC
   :epsilon-svr svm_parameter/EPSILON_SVR
   :nu-svc svm_parameter/NU_SVC
   :nu-svr svm_parameter/NU_SVR
   :one-class svm_parameter/ONE_CLASS})

(def default-params
  {:C 1
   :cache-size 100
   :coef0 0
   :degree 3
   :eps 1e-3
   :gamma 0
   :kernel-type (:rbf kernel-types)
   :nr-weight 0
   :nu 0.5
   :p 0.1
   :probability 0
   :shrinking 1
   :svm-type (:c-svc svm-types)
   :weight (double-array 0)
   :weight-label (int-array 0)})

(defn- max-features [dataset]
  (apply max (mapcat (comp keys last) dataset)))

(defn format-line
  "Format data as a LibSVM text line."
  [[label data]]
  (str label " " (->> (sort data) (map #(join ":" %)) (join " "))))

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

(defn make-params
  "Make trainer params from `dataset` and `options`."
  [dataset & {:as options}]
  (let [params (svm_parameter.)]
    (doseq [[key val] (merge default-params options)]
      (clojure.lang.Reflector/setInstanceField params (replace (name key) "-" "_") val))
    (set! (.gamma params) (/ 1.0 (max-features dataset)))
    params))

(defn sparse-map
  "Returns a sparse map of features from seq.  The map will be suitable
  as the second entry in a dataset vector.  seq should have length
  equal to the total number of features in the dataset and set empty
  values to nil.

  Ex. (sparse-map [5 nil 7]) -> {1 5, 3 7}"
  [seq]
  (->> seq
       (map vector (rest (range)))
       (keep (fn [[i v]] (when v [i v])))
       (into (sorted-map))))

(defn make-problem
  "Make a LibSVM problem from `dataset`."
  [dataset]
  (let [problem (svm_problem.)]
    (set! (. problem l) (count dataset))
    (set! (. problem y) (double-array (map first dataset)))
    (set! (. problem x) (into-array (map into-array (map make-nodes dataset))))
    problem))

(defn parse-line
  "Parse a LibSVM formatted text `line`."
  [line]
  (let [[label & data] (split line #"\s+")]
    [(edn/read-string label)
     (reduce
      (fn [data [index value]]
        (assoc data
          (Integer/parseInt index)
          (Double/parseDouble value)))
      (sorted-map) (map #(split % #":") data))]))

(defn predict
  "Predict the label of the `feature` with `model`."
  [model feature]
  (->> (make-nodes [nil feature])
       (into-array)
       (svm/svm_predict model)))

(defn read-dataset
  "Read the dataset in LibSVM format from `url`."
  [url] (map parse-line (line-seq (reader url))))

(defn read-model
  "Read the SVM model in LibSVM format from `filename`."
  [filename] (svm/svm_load_model filename))

(defn train-model
  "Train a SVM model with `dataset` according to `options`."
  [dataset & options]
  (let [problem (make-problem dataset)
        params (apply make-params dataset options)]
    (svm/svm_check_parameter problem params)
    (svm/svm_train problem params)))

(defn write-dataset
  "Write the `dataset` in LibSVM format to `filename`."
  [dataset filename]
  (let [file (file filename)]
    (.mkdirs (.getParentFile file))
    (with-open [w (writer file)]
      (doseq [line (map format-line dataset)]
        (.write w (str line "\n"))))
    (str file)))

(defn write-model
  "Write the `model` in LibSVM format to `filename`."
  [model filename]
  (svm/svm_save_model filename model)
  filename)
