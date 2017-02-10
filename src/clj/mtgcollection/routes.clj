(ns mtgcollection.routes
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [mtgcollection.cards :refer [random-card]]
            [mtgcollection.routes.user :refer [user-routes]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.util.response :as res]))

(defn POST-upload-csv []
  (wrap-multipart-params
   (POST "/collection/csv" [file]
     {:body (take 2 (with-open [in-file (io/reader (:tempfile file))]
                      (doall
                       (csv/read-csv in-file))))})))

(defn app-routes [{:keys [datomic]}]
  (routes
   (GET "/" _
     (some-> (res/resource-response "public/index.html")
             (res/content-type "html")))
   (GET "/random-card" []
     {:body (#'random-card (:conn datomic))})
   (user-routes datomic)
   (POST-upload-csv)
   (resources "/")))
