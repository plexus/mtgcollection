(ns mtgcollection.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]))

;;https://mtg.arnebrasseur.net/ZEN/Trapfinder's%20Trick.xlhq.jpg
(defn card []
  (let [card-sub (subscribe [:card])]
    (fn []
      (let [{:card/keys [name] :set/keys [code] set-name :set/name} @card-sub]
        [:div
         [:img.card {:src (str "https://mtg.arnebrasseur.net/" code "/" name ".xlhq.jpg")
                     :on-click #(dispatch [:request-random-card])}]
         code " - " set-name]))))

(defn navbar []
  [:div.navbar
   [:div.container

    [:a "Manapool"]]])

(defn login-form []
  [:div.login-form
   [:div.form-group.row
    [:label.col-xs-3.col-form-label {:for "username"} "Username"]
    [:div.col-xs-9
     [:input.form-control {:type "text" :id "username"}]]]
   [:div.form-group.row
    [:label.col-xs-3 {:for "password"} "Password"]
    [:div.col-xs-9
     [:input.form-control {:type "text" :id "password"}]]]
   [:input.form-control {:type "submit" :value "Login"}]])

(defn front-page []
  [:div.row
   [:div.col-md-4.col-md-push-2
    [:p
     "Welcome to Manapool. Please enjoy your stay!"]]
   [:div.col-md-4.col-md-push-2
    [login-form]]])

(defn main-panel []
  [:div.app
   [navbar]
   [:div.container

    [front-page]]
   ])
