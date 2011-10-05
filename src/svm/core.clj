(ns svm.core
  (:import [libsvm svm_parameter]))

(def svm-types
  {:c-svc svm_parameter/C_SVC
   :epsilon-svr svm_parameter/EPSILON_SVR
   :nu-svc svm_parameter/NU_SVC
   :nu-svr svm_parameter/NU_SVR
   :one-class svm_parameter/ONE_CLASS})

(def kernel-types
  {:linear svm_parameter/LINEAR
   :poly svm_parameter/POLY
   :pre-computed svm_parameter/PRECOMPUTED
   :rbf svm_parameter/RBF
   :sigmoid svm_parameter/SIGMOID})


