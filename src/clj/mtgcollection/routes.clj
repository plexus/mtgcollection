(ns mtgcollection.routes
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [mtgcollection.core :refer [random-card]]
            [ring.util.response :as res]))

(defn app-routes [{:keys [datomic]}]
  (routes
   (GET "/" _
     (some-> (res/resource-response "public/index.html")
             (res/content-type "html")))
   (GET "/random-card" []
     {:body (#'random-card (:conn datomic))})
   (resources "/")))
