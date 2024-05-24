(ns cashcloj.bitcoin

  (:require [scicloj.clay.v2.api :as clay]
            [tablecloth.api :as tc]
            [java-time :as jt]
            [charred.api :as charred]
            [cashcloj.utils :as util]
            [tech.v3.dataset.math :as math]
            [scicloj.kindly.v4.kind :as kind])
  (:import
   (java.util Properties)
   (edu.stanford.nlp.pipeline StanfordCoreNLP)
   (edu.stanford.nlp.ling CoreAnnotations CoreAnnotations$SentencesAnnotation)
   (edu.stanford.nlp.pipeline Annotation)
   (edu.stanford.nlp.neural.rnn RNNCoreAnnotations)
   (edu.stanford.nlp.sentiment SentimentCoreAnnotations
                               SentimentCoreAnnotations$SentimentAnnotatedTree
                               SentimentCoreAnnotations$SentimentClass)))

(def pipeline
  (let [props (doto (Properties.)
                (.setProperty "annotators" "tokenize, ssplit, parse, sentiment"))]
    (StanfordCoreNLP. props)))

(defn estimate-sentiment [text]
  (let [annotation (.process pipeline text)]
    (map (fn [sentence]
           (let [tree (.get sentence SentimentCoreAnnotations$SentimentAnnotatedTree)]
             {:sentence sentence
              :sentiment (.get sentence SentimentCoreAnnotations$SentimentClass)
              :sentiment-int (RNNCoreAnnotations/getPredictedClass tree)}))
         (.get annotation CoreAnnotations$SentencesAnnotation))))

(defn parse-json-timestamp [date-string]
  (jt/local-date (jt/formatter "yyyy-MM-dd'T'HH:mm:ssX") date-string))

(def btc-sentiment
  "A JSON file that is populated out of News API with the word bitcoin used as criteria"
  (-> "data/sentiments/btc.json"
      (slurp)
      (charred/read-json)
      (get "articles")
      (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
      (tc/convert-types :publishedat [:date parse-json-timestamp])
      ( tc/head 4)))

btc-sentiment

(def btc-usd
  "BTC-USD timeseries data"
  (-> "data/instruments/BTC-USD.csv"
      (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
      (util/add-pct-change)
      (util/add-highly-volatile)
      (tc/add-column :description (:description btc-sentiment) :cycle)
      (tc/add-column :description (:description btc-sentiment) :cycle)))

(def sample-date "2024-05-17T17:50:07Z")

(-> sample-date
    (jt/zoned-date-time))

(comment
  (->> ["This is an excellent book."
        "I enjoy reading it."
        "I can read on Sundays."
        "Today is only Tuesday. I want it to be Friday."
        "Can't wait for next Sunday."
        "The working week is unbearably long."
        "It's awful."]
       (mapcat estimate-sentiment)
       tc/dataset))
