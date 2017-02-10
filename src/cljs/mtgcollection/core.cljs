(ns mtgcollection.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              ^:keep [day8.re-frame.http-fx]
              [mtgcollection.events]
              [mtgcollection.subs]
              [mtgcollection.views :as views]
              [mtgcollection.config :as config]
              [mtgcollection.util.localstorage]
              [re-frisk.core :refer [enable-re-frisk!]]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
