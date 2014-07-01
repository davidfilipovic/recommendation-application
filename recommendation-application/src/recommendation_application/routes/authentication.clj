(ns recommendation-application.routes.authentication
  (:require [compojure.core :refer [defroutes GET POST]]
            [recommendation-application.views.layout :as layout]
            [hiccup.form :refer [form-to label text-field radio-button password-field submit-button email-field]]
            [hiccup.element :refer :all]
            [hiccup.core :refer :all]
            [noir.response :refer [redirect]]))

(defn registration []
  "Register form"
  (layout/common
    (form-to [:post "/register"]
             [:h1 "Please provide following information: "]
             [:table
              [:tr
               [:td (label :name "Name:")]]
              [:tr 
               [:td (text-field :name)]]
              [:tr
               [:td (label :email "E-mail:")]]
              [:tr 
               [:td (email-field :email)]]
              [:tr
               [:td (label :user-name "User name: ")]]
              [:tr 
               [:td (text-field :user-name)]]
              [:tr
               [:td (label :password "Password:")]]
              [:tr 
               [:td (password-field :password)]]
              [:tr
               [:td (label :repeat-password "Repeat password:")]]
              [:tr 
               [:td (password-field :repeat-password)]]]
             [:br]
             (submit-button "Register")
             [:br]
             [:br]
             [:h2 (link-to {:align "left"} "/" "Log in")])))


(defn login []
  "Register form"
   (layout/common 
      [:div.body]
      [:div.grad]
      [:div.header 
       [:div "Recommendation "
        [:span "Application"]]]
     (form-to [:post "/register"]     
         (html [:div.login
               (text-field {:placeholder "username"} :name) 
               (password-field {:placeholder "password"} :password )
               [:br]
               (submit-button "Register")]))))

(defn login1 []
  "Login form"
  (layout/common
    (form-to [:post "/"]
             [:h1 "Log in"]
             [:table 
               [:tr
               [:td (label :user-name "User name: ")]]
              [:tr 
               [:td (text-field "user-name")]]
              [:tr
               [:td (label :password "Password:")]]
              [:tr 
               [:td (password-field "password")]]]
             [:br]
             (submit-button "Log in")
             [:br]
             [:h2 "If you don't have an account already, please " (link-to {:align "left"} "/register" "Register")])))

(defroutes authentication-routes
  (GET "/register" [] (registration))
  (GET "/" [] (login))
  (POST "/register" [user-name password repeat-password] (redirect "/")))







