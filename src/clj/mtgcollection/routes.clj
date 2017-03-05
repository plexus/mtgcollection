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

(defn- uid [req]
  (get-in req [:identity :user :db/id]))

(defn collection-routes [conn]
  (routes
   (GET "/collection" req
     {:body
      (if-let [uid (uid req)]
        {:count (d/q '[:find (count ?cid) .
                       :in $ ?uid
                       :where
                       [?uid :user/collection ?cid]]
                     (d/db conn) uid)}
        {:count 0})})

   (wrap-multipart-params
    (POST "/collection/csv" [file :as req]
      (when-let [uid (uid req)]
        (let [db (d/db conn)
              {:keys [txs not-found]} (->> (read-csv (:tempfile file))
                                           (drop 1)
                                           (collection/import-csv db uid))]
          (d/transact conn txs)
          {:body {:not-found not-found}}))))))

(defn app-routes [{:keys [datomic]}]
  (let [conn (:conn datomic)]
    (routes
     (GET "/" _
       (some-> (res/resource-response "public/index.html")
               (res/content-type "html")))
     (GET "/random-card" []
       {:body (#'random-card conn)})
     (user-routes datomic)
     (collection-routes conn)
     (resources "/"))))
