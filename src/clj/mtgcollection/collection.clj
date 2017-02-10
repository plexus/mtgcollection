(ns mtgcollection.collection
  (:require [mtgcollection.cards :refer [find-card]]
            [datomic.api :as d]))

(defn find-cards-from-csv
  "Returns a list, each entry either a card id, or {:not-found <original row>}"
  [db parsed-csv]
  (let [header-row (first parsed-csv)
        data (rest parsed-csv)
        count-pos 0
        name-pos 1
        edition-pos 3]
    (mapcat
     (fn [csv-row]
       (let [count   (nth csv-row count-pos)
             name    (nth csv-row name-pos)
             edition (nth csv-row edition-pos)
             card-id (find-card db name edition)]
         (repeat (Integer. count)
                 (if card-id
                   card-id
                   {:not-found csv-row}))))
     parsed-csv)))

(defn import-csv
  "Returns a two element map: tx is the datomic transaction, :not-found are the
  rows that couldn't be resolved"
  [db user-id parsed-csv]
  (let [card-ids (find-cards-from-csv db parsed-csv)
        not-found (->> card-ids
                       (filter :not-found)
                       (map :not-found))
        card-ids (filter integer? card-ids)]
    {:tx {:db/id user-id
          :user/owned-card card-ids}
     :not-found not-found}))




#_
(user/q '[:find ?name ?sname
          :where
          [?uid :user/handle "arne"]
          [?uid :user/owned-card ?cid]
          [?cid :card/name ?name]
          [?cid :card/name "Dark Ritual"]
          [?cid :card/set ?sid]
          [?sid :set/name ?sname]
          ])
