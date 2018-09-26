(ns repl.json)

(reduce into #{} (map keys (user/mtg-json-data)))
;; => #{"onlineOnly" "gathererCode" "translations" "border" "block" "releaseDate"
;;      "booster" "name" "magicCardsInfoCode" "alternativeNames" "type"
;;      "magicRaritiesCodes" "cards" "mkm_id" "code" "mkm_name" "oldCode"}

(frequencies
 (map #(get % "layout")
      (mapcat #(get % "cards") (user/mtg-json-data))))

;; => {"leveler" 36,
;;     "token" 36,
;;     "double-faced" 185,
;;     "flip" 22,
;;     "normal" 31776,
;;     "vanguard" 116,
;;     "split" 115,
;;     "plane" 152,
;;     "meld" 9,
;;     "phenomenon" 16,
;;     "scheme" 45}

(frequencies (map #(get % "border") (user/mtg-json-data)))
;; => {"black" 181, "white" 18, "silver" 3}

(frequencies (map #(get % "type") (user/mtg-json-data)))
;; => {"un" 2,
;;     "from the vault" 9,
;;     "starter" 8,
;;     "masters" 5,
;;     "expansion" 74,
;;     "planechase" 3,
;;     "masterpiece" 2,
;;     "reprint" 7,
;;     "box" 9,
;;     "vanguard" 1,
;;     "conspiracy" 2,
;;     "archenemy" 1,
;;     "premium deck" 3,
;;     "duel deck" 22,
;;     "promo" 30,
;;     "commander" 6,
;;     "core" 18}
