(ns cashcloj.bitcoin
  (:require [scicloj.clay.v2.api :as clay]
            [tablecloth.api :as tc]
            [java-time :as jt]
            [charred.api :as charred]
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
  (-> "data/sentiments/btc.json"
      (slurp)
      (charred/read-json)
      (get "articles")
      (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
      (tc/convert-types :publishedat [:string jt/zoned-date-time])
      (tc/info))
)

(def sample-date "2024-05-17T17:50:07Z")

(-> sample-date
    (jt/zoned-date-time))

(def timeseries-with-sentiment
  )
