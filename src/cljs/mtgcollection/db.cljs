(ns mtgcollection.db)

(def default-db
  {:name "re-frame"
   :card {:card/manaCost "{R}",
          :card/cmc "1",
          :card/multiverseid 19602,
          :card/set #:db{:id 17592186046208},
          :set/releaseDate #inst "1999-10-04T00:00:00.000-00:00",
          :card/rarity "Common",
          :card/colorIdentity ["R"],
          :set/name "Mercadian Masques",
          :card/text
          "{1}: Flailing Soldier gets +1/+1 until end of turn. Any player may activate this ability.\n{1}: Flailing Soldier gets -1/-1 until end of turn. Any player may activate this ability.",
          :set/code "MMQ",
          :db/id 17592186046208,
          :card/name "Flailing Soldier",
          :card/type "Creature — Human Soldier",
          :card/artist "Orizio Daniele",
          :card/types ["Creature"]}})
