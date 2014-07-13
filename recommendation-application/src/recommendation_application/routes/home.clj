(ns recommendation-application.routes.home
  (:use [recommendation-application.routes.authentication :only [logout]])
  (:require [compojure.core :refer :all]
             [hickory.core :as hickory]
            [recommendation-application.views.layout :as layout]
         [recommendation-application.get_data :only [t]]
         [noir.session :as session]
         [hiccup.element :refer :all]
         [hickory.select :as s]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-http.client :as client]))
 


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


(defn get-json 
  "Extract part of the site from the link, in json format."
  [link part]
     (slurp (str "http://webscrapemaster.com/api/?url=" link "&xpath=" part)))

(defn get-specified-part
  [link part]
  (get-json link part))

(defn get-user-reviews
  [link]
  (get-json link "//div[@id=Reviewsall0]"))

;(get-json "http://store.steampowered.com/app/730/?snr=1_7_7_230_150_1" 
 ;               "//div[@id=game_area_description]" 
  ;              "//div[@id=game_area_sys_req]"
   ;             "//div[@id=Reviewsall0]" "//div[@id=game_area_metascore]")
 
   
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
  (let [content (hickory-parser link "game_header_image_ctn")]
   (second 
     (map :src
          (map :attrs
               (get (first content) :content))))))
   
(defn get-game-score
  [link]
  "Get score for the game"
   (let [content (get-json link "//div[@id=game_area_metascore]")]
   (apply str 
          (take 2 
                (filter #(string? %)
                        (map #(re-matches #"[0-9]" (str %)) content))))))

(defn get-game 
  [link]
  "Retreive game and prepare it for saving"
  (let [about-game-and-reviews]))
   


         
(defn home []
  (layout/common 
        [:h1 (str "Hello " (session/flash-get :username))]
        [:h1 (str "Hellssso " (session/flash-get :name))]
        [:h1 (link-to "/logout" "Logout")]
        ;(get-ratings "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1")
        ;(first @get-link-for-every-game)
       ; (get-ratings "http://store.steampowered.com/app/730/?snr=1_7_7_230_150_1")
       [:br]
       [:br]
      ;(first (reviews "http://store.steampowered.com/app/440/?snr=1_7_7_230_150_1"))
      ;;(json/pprint (get-link-for-picture "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1"))
      ;(json/pprint (get-logo "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1"))
      ;(json/pprint (hickory-parser "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1" "screenshot_holder"))
      (json/pprint (get-game-score "http://store.steampowered.com/app/440/?snr=1_7_7_230_150_1"))
))
        
(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" [] (logout)))

 
