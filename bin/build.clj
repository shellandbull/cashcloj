(ns build
  (:require
   [aerial.hanami.common :as hc]
   [scicloj.clay.v2.api :as clay]))

(defn build-book []
  (swap! hc/_defaults
         assoc
         :BACKGROUND "white")

  (clay/make!
    {:show             false
     :run-quarto       false
     :format           [:quarto :html]
     :book             {:title "Cashcloj | Data Science in Clojure"}
     :base-source-path "src"
     :base-target-path "docs"
     :subdirs-to-sync  ["src" "data"]
     :source-path      ["index.clj"
                   "cashcloj/core.clj"]}))

(defn build-cli [_]
  (build-book)
  (System/exit 0))