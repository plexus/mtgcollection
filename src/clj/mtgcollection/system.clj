(ns mtgcollection.system
  (:gen-class)
  (:require [buddy.auth.middleware :refer [wrap-authentication]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [mtgcollection.routes :refer [app-routes]]
            [mtgcollection.routes.user :as user-routes]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [system.components.datomic :refer [new-datomic-db]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.http-kit :refer [new-web-server]]
            [system.components.middleware :refer [new-middleware]]))

(defn prod-system []
  (component/system-map
   :datomic (new-datomic-db (env :datomic-uri))

   :routes (-> (new-endpoint app-routes)
               (component/using [:datomic]))

   :middleware (new-middleware  {:middleware [[wrap-defaults :defaults]
                                              wrap-restful-format
                                              [wrap-authentication :auth-backend]]
                                 :auth-backend (user-routes/buddy-backend)
                                 :defaults api-defaults})

   :handler (-> (new-handler)
                (component/using [:routes :middleware]))

   :http (-> (new-web-server (Integer. (or (env :port) 10555)))
             (component/using [:handler]))))

(defn -main [& _]
  (component/start (prod-system)))
