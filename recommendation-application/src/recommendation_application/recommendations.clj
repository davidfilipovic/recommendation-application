(ns recommendation-application.recommendations
  (:use [recommendation-application.get-data :only [shared-critics game-critics]]))

(defn sum [x] (reduce + 0 x))

(defn expt [x n]
  (reduce * (repeat n x)))

(defn pearson-correlation 
  "Calculate pearson correlation."
  [data first-critic second-critic]
  (let [shared-games (shared-critics data first-critic second-critic)
        size (count shared-games)]
    (if (zero? size)
      0
      (let [first-ratings (for [[crit sco] shared-games]
                            (first sco))
            second-ratings (for [[crit sco] shared-games]
                             (second sco))
            first-sum (sum first-ratings)
            second-sum (sum second-ratings)
            first-square-sum (sum (map #(expt % 2) first-ratings))
            second-square-sum (sum (map #(expt % 2) second-ratings))
            product-sum (sum (map * first-ratings second-ratings))
            numerator (- product-sum (/ (* first-sum second-sum) size))
            first-factor (- first-square-sum (/ (expt first-sum 2) size))
            second-factor (- second-square-sum (/ (expt second-sum 2) size))
            denominator (Math/sqrt (* first-factor second-factor))]          
        (if-not (zero? denominator)
          (/ numerator denominator)
          0)))))

(defn top-matches-for-user 
  "Get the top matches for a critic."
  [data first-critic]
  (for [second-critic (filter #(not= first-critic %) (keys data))]   
    [(pearson-correlation data first-critic second-critic) second-critic]))

(defn top-matches
  "Returns a ranked list of the most similar critics."
  ([data critic n]
    (let [matches (top-matches-for-user data critic)]
      (take n (sort-by first > matches)))))

(defn recommend-games-for-game 
  "Returns recommendation for supplied game name."
  [game-name]
  (let [critics (game-critics)
        games-names (keys critics)
        ;other-games (pmap #(let [g %] (if-not (= g game-name))) games-names);(filter #(not= game-name %) games-names)
        similar-games (pmap #(if-not (= game-name %)
                               (pearson-correlation critics game-name %)) games-names)
        fm (zipmap games-names similar-games)
        d  (into {} (filter second fm))
        fm1 (into {} (filter #(> (second %) 0) d))
        ] 

     (into (sorted-map-by (fn [key1 key2] (compare (fm1 key2) (fm1 key1)))) fm1)
   ; d
    ))
    ;(into (sorted-map-by (fn [key1 key2] (compare (key2 my-map) (key1 my-map)))) my-map)
    ;final-map
    ;similar-games
    #_(sort-by first;val > 
               (into {} ;(filter (fn [[key value]] (if (< 0 value)
                                                         ;(dissoc final-map key))) 
                                                         final-map));));)
