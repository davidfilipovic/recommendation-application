(ns recommendation-application.routes.home
  (:use ;[recommendation-application.get-data :only [home-page]]
        [recommendation-application.routes.authentication :only [logout]]
        [recommendation-application.models.database :only [get-game-by-name]]
        [recommendation-application.recommendations :only [recommend-games-for-game]]
        hickory.core)
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [hiccup.element :refer :all]
            [compojure.core :refer [defroutes GET POST]]))
 

(defn layout 
  [recommendations left-content & right-content]  
    (html5   
      [:head
       [:meta {:charset "utf=8"}]
       (include-css "../css/main.css")
       (include-css "css/login.css")
       (include-css "../js/prettyPhoto/css/prettyPhoto.css")
       (include-js "js/piecemaker/swfobject/swfobject.js")
       (include-js "js/jquery.prettyPhoto.js")
       (include-js "../js/jquery.cycle.all.js")
       (include-js "js/jquery_1.4.2.js")
       (include-js "js/easing.js")
       (include-js "js/tooltip/jquery.tools.min.js")
       (include-js "js/jqueryui.js")
       (include-js "js/filterable.pack.js")
       (include-js "js/jquery.tabs/jquery.tabs.pack.js")
       (include-js "js/custom.js")
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
       [:style
        ;{:type "text/css", :media "screen"}
       "#slideshow-3d {visibility:hidden}"]
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
            ;[:div#slideshow
       ; [:a {:id "prev" :href "#"}]
       ; [:a {:id "next" :href "#"}]
       ; [:a#slideshow-link
       ;  [:span]]
       ;  [:ul#slides
       ;   [:li ;{:style "position: absolute; top: 0px; left: -960px; display: none; z-index: 3; opacity: 1; width: 960px; height: 323px;"} 
       ;  [:img {:src "images/1.jpg"}]]
       ;    [:li; {:style "position: absolute; top: 0px; left: -960px; display: none; z-index: 3; opacity: 1; width: 960px; height: 323px;"} 
       ;   [:img {:src "images/3.jpg"}]]]
       ;    [:div#slideshow-nav-holder
       ;    [:div#slideshow-nav
       ;    [:a {:href "images/2.jpg" :class "activeSlide"}]]]]
       [:div
    {:id "slideshow-3d"}
    [:a
     {:href "http://www.adobe.com/go/getflashplayer"}
     [:img {:src "images/get_flash_player.gif", :alt ""}]]]
 ;      [:object
    ;{:type "application/x-shockwave-flash",
  ;   :data "js/piecemaker/piecemaker.swf",
  ;   :width "960",
  ;   :height "430",
  ;   :id "slideshow-3d",
 ;    :style "visibility: visible;"}
 ;   "\n"
  ;  [:param {:name "play", :value "false"}]
   ; [:param {:name "menu", :value "false"}]
    ;[:param {:name "scale", :value "showall"}]
;    [:param {:name "wmode", :value "transparent"}]
 ;   [:param {:name "allowfullscreen", :value "true"}]
  ;  "\n"
   ; [:param {:name "allowscriptaccess", :value "sameDomain";}]
;    "\n"
 ;   [:param {:name "allownetworking", :value "all"}]
 ;   "\n"
  ;  [:param
   ;  {:name "flashvars",
  ;    :value
    ;  "cssSource=js/piecemaker/piecemaker.css&xmlSource=js/piecemaker/piecemaker.xml&imageSource=images/"}]]
    [:div.headline "Game"]
    [:div.shadow-divider]
    [:div.front-left-col
     [:div.bullet-title
      [:div.big "Game"]
      [:div.small "Description"]]
     left-content
     [:div.headline-h "People who liked this game, also liked: "]
     recommendations
    ]
    [:div.front-middle-coll]
    [:div.front-right-col
     [:div.bullet-title
      [:div.big "Critics"]
      [:div.small "List of acclaimed games critics"]]
     right-content]
    [:div.clear]
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
  (let [game (get-game game-name)
        img (game :picture)]
    [:table
     [:ul.blocks-holder
      [:li.block
       [:img {:src img :class "thumb"}]]
      [:li.block-data 
       [:div.critic-game-score
        [:div.main (get game :score)]]]   
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
              src (game :picture)]
            [:img {:src src :class "thumb-d"
                   }];]
            ))])))


(def t (parse "<div id=\"slideshow-3d\"><a href=\"http://www.adobe.com/go/getflashplayer\"><img src=\"img/get_flash_player.gif\" alt=\"\" /></a></div>"))

(defn home-page1 []
  (layout 
   ; [:h1 (str "Hello " (session/flash-get :username))]
   ;[:h1 (str "Hello " (session/flash-get :name))]
   ;[:h1 (link-to "/logout" "Logout")]
   
   ;(clojure.pprint/pprint 
   (show-recommendations "Diablo")
   (get-game-information "Diablo")
   ;(clojure.pprint/pprint
   ;  (get 
   ;                   (get-game-by-name "Diablo") :score);)
   (get-reviews "Diablo")
   ;(show-recommendations "Diablo")
   ; (clojure.pprint/pprint (as-hiccup r))
   ))    
       
(defroutes home-routes
  (GET "/home" [] (home-page1))
  (GET "/logout" [] (logout))
  ;(POST "/home" [name] (home-page name))
  )


