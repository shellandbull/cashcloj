(ns cashcloj.utils
  (:require [clojure.string :as str]))

(defn to-kebab-case [s]
  (-> s
      (str/lower-case)
      (str/replace #" " "-")))

