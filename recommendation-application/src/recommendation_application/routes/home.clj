(ns recommendation-application.routes.home
  (:use [recommendation-application.routes.authentication :only [logout]]
        [recommendation-application.models.database :only [get-game-by-name update-game save-game get-all-games drop-all-data]]
        recommendation-application.get-data                                     
        [hiccup.form :only [form-to label text-area submit-button text-area text-field]]
        hickory.core)
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [hiccup.element :refer :all]      
            [clojure.string :as string]
            [compojure.core :refer [defroutes GET POST]]))

(def search-box [:div.top-search
                 [:div#searchform
                  (form-to [:post "/games"]       
                           (text-field {:name "game" :id "s" :placeholder "Search game..."} "")                     
                           [:div#searchsubmit
                            (submit-button {:name "submit", :id "searchsubmit"} "")])]])

(defn verify-search [game]
  (if-not (empty? (filter (fn [x] (= x game)) (map :name (get-all-games))))
    (redirect (str "/games/" game "#all-div"))
    (do
      (session/flash-put! :wrong-search "Sorry, that game does not exists.")
      (redirect (str "/games/" (session/get :game))))))

(defn pagination [page last-page]
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
            {:href (str "/home&" last-page "#genId")} "Last"]]]])

(defn list-of-games 
  [page]
  (let [games (partition 10 (get-all-games))
        last-page (count games)]
    (if-not (empty? games)
      [:div#genId
       [:div.headline {:style "margin-bottom: 15px; margin-top: 1px;"} (str "List of all games")]
       [:div.headline-l (str "There are "(count (get-all-games)) " games currently in database")]
       [:div.shadow-divider]
       [:div.list-all
        [:div.front-left-col-games  
         (for [game (butlast (nth games (dec page)))]
           (let [img (game :picture)]
             [:div.games-list-left
              [:ul.image-holder
               [:li.block
                [:a {:href (str "/games/" (game :name) "#all-div")}
                 [:img {:src img :class "thumb"}]]]]
              [:div.game-name 
               [:a {:href (str "/games/" (game :name) "#all-div")}
                (game :name)]]
              [:div.game-date 
               (game :release-date)]
              [:br][:br][:br]
              [:div.game-score
               (game :score)]]))
         (let [game (last
                      (nth games (dec page)))
               img (game :picture)]
           [:div.games-list-left-last
            [:ul.image-holder
             [:li.block
              [:a {:href (str "/games/" (game :name) "#all-div")}
               [:img {:src img :class "thumb"}]]]]
            [:div.game-name 
             [:a {:href (str "/games/" (game :name) "#all-div")}
              (game :name)]] 
            [:div.game-date 
             (game :release-date)]
            [:br][:br][:br]
            [:div.game-score
             (game :score)]])]
        
        [:p {:class "clear"}]
        (pagination page last-page)]]
      
      [:div.genId
       [:p.clear]
       [:div.headline (str "There are no games in database currently. Please refresh the page. " (count (get-all-games)))]
       [:div.shadow-divider]])))

(def slide
  [:script
   {:type "text/javascript"}
   "\n// <![CDATA[\nvar flashvars = {};
   flashvars.cssSource = \"/js/piecemaker/piecemaker.css\";
   flashvars.xmlSource = \"/js/piecemaker/piecemaker.xml\";
   flashvars.imageSource = \"/images\";
   var params = {};
   params.play = \"false\";
   \nparams.menu = \"false\";
   params.scale = \"showall\";
   \nparams.wmode = \"transparent\";
   params.allowfullscreen = \"true\";
   \nparams.allowscriptaccess = \"sameDomain\";
   params.allownetworking = \"all\";
   swfobject.embedSWF('/js/piecemaker/piecemaker.swf', 'slideshow-3d', '1000', '520', '10', null, flashvars, params, null);\n// ]]>\n"])

(def object
  [:object
   {:type "application/x-shockwave-flash",
    :data "/js/piecemaker/piecemaker.swf",
    :width "960",
    :height "530",
    :id "slideshow-3d",
    :style "visibility: visible;"}           
   [:param {:name "play", :value "false"}]
   [:param {:name "menu", :value "false"}]
   [:param {:name "scale", :value "showall"}]
   [:param {:name "wmode", :value "transparent"}]
   [:param {:name "allowfullscreen", :value "true"}]             
   [:param {:name "allowscriptaccess", :value "sameDomain"}]             
   [:param {:name "allownetworking", :value "all"}]          
   [:param
    {:name "flashvars",
     :value
     "cssSource=/js/piecemaker/piecemaker.css&xmlSource=/js/piecemaker/piecemaker.xml&imageSource=/images/"}]])

(defn head [title]
  [:head
   [:meta {:charset "utf=8"}]
   (include-css "/css/main.css")
   (include-js "/js/jquery_1.4.2.js")
   (include-js "/js/jqueryui.js")
   (include-js "/js/tooltip/jquery.tools.min.js")
   (include-js "/js/piecemaker/swfobject/swfobject.js")
   (identity slide)
   [:title (str title)]])

(defn header [user]
  [:div#home-header
   [:div.degree
    [:div.wrapper
     [:div.title-holder
      [:div.title-t [:a {:href "/home"} "Games recommendation"]]
      [:div.username (str "Wellcome, " user ". ")
       (link-to "/logout" "Logout")]]
     [:div.link [:a {:href (str "/home" "#genId")} "All games"]]
     [:div.search-err (session/flash-get :wrong-search)]
     (identity search-box)]]])

(def footer
  '([:div#footer
     [:div.degree]]  
     [:div#bottom
      [:div.wrapper "© Copyright 2014. All Rights Reserved"]]))

(defn body-list [user page]
  [:body  
   (retreive-data)   
   (header user)
   [:div#main    
    [:div.wrapper
     [:div.home-content
      (identity object)
      (list-of-games page)]]] 
   (identity footer)])

(defn- layout 
  ([page]  
    (html5   
      (head "List of games")
      (let [user (session/get :username)] 
        (body-list user page)))))

(defn show-list-page [page] 
  (layout page))

(defroutes home-routes
  (GET "/home" [] (show-list-page 1))
  (GET "/home&:page" [page] (show-list-page (Integer/valueOf page)))
  (GET "/logout" [] (logout)))
