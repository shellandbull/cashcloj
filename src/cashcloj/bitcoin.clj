(ns cashcloj.core
  (:require [scicloj.clay.v2.api :as clay]
            [clojure.string :as str]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [cashcloj.utils :as util]
            [scicloj.clay.v2.api :as clay]
            [scicloj.noj.v1.vis.hanami :as hanami]
            [tech.v3.dataset.math :as math]
            [scicloj.kindly.v4.kind :as kind]
            [aerial.hanami.templates :as ht]))

(def btc-usd
  (-> "data/instruments/BTC-USD.csv"
      (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
      (tc/map-columns :pct-change [:open :close] (fn [open close] (-> close
                                                                      (- open)
                                                                      (/ open)
                                                                      (* 100))))
      (tc/map-columns :highly-volatile [:pct-change] #(if (or (tcc/> % 6.0) (tcc/< % -6.0) ) 1 0))))

(-> btc-usd
  (tc/separate-column :date [:day :month :year] (fn [date] [(.getDayOfMonth date) (.getMonth date) (.getYear date)]))
  (math/correlation-table) (get :highly-volatile))
