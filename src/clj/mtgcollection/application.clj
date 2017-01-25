(ns mtgcollection.application
  (:gen-class)
  (:require [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.http-kit :refer [new-web-server]]))

(defn routes [endpoint]
  )

(defn app-system []
  (component/system-map
   :routes (new-endpoint routes)
   :middleware (new-middleware  {:middleware [[wrap-defaults :defaults]]
                                 :defaults api-defaults})
   :handler (component/using
             (new-handler)
             [:routes :middleware])
   :http (component/using
          (new-web-server (Integer. (or (env :port) 10555)))
          [:handler])))

(defn -main [& _]
  (component/start (app-system)))
