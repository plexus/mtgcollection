(ns mtgcollection.routes
  (:require [buddy.core.hash :as hash]
            [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [mtgcollection.core :refer [random-card]]
            [mtgcollection.routes.user :refer [user-routes]]
            [ring.util.response :as res]))


(defn app-routes [{:keys [datomic]}]
  (routes
   (GET "/" _
     (some-> (res/resource-response "public/index.html")
             (res/content-type "html")))
   (GET "/random-card" []
     {:body (#'random-card (:conn datomic))})
   (user-routes datomic)
   (resources "/")))
