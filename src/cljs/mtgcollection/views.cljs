(ns mtgcollection.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]))

(defn card []
  (let [card-sub (subscribe [:card])]
    (fn []
      (let [{:card/keys [name] :set/keys [code] set-name :set/name} @card-sub]
        [:div
         [:img.card {:src (str "https://mtg.arnebrasseur.net/" code "/" name ".xlhq.jpg")
                     :on-click #(dispatch [:request-random-card])}]
         code " - " set-name]))))

(defn main-panel []
  [card])


;;https://mtg.arnebrasseur.net/ZEN/Trapfinder's%20Trick.xlhq.jpg
