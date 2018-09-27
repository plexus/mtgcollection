(ns repl.images
  (:require [mtgcollection.queries :as q]
            [datomic.api :as d]
            [mtgcollection.images :as img]))

(def ice-age-cards (:card/_set (first (q/sets (user/db) ["ICE"]))))

(defn card->imgdata [card]
  {:card/multiverse-id (:card/multiverse-id card)
   :set/code (:set/code (:card/set card))
   :card/name (:card/name card)
   :card/image-slug (img/card-image card)})

(defn set->imgdata [set]
  (map card->imgdata (:card/_set set)))

(def image-data (map card->imgdata ice-age-cards))

(def sets (->> (user/db)
               q/sets
               (sort-by :set/release-date)
               reverse
               (drop 19)
               (remove :set/online-only)
               (remove #(= (first (:set/code %)) \p))
               (remove #(#{"DDN"
                           "MD1"
                           "MMA"
                           "PC2"
                           "DDE"
                           "DDD"
                           "DDC"
                           "DD2" ;; token missing
                           "CON"
                           "CST" ;; coldsnap theme decks = alliances/ice age/cold snap reprints
                           "9ED" ;; vengeance missing
                           "CEI" ;; international collector's edition
                           "CED"
                           "RQS" ;; rivals quick start set, looks like 4ED?
                           "MGB" ;; Multiverse Gift Box = Visions
                           "ITP" ;; Introductory Two-Player Set (ITP) = 4ED
                           "VAN" ;; Arcanis, the Omnnipotent Avatar missing / Vanguard — Promotional oversized cards that modify the game rules of each player.
                           "UGL" ;; "UGL/B.F.M. (Big Furry Monster, Right Side).xlhq.jpg"
                           "NMS" ;; Should be "NEM"? Nemesis
                           "S00" ;; Obsianus Golem Starter 2000 , 5th/6th ed?
                           "8ED" ;; cards missing
                           } (:set/code %)))
               (reverse)
               ))

(def image-data (into [] (mapcat set->imgdata) sets))

(require 'clojure.pprint)

(:set/name (first
            (q/sets (user/db) ["NMS"])))

(spit "resources/image_data.edn"
      (with-out-str
        (binding [clojure.core/*print-length* false]
          (clojure.pprint/pprint image-data))))
