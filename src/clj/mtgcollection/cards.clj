(ns mtgcollection.cards
  (:require [datomic.api :as api]))

(defn random-card [conn]
  (let [db (api/db conn)
        card-id (first (api/q '[:find (sample 1 ?eid) .
                                :where [?eid :card/name _]] db))
        card-data (api/pull db '[*] card-id)
        set-id (-> card-data :card/set :db/id)
        set-data (api/pull db '[*] set-id)]
    (merge card-data set-data)))

(defn find-card [db name set]
  (api/q '[:find ?eid .
           :in $ ?name ?set
           :where
           [?eid :card/name ?name]
           [?eid :card/set ?sid]
           (or [?sid :set/code ?set]
               [?sid :set/name ?set])] db name set))

(comment

  (find-card (api/db (user/datomic)) "Dark Ritual" "4ED")
  (find-card (api/db (user/datomic)) "Crypt Rats" "Visions")
  (find-card (api/db (user/datomic)) "Crypt Rats" "VIS")


  (api/q '[:find [[?eid]]
           :in $ ?name ?edition
           :where [?eid :card/name name]] db name edition)

  (api/q '[:find ?name
           :where
           [?eid :card/name ?name]
           [(fulltext $ :card/name "Lightning") [[?eid]]]
           [(fulltext $ :card/name "Bolt") [[?eid]]]] (api/db (user/datomic)))

  (api/q '[:find ?name
           :where
           [?eid :card/name ?name]
           [?eid :card/set ?sid]
           [?sid :set/code "ICE"]] (api/db (user/datomic)))

  (api/q '[:find ?name
           :where
           [?eid :card/multiverseid 209]
           [?eid :card/name ?name]] (api/db (user/datomic)))


  (:card/name (random-card (user/datomic)))


  (api/q '[:find [(sample 1 ?eid)] :where [?eid :card/name _]] (api/db (user/datomic)))

  (api/q '[:find ?eid
           :where
           [?eid :card/name "Dark Ritual"]] (api/db (user/datomic)))

  )
