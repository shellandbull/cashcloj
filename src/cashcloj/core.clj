(ns cashcloj.core
    (:require [scicloj.clay.v2.api :as clay]
            [clojure.string :as str]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [scicloj.clay.v2.api :as clay]
            [scicloj.noj.v1.vis.hanami :as hanami]
            [scicloj.kindly.v4.kind :as kind]
            [aerial.hanami.templates :as ht]))


^{:kindly/hide-code true}
(kind/md ["
# [Scicloj](https://scicloj.github.io/) explorations part 1 | NASDAQ & Cryptocurrency timeseries

This is my first exploration in the world of data science under the hand of Scicloj.
I decided to give this stack a try due to its [interactive programming](https://docs.cider.mx/cider/usage/interactive_programming.html) capacities.
This concept was introduced to me during a [London Clojurians](https://londonclojurians.org/) talk [\"From data to insights: Clojure for data deep dive\"](https://www.youtube.com/watch?v=eUFf3-og_-Y).

This file you are looking at is rendered using [Clay](https://github.com/scicloj/clay).

**What's the stack?**

- [Spacemacs](https://www.spacemacs.org/) for my text editor as it comes with plugins for [Clay](https://github.com/scicloj/clay) & [Cider](https://github.com/clojure-emacs/cider)
- [Clojure](https://clojure.org/) A dynamically typed lisp dialect that calls the JVM home
- [Tablecloth](https://scicloj.github.io/tablecloth) a `dataframe` like manipulation library. Similar to Pandas
- [Clay](https://github.com/scicloj/clay) for data visualizations in a dynamic fashion

**What'll be doing**

I downloaded all the instruments from [NASDAQ](https://www.nasdaq.com/) and the top 250 cryptocurrencies from [Binance](https://www.binance.com/). We are going to
go ahead and explore these datasets.

For the purposes of this first exploration I will keep things extremely simple. We'll visualise the data as a table, perform simple transformations & plot it onto a few simple charts.

**How I'm doing it**

With some help & documentation I have set up my spacemacs so I can cast functions onto either a buffer or Clay.
This way I can immediately get feedback on each single function I evaluate.
"])

(kind/md ["
## Reading a dataset

It's really simple just invoke `tc/dataset` over a URL or filename and you'll immediately get a primitive in which is very
easy to operate on.

In this example I just select a few columns and keep the top 50
"])

(defn to-kebab-case [s]
  (-> s
      (str/lower-case)
      (str/replace #" " "-")))

(-> "data/nasdaq-instrument-list.csv"
    (tc/dataset {:key-fn (comp keyword to-kebab-case)})
    (tc/select-columns [:symbol :name])
    (tc/head 50))

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
    (tc/head 100))

(-> "data/instruments/1INCH-USD.csv"
  (tc/dataset {:key-fn (comp keyword to-kebab-case)})
  (tc/map-columns :pct-change [:open :close] (fn [open close] (-> close
                                                                  (- open)
                                                                   (* 100))))
  (tc/map-columns :highly-volatile [:pct-change] #(or (tcc/> % 6.0) (tcc/< % -6.0)))
  (hanami/plot ht/point-chart {
                               :X     :date
                               :Y     :pct-change
                               :XTYPE :temporal
                               :WIDTH 1000
                               :COLOR {:field :highly-volatile}
                               }))

