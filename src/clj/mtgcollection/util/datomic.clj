(ns mtgcollection.util.datomic
  (:require [datomic.api :as d]))

(defn transact-one
  "Transact tx and return record with real entity id."
  [conn tx]
  (let [tempid (d/tempid :db.part/user)
        tx (assoc tx :db/id tempid)
        post-tx @(d/transact conn [tx])
        db (:db-after post-tx)
        entid (d/resolve-tempid db (:tempids post-tx) tempid)]
    (assoc tx :db/id entid)))
