(ns mtgcollection.styles
  (:refer-clojure :exclude [complement])
  (:require [garden-watcher.def :refer [defstyles]]
            [garden.color :refer :all]))

(defstyles style
  [:a {:cursor "pointer"}]
  [:.card {:border-radius "16px"
           :margin "1em"
           :float "left"
           #_#_:width "300px"}]
  [:body {:background-color (lighten (hex->rgb "#75A88B") 40)}]
  [:.navbar {:background-color (darken (hex->rgb "#75A88B") 30)
             :color "#fff"
             :line-height "3rem"}
   [:.branding
    [:a {:color "#fff"}]]
   [:a {:color "#ccccff"
        :text-decoration "underline"}]
   [:.spinner {:position "absolute"
               :height "35px"
               :width "35px"
               :left "50%"
               :margin "1.2em"}]]

  [:.margin-bottom {:margin-bottom "2rem"}]
  )
