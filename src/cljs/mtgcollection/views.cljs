(ns mtgcollection.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
              [reagent.core :as r]))

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
  (let [type (r/atom :login)
        handle (r/atom "")
        password (r/atom "")]
    (fn []
      [:div.login-form
       [:div.form-group.row
        [:label.col-xs-3.col-form-label {:for "username"} "Username"]
        [:div.col-xs-9
         [:input.form-control {:type "text"
                               :id "username"
                               :on-change #(reset! handle (-> % .-target .-value))}]]]
       [:div.form-group.row
        [:label.col-xs-3 {:for "password"} "Password"]
        [:div.col-xs-9
         [:input.form-control {:type "text"
                               :id "password"
                               :on-change #(reset! password (-> % .-target .-value))}]]]
       [:input.form-control
        (case @type
          :login {:type "submit"
                  :value "Login"
                  :on-click #(dispatch [:user/login @handle @password])}
          :register {:type "submit"
                     :value "Register"
                     :on-click #(dispatch [:user/register @handle @password])})]
       [:div.text-center
        (case @type
          :login [:a {:on-click #(reset! type :register)} "register instead"]
          :register [:a {:on-click #(reset! type :login)} "log in instead"])]])))

(defn front-page []
  [:div
   [:div.row.margin-bottom
    [:div.col-md-4.col-md-push-4.text-center
     [:h2
      "Welcome to Manapool."]
     [:h3
      "Enjoy your stay!"]]]
   [:div.row
    [:div.col-md-4.col-md-push-4
     [login-form]]]])

(defn main-panel []
  [:div.app
   [navbar]
   [:div.container
    [front-page]]])
