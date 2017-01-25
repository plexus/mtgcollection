(ns user
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [datomic.api :as api]
            [environ.core :refer [env]]
            [mtgcollection.mtgjson :as mtgjson]
            [mtgcollection.schema :as schema]))

(defn mtg-json-data []
  (-> "AllSets-x.json" io/resource io/reader json/parse-stream))

(defn insert-set-and-card-data [conn sets]
  (->> sets
       (map mtgjson/set-tx-data)
       (apply concat)
       (api/transact conn)))

(defn reset-db []
  (let [db-uri (env :datomic-uri)]
    (api/delete-database db-uri)
    (api/create-database db-uri)
    (schema/setup-db-schema db-uri)
    (let [conn (api/connect db-uri)]
      (->> (mtg-json-data)
           vals
           (insert-set-and-card-data conn)))))
