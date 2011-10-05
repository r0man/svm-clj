(ns svm.data
  (:use [clojure.string :only (join split)]))

(defn format-libsvm-line
  "Format data as a LibSVM text line."
  [[label data]]
  (str label " " (->> (sort data) (map (partial join ":")) (join " "))))

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
      (sorted-map) (map #(split % #":") data))]))

