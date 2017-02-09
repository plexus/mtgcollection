(defproject mtgcollection "0.1.0-SNAPSHOT"
  :description ""
  :url ""

  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293" :scope "provided"]
                 [com.cognitect/transit-clj "0.8.297"]
                 [org.clojure/core.async "0.2.395"]

                 [com.datomic/datomic-pro "0.9.5544"]
                 [postgresql "9.3-1102.jdbc41"]

                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-middleware-format "0.7.0"]
                 [compojure "1.5.2"]

                 [cheshire "5.7.0"]
                 [clj-time "0.13.0"]
                 [environ "1.1.0"]

                 [com.stuartsierra/component "0.3.1"]
                 [org.danielsz/system "0.3.1"]
                 [org.clojure/tools.namespace "0.2.11"]

                 [reagent "0.6.0"]
                 [re-frame "0.9.1"]
                 [cljs-ajax "0.5.8"]
                 [day8.re-frame/http-fx "0.1.3"]]


  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.5"]]

  :source-paths ["src/clj" "src/cljc"]

  :test-paths ["test/clj" "src/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :uberjar-name "mtgcollection.jar"

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc"]

                :figwheel {:on-jsload "mtgcollection.core/mount-root"}

                :compiler {:main mtgcollection.core

                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/mtgcollection.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main mtgcollection.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main mtgcollection.core
                           :output-to "resources/public/js/compiled/mtgcollection.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :closure-defines {goog.DEBUG false}
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]
             :server-logfile "log/figwheel.log"
             :ring-handler user/dev-ring-handler}

  :jvm-opts ["-Ddatomic.txTimeoutMsec=300000"]

  :env {:datomic-uri
        ;; "datomic:dev://localhost:4334/mtgcollection"
        "datomic:sql://mtgcollection?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic"}

  :profiles
  {:dev
   {:dependencies [[figwheel "0.5.8"]
                   [figwheel-sidecar "0.5.8"]
                   [lambdaisland/garden-watcher "0.2.0"]
                   [com.cemerick/piggieback "0.2.1"]
                   [refactor-nrepl "2.3.0-SNAPSHOT"]
                   [reloaded.repl "0.2.3"]]}})
