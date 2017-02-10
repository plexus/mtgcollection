(ns mtgcollection.styles
  (:require [garden-watcher.def :refer [defstyles]]
            [garden.color :refer :all]))

(defstyles style
  [:.card {:border-radius "10px"
           :width "300px"}]
  [:body {:background-color (lighten (hex->rgb "#75A88B") 40)}]
  [:.navbar {:background-color (darken (hex->rgb "#75A88B") 30)
             :color "#fff"
             :line-height "1.5em"
             :font-size "2em"
             }
   [:a {:color "#fff"}]]

  [:.margin-bottom {:margin-bottom "2rem"}]
)
