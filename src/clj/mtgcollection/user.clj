(ns mtgcollection.user
  (:require [datomic.api :as api]))

(defn ->user [handle password]
  #:user{:handle handle
         :password password})

(defn create-user [conn handle password]
  )
