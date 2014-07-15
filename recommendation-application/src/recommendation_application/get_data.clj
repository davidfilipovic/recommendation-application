(ns recommendation-application.get-data
  (:use [recommendation-application.models.database :only [save-game empty-db get-game-by-name get-all get-by-score]]
        [recommendation-application.mongo :only [get-books]])
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
            [clj-http.client :as client]))
 
(def site-htree
  "Parse every page with links into map"
   (for [i (range 26)]
     (->
     (client/get 
       (s/select 
         (s/child 
           (s/class "product_title"))
         (str "http://www.metacritic.com/browse/games/genre/date/strategy/pc?view=condensed&page=" i))) 
     :body parse as-hickory)))


(defn get-game-score
  [link]
  "Get score for the game"
   (let [content (get-json link "//div[@id=game_area_metascore]")]
   (apply str 
          (take 2 
                (filter #(string? %)
                        (map #(re-matches #"[0-9]" (str %)) content))))))

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
    (map :href
         (map :attrs content))))

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
    (string/replace (first (map :content content)) " for PC Reviews - Metacritic" "")))

(defn get-picture-link [link]
  (let [content (hickory-parser link "large_image")]
    (get (get (second 
                (get (first content) :content)) :attrs) :src)))

(defn get-sumary-details [link]
  (let [content (hickory-parser-desc link "product_summary" "data")]
    (get (second (flatten (map :content content))) :content)))

(defn get-game 
  [link]
  "Retreive game and prepare it for saving"
  (let [game 
        (assoc {} 
               :name (get-game-name link)
               :score (get-game-score link)
               :picture (get-link-for-picture link))]
    (save-game game)))

(defn home-page 
 ([]
  (layout/common 
        [:h1 (str "Hello " (session/flash-get :username))]
        ;[:h1 (str "Hello " (session/flash-get :name))]
        [:h1 (link-to "/logout" "Logout")]
        [:br]
        ;(form-to [:post "/home"]
        ;(text-field {:placeholder "search"} :name)
        ;(submit-button "Search"))  
        ;(json/pprint (get-reviews-link "http://www.metacritic.com/game/pc/half-life-2"))
        ;(json/pprint (prepare-critics (get-all-critics-data "http://www.metacritic.com/game/pc/half-life-2/critic-reviews")))
        (json/pprint (get-sumary-details "http://www.metacritic.com/game/pc/divekick"))
       ))
 ([name]
  (layout/common 
 [:h1(:score (get-game-by-name name))])))













