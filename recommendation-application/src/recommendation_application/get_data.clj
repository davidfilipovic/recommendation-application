(ns recommendation-application.get-data
  (:use [recommendation-application.models.database :only [save-game empty-db get-game-by-name get-all get-all-games get-by-score insert-game drop-all-data]]
       
        )
  (:require [compojure.core :refer :all]
            [hiccup.form :refer [form-to label text-field radio-button password-field submit-button email-field]]
            [hickory.core :as hickory]
            [recommendation-application.views.layout :as layout]
            [hiccup.element :refer :all]
            [noir.session :as session]
            [hickory.select :as s]
            [noir.response :refer [redirect]]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-http.client :as client])
  (:import (java.io InputStream InputStreamReader BufferedReader)
           (java.net URL HttpURLConnection)))

(def data2 {"The Walking Dead: A Telltale Games Series"
  {"CD-Action" 90,
   "Games.cz" 80,
   "Hyper Magazine" 90,
   "GRYOnline.pl" 95,
   "PC PowerPlay" 100,
   "Gamer.nl" 90,
   "GamingTrend" 95,
   "IGN" 93,
   "Game Over Online" 83,
   "Eurogamer" 80,
   "LEVEL (Czech Republic)" 90,
   "ActionTrip" 96,
   "Metro GameCentral" 80,
   "Pelit (Finland)" 92}
 "Guacamelee! Gold Edition"
  {"Level7.nu" 80,
   "3DJuegos" 86,
   "Canadian Online Gamers" 90,
   "PC Gamer UK" 80,
   "Eurogamer Germany" 90,
   "Hardcore Gamer Magazine" 90,
   "Impulsegamer" 90,
   "Gaming Nexus" 90,
   "Gaming Age" 91,
   "IGN" 91,
   "Hooked Gamers" 87,
   "IncGamers" 70,
   "PC Games (Germany)" 87,
   "GameOver.gr" 85,
   "GameSpot" 90,
   "Eurogamer Italy" 80,
   "ZTGD" 95,
   "Gamers' Temple" 90,
   "Worth Playing" 98,
   "DarkStation" 90,
   "DarkZero" 90,
   "Multiplayer.it" 90}
 "World Soccer Winning Eleven 8 International"
  {"GameZone" 95,
   "Game Revolution" 91,
   "Yahoo! Games" 90,
   "Computer Games Magazine" 90,
   "PC Gamer" 89,
   "GameSpot" 87,
   "Computer Gaming World" 80}
 "Freedom Force"
  {"Total Video Games" 80,
   "Yahoo! Games" 80,
   "Game Informer" 88,
   "Media and Games Online Network" 95,
   "Gamer's Pulse" 88,
   "Computer Gaming World" 100,
   "Games Radar" 90,
   "GamePen" 80,
   "Four Fat Chicks" 100,
   "GameSpy" 89,
   "Maxim Online" 60,
   "Armchair Empire" 88,
   "All Game Guide" 90,
   "Adrenaline Vault" 90,
   "Gamer.tv" 87,
   "IGN" 93,
   "Game Over Online" 86,
   "Pregaming" 90,
   "PC Gamer" 94,
   "GameSpot" 90,
   "G4 TV" 80,
   "netjak" 90,
   "Entertainment Weekly" 100,
   "Gamers' Temple" 91,
   "GamePro" 90,
   "ActionTrip" 93,
   "GameZone" 89}
 "Warcraft III: Reign of Chaos"
  {"Total Video Games" 90,
   "Gamezilla!" 96,
   "Yahoo! Games" 80,
   "Game Informer" 95,
   "Media and Games Online Network" 91,
   "GamesRadar" 100,
   "Gamer's Pulse" 100,
   "Computer Gaming World" 80,
   "Games Radar" 90,
   "FiringSquad" 91,
   "GamePen" 80,
   "Cinescape" 100,
   "GameSpy" 92,
   "Maxim Online" 100,
   "Armchair Empire" 95,
   "All Game Guide" 80,
   "GameCritics" 85,
   "Adrenaline Vault" 100,
   "IGN" 93,
   "Game Over Online" 90,
   "Pregaming" 100,
   "PC Gameworld" 91,
   "PC Gamer" 94,
   "Eurogamer" 90,
   "GameSpot" 93,
   "G4 TV" 80,
   "AtomicGamer" 92,
   "Game Revolution" 91,
   "netjak" 79,
   "Computer Games Magazine" 80,
   "Playboy" 95,
   "CPUGamer" 100,
   "Entertainment Weekly" 100,
   "Gamers' Temple" 96,
   "Cincinnati Enquirer" 90,
   "GamePro" 100,
   "ActionTrip" 95,
   "Electric Playground" 95,
   "GameZone" 97,
   "GamerWeb PC" 95}
 "Baldur's Gate II: Shadows of Amn"
  {"Spank!" 90,
   "Da Gameboyz" 98,
   "Gamer's Pulse" 98,
   "CheckOut" 100,
   "Games Radar" 100,
   "FiringSquad" 93,
   "GMR Magazine" 100,
   "GameSpy" 92,
   "Armchair Empire" 95,
   "CNET Gamecenter" 90,
   "All Game Guide" 80,
   "Adrenaline Vault" 100,
   "IGN" 94,
   "Game Over Online" 91,
   "GamePower" 100,
   "PC Gameworld" 98,
   "PC Gamer" 91,
   "Eurogamer" 90,
   "GameSpot" 92,
   "Happy Puppy" 95,
   "Quandary" 100,
   "Game Revolution" 91,
   "Computer Games Magazine" 100,
   "GamePro" 100,
   "ActionTrip" 96,
   "Electric Playground" 95,
   "Antagonist" 98,
   "GameZone" 95,
   "GamerWeb PC" 96,
   "Daily Radar" 100}
 "Sid Meier's Civilization III"
  {"Yahoo! Games" 80,
   "Game Informer" 85,
   "Media and Games Online Network" 90,
   "Computer Gaming World" 100,
   "Games Radar" 100,
   "FiringSquad" 96,
   "GameSpy" 93,
   "TotalGames.net" 93,
   "Next Generation Magazine" 100,
   "Adrenaline Vault" 80,
   "IGN" 93,
   "Game Over Online" 92,
   "PC Gamer" 92,
   "GameSpot" 92,
   "G4 TV" 80,
   "Game Revolution" 91,
   "Computer Games Magazine" 100,
   "Gamers' Temple" 88,
   "Voodoo Extreme" 95,
   "Cincinnati Enquirer" 80,
   "GamePro" 70,
   "ActionTrip" 92,
   "Electric Playground" 70,
   "GameZone" 91}
 "Railroad Tycoon II"
  {"Gamezilla!" 84,
   "Yahoo! Games" 90,
   "TotalGames.net" 92,
   "All Game Guide" 50,
   "Adrenaline Vault" 90,
   "IGN" 89,
   "Game Over Online" 85,
   "PC Gameworld" 97,
   "PC Gamer" 92,
   "GameSpot" 91,
   "Computer Games Magazine" 90}
 "Command & Conquer: Red Alert"
  {"GameSpot" 95,
   "TotalGames.net" 92,
   "PC Gamer" 91,
   "Adrenaline Vault" 90,
   "Yahoo! Games" 90,
   "All Game Guide" 90,
   "Game Revolution" 75}
 "Superbike 2001"
  {"CNET Gamecenter" 80,
   "Hot Games" 100,
   "Adrenaline Vault" 70,
   "IGN" 90,
   "Game Over Online" 70,
   "PC Gamer" 83,
   "GameSpot" 91,
   "Happy Puppy" 90,
   "Computer Games Magazine" 100,
   "ActionTrip" 90,
   "Electric Playground" 90,
   "Daily Radar" 88}})

(defn get-page [link]
  "Parse every page with links into map"
     (->
       (client/get link)
       :body hickory/parse hickory/as-hickory))

(defn hickory-parser 
  "For given link and class, get map"
  [link class]
  (->(s/select 
         (s/child 
           (s/class class))
             (get-page link))))  

(defn hickory-parser-desc
  "For given link and class, get map"
  [link sub-class class]
  (->(s/select 
       (s/descendant
           (s/class sub-class)
           (s/class class))
             (get-page link))))  

(defn get-critics-reviews-link 
  [link]
  (let [content (hickory-parser-desc link "nav_critic_reviews" "action")]
    (str "http://www.metacritics.com"
         (first (map :href
                   (map :attrs content))))))

(defn get-all-critics-data 
  "Get all informations about critics"
  [link]
  (let [critic-name (hickory-parser-desc link "main_col" "source")
        critic-score (hickory-parser-desc link "main_col" "indiv")
        critic-body (hickory-parser-desc link "main_col" "review_body")
        critic-date (hickory-parser-desc link "main_col" "date")]
  (assoc {}
         :name (flatten (map :content (flatten (map :content critic-name))))
         :score (flatten (map :content critic-score))
         :body (flatten (map :content critic-body))
         :date (flatten (map :content critic-date)))))
        
(defn prepare-critics
  "Get map with critics, and prepare them for saving"
  [map]
  (loop [acc []
         score (:score map)
         name (:name map)
         body (:body map)
         date (:date map)]
    (if (empty? score)
      acc
      (recur (conj acc
                   (assoc {} :name (first name) :score (first score) :body (first body) :date (first date))) 
             (rest score) (rest name) (rest body) (rest date)))))

(defn get-game-score [link]
	  (let [content (hickory-parser-desc link "metascore_anchor" "xlarge")]
	    (get 
       (second 
         (first 
           (map :content content))) :content)))

(defn get-game-name [link]
  (let [content
        (s/select 
          (s/child 
            (s/tag :head)
            (s/tag :title))
          (get-page link))]
   ; (map read-string
        (string/replace 
         (first (first 
                 (map :content content))) " for PC Reviews - Metacritic" "")));));)

(defn get-picture-link [link]
  (let [content (hickory-parser link "large_image")]
    (get (get (second 
                (get (first content) :content)) :attrs) :src)))

(defn get-summary-details [link]
  (let [content (hickory-parser-desc link "product_summary" "blurb_collapsed")
        content1 (hickory-parser-desc link "product_summary" "blurb_expanded")]
           (str (first (flatten
                        (map :content
                           content)))
                (first (flatten
                        (map :content
                           content1)))))) 

(def active-agents (atom 0))

(defn get-game 
  [link]
  "Retreive game and prepare it for saving"
  (let [game 
        (assoc {} 
               :name (get-game-name link)
               :score (get-game-score link)
               :picture (get-picture-link link)
               :about (get-summary-details link)
               :critics (prepare-critics (get-all-critics-data (get-critics-reviews-link link))))]
    (save-game game)
    (swap! active-agents dec)
    ))
    
(def site-tree
  "Parse every page with links into map"
  (for [i (range 1)]
          (str "http://www.metacritic.com/browse/games/release-date/available/pc/metascore?page=" i)))
    
(def get-link-for-every-game
  "Get every link from every page"
  (atom (pmap (fn [link]
                (let [content (s/select 
                                (s/child 
                                  (s/class "product_title"))
                                (get-page link))]
                  (map #(str "http://www.metacritic.com" %)    
                       (map :href
                            (map :attrs
                                 (map #(get % 1)
                                      (map :content content)))))))
              site-tree)))

(defn get-games-from-links
  []
  (dorun (map #(let [agent (agent %)]
                 (send agent get-game)
                 (swap! active-agents inc)) 
                   (first @get-link-for-every-game))))



(defn aa []
  (let [agents (doall (map #(agent %) (drop-last 6 (first @get-link-for-every-game))))]
      (doseq [agent agents] 
       (send agent get-game))
 ;   (apply await-for 1000 agents)
   ; (doall (map #(deref %) agents))
   ))

(defn prepare-base []
  (drop-all-data)
  ;(aa)
  )















(defn home-page []
  (layout/common 
        [:h1 (str "Hello " (session/flash-get :username))]
        ;[:h1 (str "Hello " (session/flash-get :name))]
    [:h1 (link-to "/logout" "Logout")]
    [:br]
    ;(form-to [:post "/home"]
    ;(text-field {:placeholder "search"} :name)
    ;(submit-button "Search"))  
    
   ; (clojure.pprint/pprint (get-summary-details "http://www.metacritic.com/game/pc/spider-man-the-movie"))
   ; (clojure.pprint/pprint (nth (get-all-games) 60))
    ;(json/pprint (get-reviews-link "http://www.metacritic.com/game/pc/half-life-2"))
    ;(json/pprint (prepare-critics (get-all-critics-data "http://www.metacritic.com/game/pc/half-life-2/critic-reviews")))
    ;(get-game "http://www.metacritic.com/game/pc/divekick")
     ; (send-agents)
  ;  (aa)    
      ; (clojure.pprint/pprint  (drop-last 6 (first @get-link-for-every-game)))
     ;(get-url "http://www.metacritic.com/game/pc/spider-man-the-movie")   ;   (t)
 

     
    
   ; (clojure.pprint/pprint (shared-critics (first (game-critics)) (second (game-critics))))
    
    ;(clojure.pprint/pprint (shared-critics cla pu))
    
     
     
     
   

 ;(clojure.pprint/pprint (game-critics))
 ;     
      ;(take 10 
        ;   (first @get-link-for-every-game))
      
     ;(clojure.pprint/pprint (dorun
       ;                       (take 10 (first @get-link-for-every-game))
      ; (get-page "http://www.metacritic.com/game/pc/taito-legends")
       
    ;
       ;(clojure.pprint/pprint
       ;(get-all "games")
          [:h1 "Number of games imported: "]
        [:h1 (count (get-all-games))]))


