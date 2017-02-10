(ns mtgcollection.routes
  (:require [buddy.core.hash :as hash]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :refer [days from-now]]
            [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [datomic.api :as api]
            [mtgcollection.core :refer [random-card]]
            [ring.util.response :as res]))

(defonce secret (hash/sha256 "gQ7RO1qJuU90VFd0ncV++yApix885FTWBI3vWcN0DJyGVJlyiPSh1A=="))
(defonce encryption {:alg :a256kw :enc :a128gcm})

(defn transact-one
  "Transact tx and return record with real entity id."
  [conn tx]
  (let [tempid (api/tempid :db.part/user)
        tx (assoc tx :db/id tempid)
        post-tx @(api/transact conn [tx])
        db (:db-after post-tx)
        entid (api/resolve-tempid db (:tempids post-tx) tempid)]
    (assoc tx :db/id entid)))

(defn app-routes [{:keys [datomic]}]
  (routes
   (GET "/" _
     (some-> (res/resource-response "public/index.html")
             (res/content-type "html")))
   (GET "/random-card" []
     {:body (#'random-card (:conn datomic))})
   (GET "/login" [username password]
     )
   (GET "/register" [handle password]
     (let [user #:user{:handle handle
                       :password (hashers/derive password)}
           user (transact-one (:conn datomic) user)
           claims {:user user
                    :exp (-> 3 days from-now)}
           token (jwt/encrypt claims secret encryption)]
       {:body (assoc user :token token)}))
   (resources "/")))
