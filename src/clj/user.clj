(ns user
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [datomic.api :as api]
            [environ.core :refer [env]]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.repl-api :as fw-repl-api]
            [figwheel-sidecar.system :as fw-sys]
            [garden-watcher.core :refer [new-garden-watcher]]
            [mtgcollection.mtgjson :as mtgjson]
            [mtgcollection.schema :as schema]
            [mtgcollection.system :refer [prod-system]]
            [reloaded.repl :refer [system]]
            [ring.middleware.reload :refer [wrap-reload]]
            [datomic.api :as d]))

(def go reloaded.repl/go)
(def reset reloaded.repl/reset)

(defn mtg-json-data []
  (-> "AllSets-x.json" io/resource io/reader json/parse-stream vals))

(defn load-mtgjson-into-datomic [db-uri]
  (let [conn (api/connect db-uri)]
    (doseq [json (mtg-json-data)]
      (let [set-data (mtgjson/set-tx-data json)]
        (println (get json "code") (get json "name"))
        (api/transact conn set-data)))))

(defn reset-db []
  (let [db-uri (env :datomic-uri)]
    #_(api/delete-database db-uri)
    #_(api/create-database db-uri)
    (schema/setup-db-schema db-uri)
    (load-mtgjson-into-datomic db-uri)))

(reset-db)

(defn dev-system []
  (-> (prod-system)
      (update-in [:middleware :middleware :middleware] conj wrap-reload)
      (assoc
       :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
       :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})
       :garden-watcher (new-garden-watcher ['mtgcollection.styles]))))

(defn cljs-repl
  ([]
   (cljs-repl nil))
  ([id]
   (when (get-in reloaded.repl/system [:figwheel-system :system-running] false)
     (fw-sys/cljs-repl (:figwheel-system reloaded.repl/system) id)
     (fw-repl-api/cljs-repl))))

(set-refresh-dirs "src" "dev")
(reloaded.repl/set-init! dev-system)

(defn conn []
  (:conn (:datomic reloaded.repl/system)))

(defn db []
  (d/db (conn)))

(defn q [qry & args]
  (apply d/q qry (db) args))
