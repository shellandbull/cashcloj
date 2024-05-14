(defproject cashcloj "0.1.0-SNAPSHOT"
  :description "An exploration of the stock market using data science & ML tools"
  :url "https://github.com/shellandbull/cashcloj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.scicloj/kindly "4-beta4"]
                 [org.clojure/clojure "1.11.1"]
                 [scicloj/tablecloth "7.029.2"]
                 [org.scicloj/clay "2-beta8"]
                 [org.scicloj/noj "1-alpha34"]
                 [com.cnuernber/charred "1.033"]
                 [generateme/fastmath "2.4.0"]]
  :resource-paths ["data"]
  :repl-options {:init-ns cashcloj.core})
