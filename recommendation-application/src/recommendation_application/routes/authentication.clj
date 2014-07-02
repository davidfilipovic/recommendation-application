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
    [:div.body]
      [:div.grad]
      [:div.header 
       [:div "Recommendation "
        [:span "Application"]]]
    (form-to [:post "/"]
            (html [:div.login
               (text-field {:placeholder "name"} :name) 
               (text-field {:placeholder "e-mail"} :email) 
               (text-field {:placeholder "user name"} :user-name) 
               (password-field {:placeholder "password"} :password )
               (password-field {:placeholder "repeat password"} :repeat-password )
               [:br]
               (submit-button "Register")
               [:br]
               [:br]
               (link-to {:align "right"} "/" "Login")]))))

(defn login []
  "Register form"
   (layout/common 
      [:div.body]
      [:div.grad]
      [:div.header 
       [:div "Recommendation "
        [:span "Application"]]]
     (form-to [:post "/home"]     
         (html [:div.login
               (text-field {:placeholder "username"} :user-name) 
               (password-field {:placeholder "password"} :password )
               [:br]
               (submit-button "Login")
               [:br]
               [:br]
               (link-to {:align "right"} "/register" "Register")]))))


(defroutes authentication-routes
  (GET "/register" [] (registration))
  (GET "/" [] (login))
  (POST "/register" [user-name password repeat-password] (redirect "/")))







