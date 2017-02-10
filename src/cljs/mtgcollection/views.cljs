(ns mtgcollection.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
              [reagent.core :as r]))

(def <sub (comp deref subscribe))
(def >evt dispatch)

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
    (when (<sub [:show-spinner])
      [:img.spinner {:src "images/spinner.gif"}])
    [:div.branding.col-md-9.h1
     [:a "Manapool"]]
    [:div.col-md-3.text-right
     (when-let [handle (<sub [:user/handle])]
       [:div
        [:p "Logged in as " handle]
        [:p [:a {:on-click #(>evt [:user/logout])} "log out"]]])]]])

(defn login-form []
  (let [type (r/atom :login)
        handle (r/atom "")
        password (r/atom "")
        submit #(do (case @type
                      :login (dispatch [:user/login @handle @password])
                      :register (dispatch [:user/register @handle @password]))
                    (.preventDefault %))]
    (fn []
      [:form.login-form {:on-submit submit}
       [:div.form-group.row
        [:label.col-xs-3.col-form-label {:for "username"} "Username"]
        [:div.col-xs-9
         [:input.form-control {:type "text"
                               :id "username"
                               :on-change #(reset! handle (-> % .-target .-value))}]]]
       [:div.form-group.row
        [:label.col-xs-3 {:for "password"} "Password"]
        [:div.col-xs-9
         [:input.form-control {:type "password"
                               :id "password"
                               :on-change #(reset! password (-> % .-target .-value))}]]]
       [:input.form-control {:type "submit"
                             :value (case @type :login "Login" :register "Register")}]
       [:div.text-center
        (case @type
          :login [:a {:on-click #(reset! type :register)} "register instead"]
          :register [:a {:on-click #(reset! type :login)} "log in instead"])]])))

js/FormData.

(defn csv-upload-form []
  (let [form-element (atom nil)]
    (fn []
      [:form.text-left {:ref #(reset! form-element %)
                        :encType "multipart/form-data"
                        :on-submit #(do
                                      (.preventDefault %)
                                      (>evt [:collection/upload-csv (js/FormData. @form-element)]))}
       [:input {:name "file" :type "file" :size "20"}]
       [:input {:type "submit" :name "submit" :value "Upload"}]])))

(defn main-page []
  [:div
   [:div.row.margin-bottom
    [:div.col-md-4.col-md-push-4.text-center
     [:h2 "Welcome to Manapool."]]]
   [:div
    [:div.col-md-4.col-md-push-4.text-center
     [csv-upload-form]]]])

(defn login-page []
  [:div
   [:div.row.margin-bottom
    [:div.col-md-4.col-md-push-4.text-center
     [:h2
      "Welcome to Manapool."]
     [:h3
      "Have a shitload of fun!"]]]
   [:div.row
    [:div.col-md-4.col-md-push-4
     [login-form]]]])

(defn main-panel []
  [:div.app
   [navbar]
   [:div.container
    (if (<sub [:user/handle])
      [main-page]
      [login-page])]])
