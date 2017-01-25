(ns user
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [datomic.api :as api]
            [environ.core :refer [env]]

            [mtgcollection.mtgjson :as mtgjson]
            [mtgcollection.schema :as schema]
            [mtgcollection.application :as app]

            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.system :as fw-sys]
            [garden-watcher.core :refer [new-garden-watcher]]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [reloaded.repl :refer [system init start stop go reset reset-all]]

            [com.stuartsierra.component :as component]))

(defn- mtg-json-data []
  (-> "AllSets-x.json" io/resource io/reader json/parse-stream vals))

(defn load-mtgjson-into-datomic [db-uri]
  (let [conn (api/connect db-uri)]
    (doseq [json (mtg-json-data)]
      (let [set-data (mtgjson/set-tx-data json)]
        (println (get json "code") (get json "name"))
        (api/transact conn set-data)))))

(defn reset-db []
  (let [db-uri (env :datomic-uri)]
    (api/delete-database db-uri)
    (api/create-database db-uri)
    (schema/setup-db-schema db-uri)
    (load-mtgjson-into-datomic db-uri)))

(defn dev-system []
  (merge
   (app/app-system)
   (component/system-map
    :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
    :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})
    :garden-watcher (new-garden-watcher ['mtgcollection.styles]))))

(set-refresh-dirs "src" "dev")
(reloaded.repl/set-init! #(dev-system))
