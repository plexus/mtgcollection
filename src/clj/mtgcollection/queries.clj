(ns mtgcollection.queries
  (:require [datomic.api :as d]))

(def rules
  '[[(collection-cards ?uid ?cid)
     [?uid :user/collection ?oid]
     [?oid :owned-card/card ?cid]]])

(defn sets [db]
  (d/q
   '[:find [(pull ?sid [*]) ...]
     :in $
     :where [?sid :set/code _]]
   db))

(defn collection-cards [db uid]
  (d/q
   '[:find [(pull ?cid [* {:card/set [:set/code]} {:owned-card/_card [:owned-card/count]}]) ...]
     :in $ % ?uid
     :where
     (collection-cards ?uid ?cid)]
   db
   rules
   uid))

(defn collection-set-cards [db uid sets]
  (d/q
   '[:find [(pull ?cid [* {:card/set [:set/code]} {:owned-card/_card [:owned-card/count]}]) ...]
     :in $ % ?uid [?set ...]
     :where
     (collection-cards ?uid ?cid)
     [?cid :card/set ?sid]
     [?sid :set/code ?set]]
   db
   rules
   uid
   sets))

(comment
  (first (collection-cards (user/db) [:user/handle "aaa"]))

  (-> (collection-set-cards (user/db) [:user/handle "aaa"] ["ALL" ])
      first
      :owned-card/_card
      first
      :owned-card/count)





  )
