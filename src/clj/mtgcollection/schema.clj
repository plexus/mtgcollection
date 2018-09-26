(ns mtgcollection.schema
  (:require [datomic.api :as api]
            [clj-time.core :as time]
            [clj-time.format :as time-fmt]
            [clj-time.coerce :as time-coerce]))

(def sets [#:db{:ident :set/name
                :valueType :db.type/string
                :cardinality :db.cardinality/one
                :unique :db.unique/identity
                :doc "A set's name"}
           #:db{:ident :set/code
                :valueType :db.type/string
                :cardinality :db.cardinality/one
                :unique :db.unique/identity
                :doc "A set's code"}
           #:db{:ident :set/releaseDate
                :valueType :db.type/instant
                :cardinality :db.cardinality/one
                :doc "A set's release date"}])

(def cards [#:db{:ident :card/name
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one
                 :fulltext true}
            #:db{:ident :card/types
                 :valueType :db.type/string
                 :cardinality :db.cardinality/many}
            #:db{:ident :card/type
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/subtypes
                 :valueType :db.type/string
                 :cardinality :db.cardinality/many}
            #:db{:ident :card/manaCost
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/cmc
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/rarity
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/colorIdentity
                 :valueType :db.type/string
                 :cardinality :db.cardinality/many}
            #:db{:ident :card/text
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one
                 :fulltext true}
            #:db{:ident :card/toughness
                 :valueType :db.type/float
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/multiverseid
                 :unique :db.unique/identity
                 :valueType :db.type/long
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/power
                 :valueType :db.type/float
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/flavor
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/artist
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :card/set
                 :valueType :db.type/ref
                 :cardinality :db.cardinality/one}])

(def users [#:db{:ident :user/handle
                 :unique :db.unique/value
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :user/password
                 :valueType :db.type/string
                 :cardinality :db.cardinality/one}
            #:db{:ident :user/collection
                 :valueType :db.type/ref
                 :cardinality :db.cardinality/many}])

(def collection [#:db{:ident :owned-card/card
                      :valueType :db.type/ref
                      :cardinality :db.cardinality/one}
                 #:db{:ident :owned-card/count
                      :valueType :db.type/long
                      :cardinality :db.cardinality/one}])

(defn setup-db-schema [db-uri]
  (let [conn (api/connect db-uri)]
    (api/transact conn (concat
                        sets
                        cards
                        users
                        collection))))
