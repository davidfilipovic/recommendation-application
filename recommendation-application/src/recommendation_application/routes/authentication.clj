(ns recommendation-application.routes.authentication
  (:require [compojure.core :refer [defroutes GET POST]]
            [recommendation-application.views.layout :as layout]
            [hiccup.form :refer [form-to label text-field radio-button password-field submit-button email-field]]
            [hiccup.element :refer :all]
            [hiccup.core :refer :all]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [recommendation-application.models.database :as db]
            [clojure.string :as str]))

(defn registration []
  "Register form"
  (layout/common
    [:div.body]
    [:div.grad]
    [:div.header 
    [:div "Recommendation "
     [:span "Application"]]]
    (form-to [:post "/"]
              [:div.registration
               [:div.login
               (text-field {:placeholder "name"} :name) 
               (text-field {:placeholder "email"} :email) 
               (text-field {:placeholder "user name"} :user-name) 
               (password-field {:placeholder "password"} :password )
               (password-field {:placeholder "repeat password"} :repeat-password )
               [:br]
               (submit-button "Register")
               [:br]
               [:br]
               (link-to "/" "Login")]])))

(defn login []
  "Login form"
   (layout/common 
     [:div.body]
     [:div.grad]
     [:div.header 
     [:div "Recommendation "
      [:span "Application"]]]
     (form-to [:post "/login"]     
         (html 
           [:div.login
            (text-field {:placeholder "username"} :username) 
            (password-field {:placeholder "password"} :password )
            [:br]
            [:div.error (session/flash-get :error-message)]
            (submit-button "Login")
            [:br]
            [:br]
            (link-to "/register" "Register")]))))

(defn verify-login 
  [username password]
  (cond  
    (nil? (db/username-exists? username)) "Sorry, that username does not exist."
    (not= password (:password (db/get-password-by-username username))) "Password is not valid. Please try again."
    :else true))
    
(defn login-user [username password]
  (let [message (verify-login username password)]
    (if (string? message)
      (do
      (session/flash-put! :error-message message)
      (redirect "/"))
      (do
      (session/flash-put! :user username)
      (redirect "/home")))))

(defn logout []
  (session/remove! :user)
  (redirect "/"))


(defroutes authentication-routes
  (GET "/register" [] (registration))
  (GET "/" [] (login))
  (POST "/register" [name email username password] (redirect "/"))
  (POST "/login" [username password] (login-user username password)))
 






