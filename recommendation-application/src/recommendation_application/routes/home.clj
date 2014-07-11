(ns recommendation-application.routes.home
  (:use [recommendation-application.routes.authentication :only [logout]]
        [hickory.core])
  (:require [compojure.core :refer :all]
            [recommendation-application.views.layout :as layout]
            [recommendation-application.get_data :only [t]]
            [noir.session :as session]
            [hiccup.element :refer :all]
            [hickory.select :as s]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-http.client :as client]))
 


(def site-htree
  "Parse every page with links into map"
  (for [i (range 25)]
     (->
       (client/get 
       (str "http://store.steampowered.com/search/?term=#sort_by=&sort_order=ASC&os=win&page=" i))
       :body parse as-hickory)))

(def get-link-for-every-game
  "Get every link from every page"
  (atom  (pmap (fn [link] 
                 (let [content (s/select 
                                (s/child 
                                  (s/class "search_result_row"))
                                link)]
              ;    (map #(str "http://www.metacritic.com/game/pc" %)    
                            ;  (map :href
                                   (map :href
                                      (map :attrs content))))
               site-htree)))
                    

(defn json-request
  "Take a URL, return json object and parse it into map"
  [url]
  (with-open [stream (.openStream (java.net.URL. url))]
    (let [buffer (java.io.BufferedReader. (java.io.InputStreamReader. stream))]
      (json/read buffer :key-fn keyword)))) 

(defn get-ratings
  "Extract ratings from specified page"
  [game-page]
    (let [content (s/select 
                     (s/child 
                         (s/class "highlight_metascore"))
                     (as-hickory (parse game-page)))]
      content))

(defn- get-json 
  "Extract microdata from specified link in json format."
  [link]
     (slurp (str "http://www.w3.org/2012/pyMicrodata/extract?format=json&uri=" link)))


(defn home []
  (layout/common 
   [:h1 (str "Hello " (session/flash-get :username))]
   [:h1 (str "Hellssso " (session/flash-get :name))]
   [:h1 (link-to "/logout" "Logout")]
 ;  (get-ratings "http://www.metacritic.com/game/pc/company-of-heroes-2-the-western-front-armies")
   (first @get-link-for-every-game)
  ; (first site-htree)
  ;(get-json  "json&uri=http://www.metacritic.com/game/pc/company-of-heroes-2-the-western-front-armies")
 ; (json-request "https://byroredux-metacritic.p.mashape.com/find/game&X-Mashape-Key=mNunwER4fvmshDm1rHWDGXJaJQesp1KYNb1jsnpYim82Rh9lOL") 
  [:br]
   [:br]
   ))
  
(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" [] (logout)))


