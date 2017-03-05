(ns mtgcollection.events
  (:require [ajax.core :refer [raw-response-format text-request-format]]
            [mtgcollection.db :as db]
            [mtgcollection.util.http :refer [GET POST]]
            [re-frame.cofx :refer [inject-cofx]]
            [re-frame.core :as re-frame :refer [debug reg-event-db reg-event-fx]]))

(reg-event-fx :initialize-db [(inject-cofx :storage)]
              (fn [{:keys [storage]} _]
                {:db (assoc
                      db/default-db
                      :user (:user storage))
                 :dispatch [:collection/fetch]}))

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
           {:params {:handle handle
                     :password password}
            :dispatch [:user/handle-login]}]}))

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

(reg-event-db :ajax/failed (fn [db [_ res]]
                             (assoc db
                                    :api/error res
                                    :show-spinner false)))

(reg-event-db :ajax/ok (fn [db]
                         (assoc db :show-spinner false)))

(reg-event-fx :user/handle-login [debug]
              (fn [{:keys [db storage]} [_ user]]
                {:db (assoc db
                            :user user
                            :show-spinner false)
                 :storage {:user user}
                 :dispatch [:collection/fetch]}))

(reg-event-fx :collection/upload-csv [debug]
              (fn [{:keys [db]} [_ form-data]]
                {:db (assoc db :show-spinner true)
                 :ajax [:post "/collection/csv"
                        {:body form-data
                         :dispatch [:ajax/ok]}]}))

(reg-event-fx :collection/fetch [debug]
              (fn [{:keys [db]}]
                {:db (assoc db :show-spinner true)
                 :ajax [:get "/collection" {:dispatch [:collection/receive]}]}))

(reg-event-fx :collection/receive [debug]
              (fn [{:keys [db]} [_ coll]]
                {:db (assoc db
                            :show-spinner false
                            :collection coll)}))

(reg-event-db :clear-error (fn [db _]
                             (dissoc db :api/error)))
