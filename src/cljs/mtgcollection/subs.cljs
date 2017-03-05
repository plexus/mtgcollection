(ns mtgcollection.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub :name (fn [db] (:name db)))
(re-frame/reg-sub :card (fn [db] (:card db)))
(re-frame/reg-sub :user/handle (fn [db] (:user/handle (:user db))))
(re-frame/reg-sub :show-spinner (fn [db] (:show-spinner db)))
(re-frame/reg-sub :api/error (fn [db] (:api/error db)))
(re-frame/reg-sub :collection/count (fn [db] (-> db :collection :count)))
