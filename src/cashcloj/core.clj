(ns cashcloj.core
    (:require [scicloj.clay.v2.api :as clay]
            [clojure.string :as str]
            [tablecloth.api :as tc]
            [scicloj.clay.v2.api :as clay]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [scicloj.noj.v1.vis.hanami :as hanami]
            [aerial.hanami.templates :as ht]))

(defn to-kebab-case [s]
  (-> s
    (str/lower-case)
    (str/replace #" " "-")))


(-> "data/nasdaq-instrument-list.csv"
  (tc/dataset {:key-fn (comp keyword to-kebab-case)})
  (tc/select-columns [:symbol :name]))


(-> "data/nasdaq-instrument-list.csv"
  (tc/dataset {:key-fn (comp keyword to-kebab-case)})
  (tc/order-by :volume)
  (tc/head 10))

(def nasdaq-instruments
  (-> "data/nasdaq-instrument-list.csv"
      (tc/dataset {:key-fn (comp keyword to-kebab-case)})))

(-> nasdaq-instruments
  (tc/group-by [:country])
  (tc/aggregate {:count tc/row-count})
  (hanami/plot ht/bar-chart {
                             :X :country
                             :Y :count
                             :XTYPE :nominal
                             :WIDTH 1000
                             :YSCALE {:type "log"}}))

(-> "data/instruments/1INCH-USD.csv"
  (tc/dataset {:key-fn (comp keyword to-kebab-case)})
  (tc/map-columns :pct-change [:open :close] (fn [open close] (-> close
                                                                  (- open)
                                                                  (* 100))))
  (hanami/plot ht/point-chart {
                               :X :date
                               :Y :pct-change
                               :XTYPE :nominal
                               :WIDTH 1000
                               }))

;; TODO
;; 1. Fix scale of the chart. Make the dots coloured if they are above/below 3/-3
;; 2. Add any chart with an interactable slider
;; 3. Tips on staying tidy and organised

