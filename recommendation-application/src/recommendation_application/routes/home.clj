(ns recommendation-application.routes.home
  (:use [recommendation-application.get-data :only [home-page]]
        [recommendation-application.routes.authentication :only [logout]]
        [recommendation-application.models.database :only [get-game-by-name update-game save-game get-all-games]]
       [recommendation-application.recommendations :only [;recommend-games-for-game
                                                               pearson-correlation]]
        recommendation-application.get-data                                         
        [hiccup.form :only [form-to label text-area submit-button text-area text-field]]     
        [clj-time.core :only [now]]
        hickory.core)
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [hiccup.element :refer :all]
             [clojure.string :as string]
             [recommendation-application.views.layout :as layout]
            [clj-time.format :as format]
            [compojure.core :refer [defroutes GET POST]]))

(def search-box [:div.top-search
                 [:div#searchform
                  (form-to [:post "/games"]       
                           (text-field {:name "game" :id "s" :placeholder "Search game..."} "")                     
                           [:div#searchsubmit
                            (submit-button {:name "submit", :id "searchsubmit"} "")])]])




(defn right-col [right-content]
  [:div.front-right-col
   [:div.front-right-col-title
   [:div.bullet-title
    [:div.big "Critics"]
    [:div.small "List of acclaimed games critics"]]]  
  [:div.front-right-col-games
   right-content]])



(defn- get-game1 [name]
  (let [game (get-game-by-name name)]
    game))

(defn- get-reviews
  [game-name]
  (let [game (get-game1 game-name)
        reviews  ;(sort-by :date 
        (reverse (game :critics))]
    (for [review reviews]
      [:div.games-list
       [:br]
       [:div.critic-name
        (review :name)]
       [:div.critic-date (review :date)]
       [:br]
       [:div.critic-body
        (review :body)]
       [:br]
       [:div.critic-game-score (review :score)]
       [:br]])))

(defn get-game-information
  [game-name]
  (let [game (get-game1 game-name)
        img (game :picture)]
    [:table
     [:ul.blocks-holder
      [:li.block
       [:img {:src img :class "thumb"}]]
      [:li.block-data 
       [:div.critic-game-score
        [:div.main (map read-string 
                       (game :score))]]]   
      [:li.block-data-h
       (str "Based on " (count (game :critics)) " critics reviews.")]]
     [:tr
      [:th "About: "]
      [:td (game :about)]]
     [:div.pub-name "Publisher: "
      [:br][:br]
      "Release date: "
      [:br][:br]
      "Genre: "
      [:br][:br]
      "Rating: "]]
    ))



(defn show-add-review-box [game]
  (form-to [:post "/addnewreview"]
           [:div#reviewForm
            [:fieldset        
             [:p                    
              [:label "Rate game:*"]   
              (let [rating (if (nil? (session/get :rating)) 0 (session/get :rating))]
                [:ul.rating
                 [:li.new-rating {:style (str "width:"  (* 23  rating) "px;")}]
                 [:li [:a.one {:href (str "/new-rating/" game "&1"), :title "1 star"} "1"]]
                 [:li [:a.two {:href (str "/new-rating/" game "&2"), :title "2 stars"} "2"]]
                 [:li [:a.three {:href (str "/new-rating/" game "&3"), :title "3 stars"} "3"]]
                 [:li [:a.four {:href (str "/new-rating/" game "&4"), :title "4 stars"} "4"]]
                 [:li [:a.five {:href (str "/new-rating/" game "&5"), :title "5 stars"} "5"]]])] 
             [:p                    
              [:label "Title:*"]
              (text-field :title)]
             [:p       
              [:label "Comments:"]    
             (text-area :review)]
            (if-not (nil? (session/get :error-message-rev))
             [:p
               [:div.warning-g
                [:label (session/get :error-message-rev)]]])
             [:p               
              (submit-button {:name "submit", :id "submit"} "Add")
               ]]]))

(defn list-of-games 
  [page]
  (let [games (partition 10 (get-all-games))
        last-page (count games)]
    [:div#genId
     ;[:br]
     [:div.headline "List of all games"]
     [:div.shadow-divider]
     [:div.list-all
      [:div.front-left-col-games  
       (for [game (butlast (nth games (dec page)))]
         (let [img (game :picture)]
           [:div.games-list-left
            [:ul.image-holder
             [:li.block
              [:a {:href (str "/games/" (game :name))}
               [:img {:src img :class "thumb"}]]]]
            [:div.game-name 
             (game :name)] 
            [:div.game-date 
             ;(game :release-date)
             "asdasd"]
            [:br][:br][:br]
            [:div.game-score
             (map read-string (game :score))]]))
       (let [game
             (last
               (nth games (dec page)))
             img (game :picture)]
         [:div.games-list-left-last
          [:ul.image-holder
           [:li.block
            [:a {:href (str "/games/" (game :name))}
             [:img {:src img :class "thumb"}]]]]
          [:div.game-name 
           (game :name)] 
          [:div.game-date 
           ;(game :release-date)
           "asdasd"]
          [:br][:br][:br]
          [:div.game-score
           (map read-string (game :score))]])]
      
      [:p {:class "clear"}]
      [:div.pager
       [:ul
        {:class "portfolio-pager"}       
        [:div.first
         [:a               
          {:href "/home&1#genId"} "First"]]       
        [:div.previous
         (if-not (= 1 page)
           [:a
            {:href (str "/home" "&" (dec page) "#genId")}])]      
        [:div.middle
         page]          
        [:div.next
         (if-not (= page last-page)
           [:a
            {:href (str "/home" "&" (inc page) "#genId")}])]             
        [:div.last
         [:a
          {:href (str "/home&" last-page "#genId")} "Last"]]]]]]))

(def date-form 
  (format/formatter "MMM dd, yyyy"))

(defn verify-review [title review rating]
  (cond 
    (or (nil? rating) (zero? rating)) "Please rate game."
    (or (= "" title) (nil? title)) "Please provide a title."
    :else true))

(defn add-review-to-site 
  [title rating review]
  (let [message (verify-review title review rating)
        game (get-game-by-name
                   (session/get :game))]
    (if (string? message)
      (do
        (session/put! :error-message-rev message)
        (redirect (str "/games/" (game :name) "#reviewForm")))
      (let [user (session/get :review)
            
            game-c (count (game :critics))
            num-of-critics (count (game :critics))
            new-critic (merge game 
                              {:critics (conj (game :critics) 
                                              (assoc {} 
                                                     :name title
                                                     :body review
                                                     :date (format/unparse date-form
                                                                           (new org.joda.time.DateTime (.toDate (now))
                                                                                (org.joda.time.DateTimeZone/forID "UTC")))
                                                     :score (Integer/valueOf rating)))}
                              {:score [(str (int
                                              (quot (+ 
                                                    (* num-of-critics 
                                                       (first (map read-string 
                                                                   (game :score)))) 
                                                    rating) 
                                                   (inc num-of-critics))))]})]  
        (do
          (update-game game new-critic)
          (session/remove! :game)
          (session/remove! :rating)
          (session/remove! :error-message-rev)
          (session/flash-put! :sort 1)
          (redirect (str "/games/" (game :name))))))))












#_(defn left-col 
  [recommendations
   ;left-content 
    ;add-rev
    ]
  [:div.front-left-col
   [:div.bullet-title
    [:div.big "Game"]
    [:div.small "Description"]]
   ;left-content
    [:div.headline-h "People who liked this game, also liked: "]
   recommendations
   [:div.headline-h "Add review"]
   ;add-rev
    ])


#_(defn show-recommendations [game-name]
   (let [games (take 15 (recommend-games-for-game game-name))]
     (if (not= 0 (count games))
       [:div.front-left-down-col 
        (for [name games]
         (let [game (get-game-by-name (first name))
               src (game :picture)
               g-name (game :name)]
             [:a {:href (str "/games/" g-name)}[:img {:src src :class "thumb-d"}]]
             ))])))

#_(defn layout 
  [;recommendations 
   ;left-content
    ;right-content
   ; add-rev
    ;page
    ]  
  (html5   
    [:head
     [:meta {:charset "utf=8"}]
     
      (include-css "/css/main.css")
      (include-css "/js/prettyPhoto/css/prettyPhoto.css")
      
     (include-js "/js/jquery_1.4.2.js")
      (include-js "/js/jqueryui.js")
      (include-js "/js/easing.js")
      (include-js "/js/jquery.cycle.all.js")
      (include-js "/js/jquery.tools.min.js")
      (include-js "/js/filterable.pack.js") 
      (include-js "/js/prettyPhoto/js/jquery.prettyPhoto.js")
      (include-js "/js/jquery.tabs.pack.js")
     ; (include-js "/js/custom.js")
      (include-css "/css/login.css") 
      (include-js "/js/cufon-yui.js")
      (include-js "/js/piecemaker/swfobject/swfobject.js")]
      
    (let [user (session/get :username)]     
      [:body
       
       [:div#home-header
        [:div.degree
         [:div.wrapper
          [:div.title-holder
             [:div.title "Games recommendation"]
             [:div.username (str "Wellcome, " user ". ")
              (link-to "/logout" "Logout")]]
          (identity search-box)]]]
       
       [:div#main
        
        [:div.wrapper
         [:div.home-content  
          
          [:div
           {:id "slideshow"}
           [:img {:src "/images/arthas.jpg" :width "990px"}]]
          
           #_[:div.headline (session/get :game)] 
           
          ; (list-of-games page)             
          
           ;(left-col ;left-content 
                        ; recommendations
                        ;add-rev
                        ;)        
           ;[:div.front-middle-coll]
           ;(right-col right-content)
           ]]]
       
       [:div#footer
        [:div.degree]]
       
       [:div#bottom
        [:div.wrapper (str "© Copyright 2014. All Rights Reserved")]]])))

#_(defn show-game [game]
  (layout 

   ;(clojure.pprint/pprint

  
  
  
  
   ;(show-recommendations game)    
  
  
  
  
  
  
  
   ;(get-game-information game)
   ;(get-reviews game)
   ;(show-add-review-box game)
  

  
  ; (clojure.pprint/pprint 
   ;(map read-string 
   ;(get-game-by-name "Diablo"))
   
   ;(save-game diablo)
   ;(show-recommendations "Diablo")
   ; 
    ;(clojure.pprint/pprint(format/show-formatters))
    ;(clojure.pprint/pprint (as-hiccup r))
    ))  



#_(defn show-page [page]
   (layout 
     page))


(defn home-page1 []
  (layout/common 
    
    ;(pearson-correlation (game-critics) "Diablo" "Dota 2")
    ;(clojure.pprint/pprint (prepare-critics (get-all-critics-data "http://www.metacritic.com/game/pc/dota-2")))
   
    ;(clojure.pprint/pprint (game-critics))
    
   ; (aa)
    
    (bb)
    ;(get-game "http://www.metacritic.com/game/pc/sanctum-2")
    
    
    ;(clojure.pprint/pprint (take 5 @get-link-for-every-game))
    
   ;(for [game (take 10 (get-all-games))]
     ;(into {} (:critics game)))
   
    [:h1 "Number of games imported: "]
     [:h1 (count (get-all-games))]
    ))

(defroutes home-routes
  #_(GET "/home" [] (do (session/put! :game "Sanctum 2")
                      (show-game "Sanctum 2")))
  #_(GET "/home&:page" [page] (show-page (Integer/valueOf page)))
  (GET "/home" [] (home-page1))
  (GET "/logout" [] (logout))
  #_(GET "/games/:game" [game] 
        (do (session/put! :game game)
          (show-game game)))
  (GET "/new-rating/:game&:rvalue" [game rvalue]
       (session/put! :rating (Integer/valueOf rvalue))
       (redirect (str "/games/" game "#reviewForm")))
  (POST "/addnewreview" 
        [title review]  
        (add-review-to-site title (session/get :rating) review))
  (POST "/games" [game]
        (redirect (str "/games/" game))))
 


   #_[:object
             {:type "application/x-shockwave-flash",
              :data "js/piecemaker/piecemaker.swf",
              :width "800",
              :height "600"}
             "\n\t\t\t\t"
             "<!--<![endif]-->"
             "\n\t\t\t\t\t"
             [:a
              {:href "http://www.adobe.com/go/getflashplayer"}
              "\n\t\t\t\t\t\t"
              [:img
               {:src
                "http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif",
                :alt "Get Adobe Flash player"}]
              "\n\t\t\t\t\t"]
             "\n\t\t\t\t"
             "<!--[if !IE]>-->"
             "\n\t\t\t\t"]
 

