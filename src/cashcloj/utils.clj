(ns cashcloj.utils
  (:require [clojure.string :as str]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]))

(defn to-kebab-case [s]
  "replaces whitespaces in a string for the character \"-\" lowercases the entire string"
  (-> s
      (str/lower-case)
      (str/replace #" " "-")))

(defn add-pct-change [dataset]
  "Adds a new column. Uses the open and close columns to compute the percentual change of the day"
  (-> dataset
      (tc/map-columns :pct-change [:open :close] (fn [open close] (-> close
                                                                      (- open)
                                                                      (/ open)
                                                                      (* 100))))))

(defn add-highly-volatile [dataset]
  "adds a new column by looking at the value of the column :pct-change. if pct-change is greater than 6.0 returns 1, if its less than 6.0 returns zero"
  (-> dataset
      (tc/map-columns :highly-volatile [:pct-change] #(if (or (tcc/> % 6.0) (tcc/< % -6.0)) 1 0))))
