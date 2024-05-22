(ns cashcloj.bitcoin
  (:require [scicloj.clay.v2.api :as clay]
            [tablecloth.api :as tc]
            [java-time :as jt]
            [cheshire.core :as json]
            [cashcloj.utils :as util]
            [tech.v3.dataset.math :as math]
            [scicloj.kindly.v4.kind :as kind]))

(defn parse-json-timestamp [date-string]
  (jt/offset-date-time (jt/formatter "yyyy-MM-dd'T'HH:mm:ssX") date-string))

(def btc-usd
  "BTC-USD timeseries data"
  (-> "data/instruments/BTC-USD.csv"
      (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
      (util/add-pct-change)
      (util/add-highly-volatile)))

(def btc-sentiment
  "A JSON file that is populated out of News API with the word bitcoin used as criteria"
  (as-> "data/sentiments/btc.json" ds
    (slurp ds)
    (json/parse-string ds)
    (tc/dataset ds {:key-fn (comp keyword util/to-kebab-case)})
    (tc/select-columns ds [:title :publishedat])
    (tc/map-columns ds
      :date
      (tc/column-names ds :publishedat)
      parse-json-timestamp)
  )
)

