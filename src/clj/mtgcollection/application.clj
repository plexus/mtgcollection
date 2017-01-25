(ns mtgcollection.application
  (:gen-class)
  (:require [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.http-kit :refer [new-web-server]]
            [system.components.datomic :refer [new-datomic-db]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [mtgcollection.core :refer [random-card]]
            [compojure.core :refer [GET routes]]))

(defn app-routes [{:keys [datomic]}]
  (routes
   (GET "/random-card" []
     {:body (random-card (:conn datomic))})))

(defn app-system []
  (component/system-map
   :datomic (new-datomic-db (env :datomic-uri))

   :routes (-> (new-endpoint app-routes)
               (component/using [:datomic]))

   :middleware (new-middleware  {:middleware [[wrap-defaults :defaults]
                                              wrap-restful-format]
                                 :defaults api-defaults})

   :handler (-> (new-handler)
                (component/using [:routes :middleware]))

   :http (-> (new-web-server (Integer. (or (env :port) 10555)))
             (component/using [:handler]))))

(defn -main [& _]
  (component/start (app-system)))
