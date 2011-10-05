(ns svm.data
  (:use [clojure.string :only (split)]))

(defn parse-libsvm-line
  "Parse a LibSVM formatted text line."
  [line]
  (let [[label & data] (split line #"\s+")]
    [(Integer/parseInt label)
     (reduce
      (fn [data [index value]]
        (assoc data
          (Integer/parseInt index)
          (Double/parseDouble value)))
      {} (map #(split % #":") data))]))

