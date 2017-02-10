(ns mtgcollection.user
  (:require [datomic.api :as api]))

(defn find-user-for-login [db handle password]
  (api/q '{:find [[?e]]
           :in [$ ?handle ?password]
           :where [[?e :user/handle ?handle]
                   [?e :user/password ?enc]
                   [(buddy.hashers/check ?password ?enc)]]}
         db handle password))
