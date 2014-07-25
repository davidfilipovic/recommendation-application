(ns recommendation-application.get-data
  (:use [recommendation-application.models.database :only [save-game empty-db get-game-by-name get-all get-all-games get-by-score drop-all-data]]
       ; [recommendation-application.recommendations :only [recommend-games-for-game]]
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

(defn game-critics []
  (apply merge {}
       (for [game (get-all-games)] 
         (assoc {} (:name game) 
                (into {} (for [critic (:critics game)]
                           (assoc {} (:name critic) (read-string (:score critic)))))))))

(defn shared-critics [data first-game second-game]
 (let [first-critic (data first-game)
       second-critic (data second-game)]
   (apply merge {}
      (for [k (keys first-critic)
                        :when (contains? second-critic k)]
                    (assoc {} k [(first-critic k) (second-critic k)])))))

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
 
   ; (clojure.pprint/pprint (recommend-games-for-game "Diablo"))
     ;(clojure.pprint/pprint (pearson-correlation lisa gene))
     
    ; (clojure.pprint/pprint (shared-critics critics "Toby" "Gene Seymour"))
    
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









