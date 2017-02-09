(ns mtgcollection.styles
  (:require [garden-watcher.def :refer [defstyles]]
            [garden.color :refer :all]))

(defstyles style

  [:h1 {:text-decoration "underline"}]
  [:.card {:border-radius "50px"}]
  [:body {:background-color "#75A88B"}]
  [:.navbar {:background-color (darken (hex->rgb "#75A88B") 30)
             :color "#fff"
             :line-height "1.5em"
             :font-size "2em"
             }
   [:a {:color "#fff"}]]
)
