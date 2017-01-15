(ns mtgcollection.core
  (:require [cheshire.core :refer :all]
            [clojure.java.io :as io]
            [datomic.api :as api]
            [clj-time.core :as time]
            [clj-time.format :as time-fmt]
            [clj-time.coerce :as time-coerce]))

(def db-uri "datomic:dev://localhost:4334/mtgcollection")

(def conn (api/connect db-uri))

(def date-format (time-fmt/formatter "yyyy-MM-dd"))

(defn parse-date [date]
  (time-fmt/parse date-format date))

(defn insert-set-and-card-data [conn card-data]
  (doseq [[set-code set-data] card-data]
    (api/transact conn [{:db/id #db/id[:db.part/user]
                         :set/name (get set-data "name")
                         :set/code (get set-data "code")
                         :set/releaseDate (-> set-data
                                              (get "releaseDate")
                                              parse-date
                                              time-coerce/to-date)}])

    (doseq [card (get set-data "cards")]
      (api/transact conn [#:card{:db/id #db/id[:db.part/user]
                                 :set [:set/code (get set-data "code")]
                                 :name (get card "name")
                                 :types (get card "types")
                                 :type (get card "type")
                                 :manaCost (get card "manaCost")
                                 :cmc (get card "cmc")
                                 :rarity (get card "rarity")
                                 :colorIdentity (get card "colorIdentity")
                                 :text (get card "text")
                                 :multiverseid (get card "multiverseid")
                                 :toughness (get card "toughness")
                                 :power (get card "power")
                                 :flavor (get card "flavor")
                                 :artist (get card "artist")}]))))

(def json-data (-> "AllSets-x.json"
                   io/resource
                   io/reader
                   parse-stream))

(comment
  (insert-set-and-card-data conn
                            (-> "AllSets-x.json"
                                io/resource
                                io/reader
                                parse-stream))

  (api/q '[:find ?entity ?name ?tx ?score
           :in $
           :where
           [(fulltext $ :card/name "Hand") [[?entity ?name ?tx ?score]]]]
         (api/db conn))

  (api/q '[:find ?name
           :in $
           :where
           [?e :card/name ?name]]
         (api/db conn))


)
