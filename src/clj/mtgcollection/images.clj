(ns mtgcollection.images
  (:require [clojure.string :as str]
            [clj-fuzzy.metrics :as fuzz]))

(def index (into #{} (str/split (slurp "https://mtg.arnebrasseur.net/index.cgi") #"\n")))

(defn hamming [strand1 strand2]
  (reduce
   +
   (map
    (fn [base1 base2]
      (if (= base1 base2) 0 1))
    strand1
    strand2)))

(defn img-paths [{:card/keys [set name names] :as card}]
  (let [code (:set/code set)
        card-names (cond-> [name]
                     (seq names)
                     (conj (str/join " ⁄⁄ " names)))]
    (map #(str code "/" % ".xlhq.jpg") card-names)))

(defn find-nearest [index card]
  (let [paths    (img-paths card)
        code     (:set/code (:card/set card))
        set-imgs (filter #(.startsWith % code) index)
        score-fn (fn [p] (apply max (map (partial fuzz/jaro-winkler p) paths)))
        match    (last (sort-by score-fn set-imgs))
        score    (score-fn match)]
    (when (> 0.94 score)
      (throw (ex-info "Bad match" {:score score :paths paths :match match})))
    match))

(defonce do-memoize
  (do
    (alter-var-root #'clj-fuzzy.jaro-winkler/matches memoize)
    (alter-var-root #'clj-fuzzy.jaro-winkler/transpositions memoize)
    (alter-var-root #'clj-fuzzy.jaro-winkler/winkler-prefix memoize)
    (alter-var-root #'clj-fuzzy.jaro-winkler/jaro memoize)
    (alter-var-root #'clj-fuzzy.jaro-winkler/jaro-winkler memoize)))

(defn card-image [card]
  (let [paths (img-paths card)]
    (if-let [path (some #(get index %) paths)]
      path
      (find-nearest index card))))
