(ns mtgcollection.events
    (:require [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx]]
              [mtgcollection.db :as db]
              [ajax.core :as ajax]))

(reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(reg-event-fx
  :request-random-card
  (fn [{:keys [db]} _]
    {:db (assoc db :show-twirly true)
     :http-xhrio {:method          :get
                  :uri             "/random-card"
                  :format          (ajax/transit-request-format)
                  :response-format (ajax/transit-response-format)
                  :on-success      [:install-new-card]
                  :on-failure      [:fail]}}))

(reg-event-db :install-new-card
              (fn [db [_ card]]
                (assoc db :card card)))
