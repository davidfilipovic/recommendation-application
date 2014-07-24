(ns recommendation-application.routes.home
  (:use ;[recommendation-application.get-data :only [home-page]]
        [recommendation-application.routes.authentication :only [logout]]
        [recommendation-application.models.database :only [get-game-by-name]]
        hickory.core)
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [hiccup.element :refer :all]
            [compojure.core :refer [defroutes GET POST]]))
 


 
(defn layout 
  [left-content & right-content]  
    (html5   
      [:head
       [:meta {:charset "utf=8"}]
      (include-css "/css/main.css")
       (include-css "/css/login.css")
       (include-css "/js/prettyPhoto/css/prettyPhoto.css")
       (include-js "/js/jquery.prettyPhoto.js")
      (include-js "/js/jquery.cycle.all.js")
     (include-js "/js/jquery_1.4.2.js")
      (include-js "/js/easing.js")
     (include-js "/js/tooltip/jquery.tools.min.js")
      (include-js "/js/jqueryui.js")
      (include-js "/js/filterable.pack.js")
     (include-js "/js/jquery.tabs/jquery.tabs.pack.js")
      (include-js "/js/custom.js")]
    (let [user (session/flash-get :username)]
      [:body
      [:div#home-header
       [:div.degree
        [:div.wrapper
         [:div.title-holder
           [:div.title "AQA"]
           [:div.subtitle "kk"]
           [:div.username (str "Wellcome " user)
             (link-to "/logout" "logout")]]]]]      
      [:div#main
       [:div.wrapper
        [:div.home-content
         [:div#slideshow
          [:a {:id "prev" :href "#"}]
          [:a {:id "next" :href "#"}]
          [:a#slideshow-link
           [:span]]
          [:ul#slides
           [:li ;{:style "position: absolute; top: 0px; left: -960px; display: none; z-index: 3; opacity: 1; width: 960px; height: 323px;"} 
         [:img {:src "images/1.jpg"}]]
           [:li; {:style "position: absolute; top: 0px; left: -960px; display: none; z-index: 3; opacity: 1; width: 960px; height: 323px;"} 
         [:img {:src "images/3.jpg"}]]]
          [:div#slideshow-nav-holder
           [:div#slideshow-nav
            [:a {:href "images/2.jpg" :class "activeSlide"}]]]]
         [:div.headline "Game"]
         [:div.shadow-divider]
      [:div.front-left-col
          [:div.bullet-title
           [:div.big "BIG"]
           [:div.small "SMALL"]]
       left-content]
      [:div.front-middle-coll]
         [:div.front-right-col
          [:div.bullet-title
           [:div.big "BIG right"]
           [:div.small "SMALL"]]
       right-content]]]]])))



(defn- get-reviews
  [game-name]
  (let [game (get-game-by-name game-name)]
    (for [review (game :critics)]
        [:div.games-list
         [:br]
         [:div.critic-name
           (review :name)]
         [:br]
         [:div.critic-body
           (review :body)]
         [:br]
          [:div.critic-date 
           [:div.inline
           (review :date)
           [:div.critic-game-score (review :score)]]]
         [:br]])))

(defn get-game-information
  [game-name]
  (let [game (get-game-by-name game-name)
        img (game :picture)]
    [:ul.blocks-holder
     [:li.block
      [:img {:src img :class "thumb"}]]
     ]))

;(defn game-content
  ;[game])

;(def d (parse "index.html"))

(defn home-page1 []
  (layout 
   ; [:h1 (str "Hello " (session/flash-get :username))]
   ;[:h1 (str "Hello " (session/flash-get :name))]
  ;[:h1 (link-to "/logout" "Logout")]
 
  (get-game-information "Diablo")
   (get-reviews "Diablo")
  
   )
  )    
       
(defroutes home-routes
  (GET "/home" [] (home-page1))
  (GET "/logout" [] (logout))
  ;(POST "/home" [name] (home-page name))
  )

 
