(ns recommendation-application.recommendations
  (:use [recommendation-application.models.database 
         :only [get-all-games]]))


(defn game-critics []
  (apply merge {}
       (for [game (get-all-games)] 
         (assoc {} (:name game) 
                (into {} (for [critic (:critics game)]
                           (assoc {} (:name critic) (read-string (:score critic)))))))))

(def cla  {"Gamezilla!" 100,
  "Yahoo! Games" 100,
  "Computer Gaming World" 100,
  "All Game Guide" 80,
  "Adrenaline Vault" 100,
  "PC Gameworld" 86,
  "PC Gamer" 90,
  "GameSpot" 96,
  "Game Revolution" 100,
  "Computer Games Magazine" 80,
  "Just Adventure" 100,
  "Electric Playground" 85})

(def pu {"Total Video Games" 90,
  "Yahoo! Games" 90,
  "Game Informer" 93,
  "PC Format" 90,
  "Media and Games Online Network" 89,
  "PGNx Media" 91,
  "Gamer's Pulse" 88,
  "Computer Gaming World" 70,
  "Computer Games Online RO" 90,
  "Four Fat Chicks" 100,
  "GameSpy" 91,
  "Armchair Empire" 80,
  "All Game Guide" 80,
  "Adrenaline Vault" 80,
  "IGN" 92,
  "Game Over Online" 90,
  "PC Gameworld" 93,
  "PC Gamer" 91,
  "GameSpot" 93,
  "G4 TV" 80,
  "AtomicGamer" 90,
  "Game Revolution" 91,
  "netjak" 82,
  "Gamers' Temple" 92,
  "ActionTrip" 79,
  "GameZone" 93,
  "GamerWeb PC" 91})

(defn shared-critics [first-critics second-critics]
 (apply merge {}
      (for [k (keys first-critics)
                        :when (contains? second-critics k)]
                    (assoc {} k [(first-critics k) (second-critics k)]))))



(defn pearson-correlation [prefs1 prefs2]
  (let [shared-games (shared-critics prefs1 prefs2) ])
  )