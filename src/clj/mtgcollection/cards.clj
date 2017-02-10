(ns mtgcollection.cards
  (:require [datomic.api :as api]))

(defn random-card [conn]
  (let [db (api/db conn)
        card-id (ffirst (api/q '[:find [(sample 1 ?eid)] :where [?eid :card/name _]] db))
        card-data (api/pull db '[*] card-id)
        set-id (-> card-data :card/set :db/id)
        set-data (api/pull db '[*] set-id)]
    (merge card-data set-data)))
