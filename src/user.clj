(ns user
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [datomic.api :as api]
            [environ.core :refer [env]]
            [mtgcollection.mtgjson :as mtgjson]
            [mtgcollection.schema :as schema]))

(defn- mtg-json-data []
  (-> "AllSets-x.json" io/resource io/reader json/parse-stream vals))

(defn- load-mtgjson-into-datomic []
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
    (load-mtgjson-into-datomic)))
