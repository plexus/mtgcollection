(defproject mtgcollection "0.1.0-SNAPSHOT"
  :description ""
  :url ""

  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/core.async "0.2.395"]
                 [com.datomic/datomic-pro "0.9.5544"]
                 [postgresql "9.3-1102.jdbc41"]
                 [cheshire "5.7.0"]
                 [clj-time "0.13.0"]
                 ;;[proto-repl "0.3.1"]
                 [environ "1.1.0"]]

  :plugins [[lein-environ "1.1.0"]]

  :jvm-opts ["-Ddatomic.txTimeoutMsec=300000"]

  :env {:datomic-uri
        ;; "datomic:dev://localhost:4334/mtgcollection"
        "datomic:sql://mtgcollection?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic"}

  :profiles {:dev {:dependencies [[refactor-nrepl "2.3.0-SNAPSHOT"]]}})
