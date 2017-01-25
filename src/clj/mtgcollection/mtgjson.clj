(ns mtgcollection.mtgjson
  (:require [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-fmt]
            [datomic.api :as api]))

(defn update-to-float [map key]
  (try
    (if (contains? map key)
      (update map key #(if (string? %) (Float. %) %)))
    (catch Exception e
      (prn (get map key)))))

(defn ?update [map key f & args]
  (if (contains? map key)
    (let [result (apply f (get map key) args)]
      (if (nil? result)
        (dissoc map key)
        (assoc map key result)))))

(def date-format (time-fmt/formatter "yyyy-MM-dd"))

(defn parse-date [date]
  (time-fmt/parse date-format date))

(defn extract-data [json-map prefix keys]
  (into {} (filter identity (for [[k v] json-map]
                              (if (and (some #{(keyword k)} keys) (not (nil? v)))
                                [(keyword (name prefix) k) v])))))

(defn set-record [set-data]
  #:set{:db/id (api/tempid :db.part/user)
        :name (get set-data "name")
        :code (get set-data "code")
        :releaseDate (-> set-data
                         (get "releaseDate")
                         parse-date
                         time-coerce/to-date)})

(defn card-record [set-id card]
  (-> card
      (extract-data :card [:name :types :type :manaCost :cmc :rarity
                           :colorIdentity :text :multiverseid :toughness
                           :power :flavor :artist])
      (?update :card/cmc str)
      (?update :card/power #(if (int? %) (float %)))
      (?update :card/toughness #(if (int? %) (float %)))
      (assoc :db/id (api/tempid :db.part/user)
             :card/set set-id)))

(defn set-tx-data [set-data]
  (let [set-record (set-record set-data)
        set-id (:db/id set-record)]
    (into [set-record]
          (map #(card-record set-id %) (get set-data "cards")))))
