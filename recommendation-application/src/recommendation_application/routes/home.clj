(ns recommendation-application.routes.home
  (:use ;[recommendation-application.get-data :only [home-page]]
        [recommendation-application.routes.authentication :only [logout]]
        [recommendation-application.models.database :only [get-game-by-name update-game]]
        [recommendation-application.recommendations :only [recommend-games-for-game]]
        [hiccup.form :only [form-to label text-area submit-button]]
        [clj-time.format :only [show-formatters formatter]]
        [clj-time.core :only [now]]
        hickory.core
       )
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [hiccup.element :refer :all]
            [compojure.core :refer [defroutes GET POST]]))
 

(defn layout 
  [;recommendations 
   left-content right-content add-rev]  
    (html5   
      [:head
       [:meta {:charset "utf=8"}]
       (include-css "../css/main.css")
       (include-css "../css/login.css")
       (include-css "../js/prettyPhoto/css/prettyPhoto.css")
       (include-js "../js/piecemaker/swfobject/swfobject.js")
       (include-js "../js/jquery.prettyPhoto.js")
       (include-js "../js/jquery.cycle.all.js")
       (include-js "../js/jquery_1.4.2.js")
       (include-js "../js/easing.js")
       (include-js "../js/tooltip/jquery.tools.min.js")
       (include-js "../js/jqueryui.js")
       (include-js "../js/filterable.pack.js")
       (include-js "../js/jquery.tabs/jquery.tabs.pack.js")
       (include-js "../js/custom.js")
       [:script
        {:type "text/javascript"}
        "\n// <![CDATA[\nvar flashvars = {};
         \nflashvars.cssSource = \"../js/piecemaker/piecemaker.css\";
         \nflashvars.xmlSource = \"../js/piecemaker/piecemaker.xml\";
         \nflashvars.imageSource = \"../images/\";
         \nvar params = {};
         \nparams.play = \"false\";\nparams.menu = \"false\";
         \nparams.scale = \"showall\";\nparams.wmode = \"transparent\";
         \nparams.allowfullscreen = \"true\";\nparams.allowscriptaccess = \"sameDomain\";
         \nparams.allownetworking = \"all\";
         \nswfobject.embedSWF('../js/piecemaker/piecemaker.swf', 'slideshow-3d', '960', '430', '10', null, flashvars, params, null);\n// ]]>\n"]
       ; [:style
       ;{:type "text/css", :media "screen"}
       ; "#slideshow-3d {visibility:hidden}"]
       ]
      (let [user (session/get :username)]
           
        [:body
         [:div#home-header
          [:div.degree
           [:div.wrapper
            [:div.title-holder
             [:div.title "Games recommendation"]
             [:div.username (str "Wellcome, " user)
              (link-to "/logout" "logout")]]]]]      
         [:div#main
          [:div.wrapper
           [:div.home-content
                       
            [:div
             {:id "slideshow"}
             ; [:a
             ;  {:href "http://www.adobe.com/go/getflashplayer"}
             ; [:img {:src "images/get_flash_player.gif", :alt ""}]]]
             [:img {:src "../images/arthas.jpg" :width "1000px"}]]
            
         
              [:div.headline (session/get :game)]
            [:div.shadow-divider]
            [:div.front-left-col
             [:div.bullet-title
              [:div.big "Game"]
              [:div.small "Description"]]
             left-content
             [:div.headline-h "People who liked this game, also liked: "]
            ;; recommendations
             [:div.headline-h "Add review"]
             add-rev
             ]
            
            [:div.front-middle-coll]
            [:div.front-right-col
             [:div.bullet-title
              [:div.big "Critics"]
              [:div.small "List of acclaimed games critics"]]
             right-content]
           ; [:div.clear]
            ]]]])))

(defn- get-game [name]
  (let [game (get-game-by-name name)]
    game))

(defn- get-reviews
  [game-name]
  (let [game (get-game game-name)]
    (for [review (game :critics)]
      [:div.games-list
       [:br]
       [:div.critic-name
        (review :name)]
       [:div.critic-date (review :date)]
       [:br] ; ];(
       [:div.critic-body
        (review :body)]
       [:br]
       [:div.critic-game-score (review :score)]
       [:br]])))

(defn get-game-information
  [game-name]
  (let [game (get-game game-name)
        img (game :picture)]
    [:table
     [:ul.blocks-holder
      [:li.block
       [:img {:src img :class "thumb"}]]
      [:li.block-data 
       [:div.critic-game-score
        [:div.main (map read-string (game :score))]]]   
      [:li.block-data-h
       (str "Based on " (count (game :critics)) " critics reviews.")]]
     [:tr
      [:th "About: "]
      [:td (game :about)]]
     [:div.pub-name "Publisher:"
      [:br][:br]
      "Release date:"
      [:br][:br]
      "Genre: "
      [:br][:br]
      "Rating: "]]
    ))

(defn show-recommendations [game-name]
  (let [games (take 15 (recommend-games-for-game game-name))]
    (if (not= 0 (count games))
      [:div.front-left-down-col 
       (for [name games]
        (let [game (get-game-by-name (first name))
              src (game :picture)
              g-name (game :name)]
            [:a {:href (str "/games/" g-name)}[:img {:src src :class "thumb-d"}]]
            ))])))

(defn show-add-review-box [game]
  (form-to [:post "/addnewreview"]
           [:div#reviewForm
            [:fieldset        
             [:p                    
              [:label "Rate game:"]   
              (let [rating (if (nil? (session/get :rating)) 0 (session/get :rating))]
                [:ul.rating
                 [:li.new-rating {:style (str "width:"  (* 23  rating) "px;")}]
                 [:li [:a.one {:href (str "/new-rating/" game "&1"), :title "1 star"} "1"]]
                 [:li [:a.two {:href (str "/new-rating/" game "&2"), :title "2 stars"} "2"]]
                 [:li [:a.three {:href (str "/new-rating/" game "&3"), :title "3 stars"} "3"]]
                 [:li [:a.four {:href (str "/new-rating/" game "&4"), :title "4 stars"} "4"]]
                 [:li [:a.five {:href (str "/new-rating/" game "&5"), :title "5 stars"} "5"]]])] 
             [:p                    
              [:label "Title:"]
              [:input {:name "name", :id "name", :type "text"}]]
             [:p       
              [:label "Comments:"]    
              [:textarea
               {:name "comments", :id "comments", :rows "5", :cols "20"}]]              
             [:p               
               (submit-button {:name "submit", :id "submit"} "Add");
               ]
             (when-let [message (string? (session/get :error-message))] 
             [:p#error.warning
              [:label message]])
             ]]))

(def date-form 
  (formatter "MMM dd yyyy"))

(defn verify-review [title review rating]
  (cond 
    (nil? title) "Please provide a name."
    (nil? review) "Please insert a review."
    (nil? rating) "Please rate game."
    :else true))

(defn prepare-ratings 
  [rating]
  (cond 
    (= rating 1) "sad"
    :else true))

(defn add-review-to-site 
  [title rating review]
  (let [user (session/get :review)
        game (get-game-by-name (session/get :game))
        game-c (count (game :critics))
        num-of-critics (count (game :critics))
        new-critic (merge game 
                          {:critics (conj (game :critics) 
                                          (assoc {} 
                                                 :name title
                                                 :body review
                                                 :date  (.toDate (now))
                                                 :score (Integer/valueOf rating)))
                           :score (/ (+ 
                                       (* num-of-critics (first (map read-string (first (game :score))))) 
                                       rating) (inc num-of-critics))})]
    (let [message (verify-review title review rating)]
      (if (string? message)
        (do
          (session/flash-put! :error-message message)
          (redirect (str "/games" game "#reviewForm")))
        (do
          (update-game game new-critic)
          (session/remove! :game)
          (session/remove! :rating)
          (redirect (str "/games" game)))))))

(def r (parse ""))

(defn show-game [game]
  (layout 
    ; [:h1 (str "Hello " (session/flash-get :username))]
   ;[:h1 (str "Hello " (session/flash-get :name))]
   ;[:h1 (link-to "/logout" "Logout")]
    
   ;(clojure.pprint/pprint
    
   ;(show-recommendations game)
   (get-game-information game)
   (get-reviews game)
   (show-add-review-box game)
    
   ;(clojure.pprint/pprint 
   ;(map read-string 
   ;(get-game-by-name "Diablo"))
   ;
   ;(show-recommendations "Diablo")
  ; 
   ;(clojure.pprint/pprint(format/show-formatters))
   ;(clojure.pprint/pprint (as-hiccup r))
   ))    


(defroutes home-routes
  (GET "/home" [] (do (session/put! :game "Diablo")
                         (show-game "Diablo")))
  (GET "/logout" [] (logout))
  (GET "/games/:game" [game] 
       (do (session/put! :game game)
         (show-game game)))
  (GET "/new-rating/:game&:rvalue" [game rvalue]
       (session/put! :rating (Integer/valueOf rvalue))
       (redirect (str "/games/" game "#reviewForm")))
  (POST "/addnewreview" 
        [title review]  
        (add-review-to-site title (session/get :rating) review)))



