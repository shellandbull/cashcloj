(ns cashcloj.core
    (:require [scicloj.clay.v2.api :as clay]
            [clojure.string :as str]
            [cashcloj.utils :as util]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [scicloj.clay.v2.api :as clay]
            [scicloj.noj.v1.vis.hanami :as hanami]
            [scicloj.kindly.v4.kind :as kind]
            [aerial.hanami.templates :as ht]))

^{:kindly/hide-code true}
(kind/md ["
# NASDAQ & Cryptocurrency timeseries

This is my first exploration in the world of data science using the [Scicloj stack](https://scicloj.github.io/).
I decided to give it a go due to it's [interactive programming](https://docs.cider.mx/cider/usage/interactive_programming.html) capacities.
The concept was introduced to me during a [London Clojurians](https://londonclojurians.org/) talk [\"From data to insights: Clojure for data deep dive\"](https://www.youtube.com/watch?v=eUFf3-og_-Y).

This HTML document was rendered using [Clay](https://github.com/scicloj/clay), which evaluated a `.clj` file that behaves like a notebook

**What's the stack?**

- [Spacemacs](https://www.spacemacs.org/) as my text editor. It comes with plugins for [Clay](https://github.com/scicloj/clay) & [Clojure via Cider](https://github.com/clojure-emacs/cider)
- [Clojure](https://clojure.org/) A dynamically typed lisp dialect that calls the JVM home
- [Tablecloth](https://scicloj.github.io/tablecloth) a `dataframe` like manipulation library. Similar to Pandas
- [Clay](https://github.com/scicloj/clay) for data visualizations in a dynamic fashion

**What'll be doing**

I downloaded timeseries price data from all the instruments from [NASDAQ](https://www.nasdaq.com/) and the top 250 cryptocurrencies from [Binance](https://www.binance.com/). In this post we'll
perform some simple operations on the dataset, but in the future I hope to publish some more sophisticated ML studies alongside some richer visualisations

In this first exploration I will keep things extremely simple.

We'll visualise the data as a table, do some transformations and render some charts.

**How I'm doing it**

Spacemacs has packages for Clay & Cider. I can evaluate the code on my editor as I go and either have it pop onto a buffer or even render it's output as HTML.
"])

^{:kindly/hide-code true}
(kind/md ["
## Reading a dataset

It's really simple. Just invoke `tc/dataset` over a URL or filename and you'll immediately get a primitive in which is very
easy to operate on.

In this example I just select a few columns and keep the top 10.

I also added a function `to-kebab-case` which casts the datasets keys in `this-format`

[tablecloth documentation](https://scicloj.github.io/tablecloth/)
"])

(-> "data/nasdaq-instrument-list.csv"
  (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
  (tc/select-columns [:symbol :name])
  (tc/head 10))

^{:kindly/hide-code true}
(kind/md ["
Ordering is very easy too
"])

(-> "data/nasdaq-instrument-list.csv"
  (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
  (tc/order-by :volume)
  (tc/head 10))

^{:kindly/hide-code true}
(kind/md ["
I now know that I want to re-use `data/nasdaq-instrument-list.csv` so I'll extract it onto it's own expression via `def`.

Visualising information is very easy too. Because the US holds the vast majority of entries, I am applying a logarithmic scale to keep the sizes of the bars consistent, albeit the values are pretty skewed towards a few entries.
"])

(def nasdaq-instruments
  (-> "data/nasdaq-instrument-list.csv"
      (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})))

(-> nasdaq-instruments
  (tc/group-by [:country])
  (tc/aggregate {:count tc/row-count})
  (hanami/plot ht/bar-chart {
                             :X :country
                             :Y :count
                             :XTYPE :nominal
                             :WIDTH 1000
                             :YSCALE {:type "log"}}))

^{:kindly/hide-code true}
(kind/md ["
## Plotting data

[hanami](https://github.com/jsa-aerial/hanami) makes use of the [VEGA LITE](https://vega.github.io/vega-lite/) specification for plotting charts

It works seamlessly with `tc/dataset`. Look at how smooth this code is! I am:

- Parsing a CSV with time series data
- Adding 2 new columns `pct-change` and `highly-volatile`
- Plotting a point chart where highly volatile data points are highlighted in a different colour
"])

(-> "data/instruments/1INCH-USD.csv"
  (tc/dataset {:key-fn (comp keyword util/to-kebab-case)})
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

^{:kindly/hide-code true}
(kind/md ["
## Closing remarks

I initially thought this would be a how-to piece but it evolved into being more of a \"stack review\". My only points of comparison are to use Snowflake + SQL to plot, slice & dice. Or a more widely adopted solution like Jupyter, Numpy & others.

I find this stack to be oddly entising, it's just so simple and highly declarative, there's python interop and lazily evaluated
collections may mean its a lot less code to write than if I were using Numpy. Time will only tell

It would be nice to see `tc/dataset` being able to take in an SQL connection and manage long running queries elegantly straight from the text editor.

I'll be posting more in the coming days, stay tuned.
"])

