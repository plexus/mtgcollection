(ns mtgcollection.queries
  (:require [datomic.api :as d]))

(def rules
  '[[(collection-cards ?uid ?cid)
     [?uid :user/collection ?oid]
     [?oid :owned-card/card ?cid]]])

(defn sets
  ([db codes]
   (map (partial d/entity db)
        (d/q
         '[:find [?sid ...]
           :in $ [?code ...]
           :where [?sid :set/code ?code]]
         db
         codes)))
  ([db]
   (map (partial d/entity db)
        (d/q
         '[:find [?sid ...]
           :in $
           :where [?sid :set/code _]]
         db))))

(defn all-cards [db]
  (d/q
   '[:find [(pull ?cid [* {:card/set [:set/code]} {:owned-card/_card [:owned-card/count]}]) ...]
     :in $
     :where [?cid :card/name]
     ]
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

  (time
   (doall
    (map (juxt :card/name :card/image-path)
         (mtgcollection.images/add-images
          (collection-set-cards (user/db) [:user/handle "aaa"] ["ICE"])))))

  )
