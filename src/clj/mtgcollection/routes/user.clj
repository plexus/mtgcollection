(ns mtgcollection.routes.user
  (:require [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.core.hash :as hash]
            [buddy.hashers :as hashers]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :refer [days from-now]]
            [compojure.core :refer [POST routes]]
            [datomic.api :as d]
            [mtgcollection.user :refer [find-user-for-login]]
            [mtgcollection.util.datomic :refer [transact-one]]))

(defonce secret (hash/sha256 "gQ7RO1qJuU90VFd0ncV++yApix885FTWBI3vWcN0DJyGVJlyiPSh1A=="))
(defonce encryption {:alg :a256kw :enc :a128gcm})

(defn buddy-backend []
  (jwe-backend {:secret secret
                :options encryption}))


(defn- web-token-response [user]
  (let [claims {:user user
                :exp (-> 3 days from-now)}
        token (jwt/encrypt claims secret encryption)]
    {:body {:token token
            :user/id (:db/id user)
            :user/handle (:user/handle user)}}))

(defn POST-login [conn]
  (POST "/login" [handle password]
    (let [[uid] (find-user-for-login (d/db conn)
                                     handle
                                     password)]
      (if uid
        (web-token-response {:db/id uid
                             :user/handle handle})
        {:status 403
         :body {:error :login-failed}}))))

(defn POST-register [conn]
  (POST "/register" [handle password]
    (let [user #:user{:handle handle
                      :password (hashers/derive password)}
          user (transact-one conn user)]
      (web-token-response user))))

(defn user-routes [{:keys [conn] :as datomic}]
  (routes
   (POST-login conn)
   (POST-register conn)))
