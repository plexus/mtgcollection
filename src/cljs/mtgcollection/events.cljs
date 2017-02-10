(ns mtgcollection.events
  (:require [ajax.core :refer [raw-response-format text-request-format]]
            [mtgcollection.db :as db]
            [mtgcollection.util.http :refer [GET POST]]
            [re-frame.cofx :refer [inject-cofx]]
            [re-frame.core :as re-frame :refer [debug reg-event-db reg-event-fx]]))

(reg-event-fx :initialize-db [(inject-cofx :storage)]
              (fn  [{:keys [storage]} _]
                {:db (assoc
                      db/default-db
                      :user (:user storage))}))

(reg-event-fx
  :request-random-card
  (fn [{:keys [db]} _]
    {:db (assoc db :show-spinner true)
     :ajax [:get "/random-card" {:dispatch [:install-new-card]}]}))

(reg-event-db :install-new-card
              (fn [db [_ card]]
                (assoc db :card card)))

(reg-event-fx
 :user/register
 (fn [{:keys [db]} [_ handle password]]
   {:db (assoc db :show-spinner true)
    :ajax [:post "/register"
           :params {:handle handle
                    :password password}
           :dispatch [:user/handle-login]]}))

(reg-event-fx
 :user/login
 (fn [{:keys [db]} [_ handle password]]
   {:db (assoc db :show-spinner true)
    :ajax [:post "/login"
           {:params {:handle handle
                     :password password}
            :dispatch [:user/handle-login]}]}))


(reg-event-fx :user/logout
              (fn [{:keys [db]} _]
                {:db (dissoc db :user)
                 :storage {:user nil}}))

(reg-event-db :http/request-failed (fn [db [_ res]]
                                     (assoc db
                                            :api/error (:error res)
                                            ;;:show-spinner false
                                            )))

(reg-event-fx :user/handle-login (fn [{:keys [db storage]} [_ user]]
                                   {:db (assoc db
                                               :user user
                                               :show-spinner false)
                                    :storage {:user user}}))

(reg-event-fx :collection/upload-csv [debug]
              (fn [{:keys [db]} [_ form-data]]
                {:ajax [:post "/collection/csv"
                        {:body form-data
                         :dispatch [:upload-ok]}]}))
