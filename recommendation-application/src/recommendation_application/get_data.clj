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
 
(defn get-json 
  "Extract part of the site from the link, in json format."
  [link & part]
     (slurp (str "http://webscrapemaster.com/api/?url=" link "&xpath=" (first part) "," (second part))))

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


(defn reviews [link]
  "Parse every page with links into map"
     (->
       (s/select 
         (s/child 
           (s/class "block_content_inner")
           (s/tag :div) 
           (s/id "game_area_metascore"))
         (get-page link))))

(defn hickory-parser 
  "For given link and class, get map"
  [link class]
  (->(s/select 
         (s/child 
           (s/class class))
             (get-page link))))  


(defn get-specified-part
  [link part]
  (get-json link part))

(defn get-user-reviews
  [link]
  (get-json link "//div[@id=Reviewsall0]"))
   
(defn get-link-for-picture
  "For given game link, return picture"
  [link]
  (let [content (hickory-parser link "screenshot_holder")]
    (second 
      (map :href 
           (second  
             (second (get (first content) :content)))))))

(defn get-logo [link]
  "Get game logo"
  (let [content (hickory-parser link "game_header_image_ctn" "")]
   (second 
     (map :src
          (map :attrs
               (get (first content) :content))))))


(defn get-about-game
  [link]
  (let [content (hickory-parser link "game_area_description")]
    content))

(defn get-game 
  [link]
  "Retreive game and prepare it for saving"
  (let [game 
        (assoc {} 
               :name "SH"
               :score (get-game-score link)
               :logo (get-logo link)
               :picture (get-link-for-picture link))]
    (save-game game)))


(defn get-critics-reviews-link 
  [link]
  (let [content (hickory-parser link "see_all")]
  (first (map :href (map :attrs (get (first content) :content))))))



(defn- get-user-rating 
  "Extract user rating from page-link."
  [page-link]
  (let [content (s/select (s/child (s/class "desktop")
                                   (s/tag :head)
                                   (s/attr :itemprop #(= % "ratingValue")))
                          (hickory/as-hickory (hickory/parse (slurp page-link))))]
    (first (map :content (map :attrs content)))))

(defn get-score [content]
  (map :content content))

(defn hickory-parser-desc
  "For given link and class, get map"
  [link class1 class]
  (->(s/select 
       (s/descendant
           (s/class class1)
           (s/class class))
             (get-page link))))  


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
        
       ))
 ([name]
  (layout/common 
 [:h1(:score (get-game-by-name name))])))














