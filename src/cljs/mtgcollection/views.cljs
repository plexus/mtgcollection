(ns mtgcollection.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
              [reagent.core :as r]))

(def <sub (comp deref subscribe))
(def >evt dispatch)

;;https://mtg.arnebrasseur.net/ZEN/Trapfinder's%20Trick.xlhq.jpg
(defn card [{:card/keys [name set rarity] :as card} & [opts]]
  (let [cnt    (get-in card [:owned-card/_card 0 :owned-card/count])
        width  (:width opts 300)
        height (js/Math.round (* 1.396 width))
        code   (:set/code set)]
    [:img.card {:style {:border-color (case rarity
                                        "Rare" "goldenrod"
                                        "Uncommon" "silver"
                                        "snow")
                        :border-size "2px"
                        :border-style "solid"}
                :width    width :height height
                :title    (str cnt " pieces")
                :src      (str "https://res.cloudinary.com/mtgimg/image/upload/w_" width "/mtg/" code "/" name ".xlhq.jpg")
                :on-click #(dispatch [:request-random-card])}]))

(defn navbar []
  [:div.pv4.washed-blue.bb.b-solid.bw1.b--dark-gray.bg-dark-green.flex
   [:div.container
    (when (<sub [:show-spinner])
      [:img.spinner {:src "images/spinner.gif"}])
    [:div.branding.col-md-9.f1
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

(defn subheader [& caption]
  [:div.b--black.bb.mb3
   (into [:h2.f3.mb2] caption)])

(defn main-page []
  (let [cards (<sub [:collection/cards])]
    [:div.col-xs-12
     [:div
      [subheader "Collection (" (count cards) ")"]
      (doall (for [row (partition-all 4 (sort-by :card/colorIdentity cards))]
               ^{:key (:card/multiverseid c)}
               (into [:div.flex]
                     (map (fn [c] [card c]))
                     row)))
      ]
     [:div
      [subheader "Import"]
      [csv-upload-form]]]))

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

(defn error-message [error]
  [:div.mv2.mh1.ba.br1.b--solid.b--red.bg-washed-red
   [:div.ph1.fr
    [:a {:on-click #(>evt [:clear-error])} "x"]]
   [:div.pv3.ph6.flex.items-center.justify-center
    (case (:failure error)
      :error
      (case (-> error :response :error)
        :login-failed "Login failed. Check your login/password."
        (str "Couldn't handle request: " (pr-str error)))
      (str "Unhandled error: " (pr-str error)))]
   ])

(defn main-panel []
  [:div.app
   [navbar]
   (if-let [err (<sub [:api/error])]
     [error-message err])
   [:div.container
    (if (<sub [:user/handle])
      [main-page]
      [login-page])]])
