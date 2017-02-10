(ns mtgcollection.routes
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [mtgcollection.cards :refer [random-card]]
            [mtgcollection.collection :as collection]
            [mtgcollection.routes.user :refer [user-routes]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.util.response :as res]
            [datomic.api :as d]))

(defn- read-csv [file]
  (with-open [in-file (io/reader file)]
    (doall (csv/read-csv in-file))))

(defn POST-upload-csv [db-conn]
  (wrap-multipart-params
   (POST "/collection/csv" [file :as req]
     (when-let [uid (get-in req [:identity :user :db/id])]
       (prn uid)
       (let [db (d/db db-conn)
             {:keys [txs not-found]} (->> (read-csv (:tempfile file))
                                          (drop 1)
                                          (collection/import-csv db uid))]
         (d/transact db-conn txs)
         {:body {:not-found not-found}})))))

(defn app-routes [{:keys [datomic]}]
  (routes
   (GET "/" _
     (some-> (res/resource-response "public/index.html")
             (res/content-type "html")))
   (GET "/random-card" []
     {:body (#'random-card (:conn datomic))})
   (user-routes datomic)
   (POST-upload-csv (:conn datomic))
   (resources "/")))
