# SVM-CLJ

A LibSVM wrapper for Clojure.

## Installation

Via Clojars: http://clojars.org/svm-clj.

## Usage

```clojure
(use 'svm.core)

; Load the heart scale example dataset.
(def dataset (read-dataset "test-resources/heartscale"))

; Train a SVM model.
(def model (train-model dataset))

; Get the feature map you want to predict.
(def feature (last (first dataset)))
;=> {1 0.708333, 2 1.0, 3 1.0, ...}

; Label it.
(predict model feature)
;=> 1.0
```

## License

Copyright (C) 2011 Roman Scherer
Distributed under the Eclipse Public License, the same as Clojure.
