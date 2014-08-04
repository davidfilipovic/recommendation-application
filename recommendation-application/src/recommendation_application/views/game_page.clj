(ns recommendation-application.views.game-page
  (:use [recommendation-application.models.database :only [get-game-by-name update-game save-game get-all-games drop-all-data]]
        [recommendation-application.recommendations :only [recommend-games-for-game]]
        [clj-time.core :only [now]]
        [hiccup.form :only [form-to label text-area submit-button text-area text-field]]
        [recommendation-application.routes.home :only [head header footer verify-search]])
  (:require [clj-time.format :as format-time]
            [clojure.string :as string]
            [hiccup.page :refer [html5]]
            [noir.session :as session]
            [noir.response :refer [redirect]]
            [compojure.core :refer [defroutes GET POST]]))

(defn retreive-game [name]
  (let [game (get-game-by-name name)]
    game))

(def date-form 
  (format-time/formatter "MMM dd, yyyy"))

(defn right-col 
  [right-content]
  [:div.front-right-col
   [:div.front-right-col-title
   [:div.bullet-title
    [:div.big "Critics"]
    [:div.small "List of acclaimed games critics"]]]  
  [:div.front-right-col-games
   right-content]])

(defn show-recommendations [game-name]
  "Get recommendations and show on the page."
  (let [games (take 15 (recommend-games-for-game game-name))]
    (if (not= 0 (count games))
      [:div.front-left-down-col 
       (for [name games]
         (let [game (get-game-by-name (first name))
               src (game :picture)
               g-name (game :name)]
           [:a {:href (str "/games/" g-name "#all-div")}
            [:img {:src src :class "thumb-d"}]]))])))

(defn left-col 
  [left-content add-rev]
  [:div.front-left-col
   [:div.bullet-title
    [:div.big "Game"]
    [:div.small "Description"]]
   left-content
   [:div.headline-h "People who liked this game, also liked: "]
   (show-recommendations (session/get :game))
   [:div.headline-h "Add review"]
   add-rev])

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
                                                     :date (format-time/unparse date-form
                                                                           (new org.joda.time.DateTime (.toDate (now))
                                                                                (org.joda.time.DateTimeZone/forID "UTC")))
                                                     :score (Integer/valueOf rating)))}
                              {:score  (read-string
                                         (string/replace
                                           (format "%.2f" (with-precision 1
                                                           (/ (+ 
                                                                (* num-of-critics                                     
                                                                   (game :score)) 
                                                                rating) 
                                                              (inc num-of-critics)))) "," "."))})]  
        (do
          (update-game game new-critic)
          (session/remove! :game)
          (session/remove! :rating)
          (session/remove! :error-message-rev)
          (session/flash-put! :sort 1)
          (redirect (str "/games/" (game :name) "#all-div")))))))

(defn get-reviews
  [game-name]
  (let [game (retreive-game game-name)]
    (if (= 1 (session/flash-get :sort))
      (let [reviews (reverse (game :critics))]
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
       [:br]]))
      (let [reviews (sort-by :score > (game :critics))]                                
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
           [:br]])))))

(defn get-game-information
  [game-name]
  (let [game (retreive-game game-name)
        img (game :picture)]
    [:table
     [:ul.blocks-holder
      [:li.block
       [:img {:src img :class "thumb"}]]
      [:li.block-data 
       [:div.critic-game-score
        [:div.main (game :score)]]]   
      [:li.block-data-h
       (str "Based on " (count (game :critics)) " critics reviews.")]]
     [:tr
      [:th "About: "]
      [:td (game :about)]]
     [:div.pub-name
      [:div.game-dev
       [:label "Publisher: "] (game :publisher)]
      [:div.game-genre
       [:label "Genre: "] (game :genre)]
      [:div.game-rating
       [:label "Rating: "] (game :rating)]
      [:div.game-rel-date
       [:label "Release date: "] (game :release-date)]]]))

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
              (submit-button {:name "submit", :id "submit"} "Add")]]]))

(def gallery 
  [:div {:id "slideshow"}
   [:img {:src "/images/collage1.jpg" :width "990px"}]])

(defn body-game [user left-content right-content add-rev]
  [:body     
   (header user)
   [:div#main    
    [:div.wrapper
     [:div.home-content   
     (identity gallery)
     [:div#all-div
       [:p.clear]
       [:div.headline (session/get :game)] 
       [:div.shadow-divider]
      [:div.fmr-div
       (left-col left-content add-rev)        
       [:div.front-middle-coll]
       (right-col right-content)]]]]] 
   (identity footer)])

(defn- layout 
  ([left-content right-content add-rev] 
    (html5
      (head (session/get :game))
      (let [user (session/get :username)] 
        (body-game user left-content right-content add-rev)))))

(defn show-game [game]
  (layout 
    (get-game-information game)
    (get-reviews game)
    (show-add-review-box game)))  

(defroutes game-routes
  (GET "/games/:game" [game] 
       (do (session/put! :game game)
         (show-game game)))
  (GET "/new-rating/:game&:rvalue" [game rvalue]
       (session/put! :rating (Integer/valueOf rvalue))
       (redirect (str "/games/" game "#reviewForm")))
  (POST "/addnewreview" [title review]  
        (add-review-to-site title (session/get :rating) review))
  (POST "/games" [game] (verify-search game)))
