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
   (for [i (range 26)]
     (->
       (client/get 
         (str "http://www.metacritic.com/browse/games/genre/date/strategy/pc?view=condensed&page=" i))
       :body parse as-hickory)))

(def get-link-for-every-game
  "Get every link from every page"
  (atom (pmap (fn [link]
                (let [content (s/select 
                                (s/child 
                                  (s/class "product_title"))
                                link)]
                  (map #(str "http://www.metacritic.com/game/pc" %)    
                       (map :href
                            (map :attrs
                                 (map #(get % 1)
                                      (map :content content)))))))
              site-htree)))

(defn home []
  (layout/common 
   [:h1 (str "Hello " (session/flash-get :username))]
   [:h1 (str "Hellssso " (session/flash-get :name))]
   [:h1 (link-to "/logout" "Logout")]
   (second @get-link-for-every-game)
  ; (first site-htree)
   [:br]
   [:br]
   ))
  
(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" [] (logout)))


