(ns mtgcollection.cards
  (:require [datomic.api :as d]))

(defn random-card [conn]
  (let [db (d/db conn)
        card-id (first (d/q '[:find (sample 1 ?eid) .
                              :where [?eid :card/name _]] db))
        card-data (d/pull db '[*] card-id)
        set-id (-> card-data :card/set :db/id)
        set-data (d/pull db '[*] set-id)]
    (merge card-data set-data)))

(defn find-card [db name set]
  (d/q '[:find ?eid .
         :in $ ?name ?set
         :where
         [?eid :card/name ?name]
         [?eid :card/set ?sid]
         (or [?sid :set/code ?set]
             [?sid :set/name ?set])] db name set))

(comment

  (find-card (d/db (user/datomic)) "Dark Ritual" "4ED")
  (find-card (d/db (user/datomic)) "Crypt Rats" "Visions")
  (find-card (d/db (user/datomic)) "Crypt Rats" "VIS")

  (let [db (user/db)]
    (:card/subtypes (d/entity db (find-card db "Crypt Rats" "Visions"))))

  (d/q '[:find [[?eid]]
         :in $ ?name ?edition
         :where [?eid :card/name name]] db name edition)

  (d/q '[:find ?name
         :where
         [?eid :card/name ?name]
         [(fulltext $ :card/name "Lightning") [[?eid]]]
         [(fulltext $ :card/name "Bolt") [[?eid]]]] (d/db (user/datomic)))

  (d/q '[:find ?name
         :where
         [?eid :card/name ?name]
         [?eid :card/set ?sid]
         [?sid :set/code "ICE"]] (d/db (user/datomic)))

  (d/q '[:find ?name
         :where
         [?eid :card/multiverseid 209]
         [?eid :card/name ?name]] (d/db (user/datomic)))


  (:card/name (random-card (user/datomic)))


  (d/q '[:find [(sample 1 ?eid)] :where [?eid :card/name _]] (d/db (user/datomic)))

  (d/q '[:find ?eid
         :where
         [?eid :card/name "Dark Ritual"]] (d/db (user/datomic)))

  )
