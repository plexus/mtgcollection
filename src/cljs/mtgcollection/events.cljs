(ns mtgcollection.events
  (:require [mtgcollection.db :as db]
            [mtgcollection.util.http :refer [GET POST]]
            [re-frame.core :as re-frame :refer [debug reg-event-db reg-event-fx]]))

(reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(reg-event-fx
  :request-random-card
  (fn [{:keys [db]} _]
    {:db (assoc db :show-twirly true)
     :http-xhrio (GET "/random-card" :on-success [:install-new-card])}))

(reg-event-db :install-new-card
              (fn [db [_ card]]
                (assoc db :card card)))

(reg-event-fx
  :user/register
  (fn [{:keys [db]} [_ handle password]]
    {:db (assoc db :show-twirly true)
     :http-xhrio (POST "/register"
                     :params {:handle handle
                              :password password}
                     :on-success [:user/handle-login])}))

(reg-event-fx
  :user/login [debug]
  (fn [{:keys [db]} [_ handle password]]
    {:db (assoc db :show-twirly true)
     :http-xhrio (POST "/login"
                     :params {:handle handle
                              :password password}
                     :on-success [:user/handle-login])}))

(reg-event-db :http/request-failed (fn [db] db))

(reg-event-db :user/handle-login (fn [db [_ user]] (assoc db :user user)))
