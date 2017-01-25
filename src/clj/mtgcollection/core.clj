(ns mtgcollection.core
  (:require [datomic.api :as api]))

(comment
  (api/q '[:find ?name ?manaCost ?type ?text
           :in $
           :where
           [(fulltext $ :card/text "graveyard") [[?entity]]]
           [?entity :card/name ?name]
           [?entity :card/manaCost ?manaCost]
           [?entity :card/type ?type]
           [?entity :card/text ?text]]
         (api/db conn))

  (api/q '[:find ?name
           :in $
           :where
           [76 :db/ident ?name]]
         (api/db conn))





)
