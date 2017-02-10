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
    (map
     (fn [csv-row]
       (let [count   (Integer. (nth csv-row count-pos))
             name    (nth csv-row name-pos)
             edition (nth csv-row edition-pos)
             card-id (find-card db name edition)]
         (if card-id
           [count card-id]
           {:not-found csv-row})))
     parsed-csv)))

(defn collection-card-tx [user-id [count card-id]]
  (let [owned-card-id (d/tempid :db.part/user)]
    {:db/id owned-card-id
     :owned-card/card card-id
     :owned-card/count count}))

(defn sum-same-cards [card-vectors]
  (->> card-vectors
       (reduce (fn [m [count card-id]]
                 (update m card-id #(+ (or % 0) count))) {})
       (map (juxt val key))))

(defn import-csv
  "Returns a two element map: tx is the datomic transaction, :not-found are the
  rows that couldn't be resolved"
  [db user-id parsed-csv]
  (let [card-ids (find-cards-from-csv db parsed-csv)
        not-found (->> card-ids
                       (filter :not-found)
                       (map :not-found))
        card-ids (sum-same-cards (filter vector? card-ids))
        card-txs (map #(collection-card-tx user-id %) card-ids)
        owned-card-ids (map :db/id card-txs)]
    (prn card-ids)
    {:txs (conj
           card-txs
           {:db/id user-id
            :user/collection owned-card-ids})
     :not-found not-found}))

;; (let [db (d/db (user/datomic))
;;       uid 17592186078330
;;       csv [[1 "Lightning Bolt" nil "4ED"]
;;            [3 "Dark Ritual" nil "ICE"]]]
;;   #_(find-cards-from-csv db csv)
;;   (import-csv db uid csv))





#_(d/db (:conn (user/datomic)))


;; (user/q '[:find ?user-id .
;;           :where
;;           [?user-id :user/handle "arne"]
;;           ])


#_
(user/q '[:find ?name ?sname
          :where
          [?user-id :user/handle "arne"]
          [?user-id :user/owned-card ?card-id]
          [?card-id :card/name ?name]
          [?card-id :card/name "Dark Ritual"]
          [?card-id :card/set ?sid]
          [?sid :set/name ?sname]
          ])
