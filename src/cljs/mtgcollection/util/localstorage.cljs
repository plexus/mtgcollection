(ns mtgcollection.util.localstorage
  (:require [re-frame.core :refer [reg-cofx reg-fx]]
            [cognitect.transit :as t]))

(defonce t-writer (t/writer :json))
(defonce t-reader (t/reader :json))

(defn set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) key (t/write t-writer val)))

(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (t/read t-reader (.getItem (.-localStorage js/window) key)))

(defn remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) key))

(reg-fx :storage (fn [data]
                   (set-item! "manapool" (merge (or (get-item "manapool") {}) data))))

(reg-cofx :storage (fn [cofx _]
                     (assoc cofx
                            :storage (or (get-item "manapool") {}))))

#_(remove-item! "manapool")
