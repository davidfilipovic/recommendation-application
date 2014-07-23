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
    [:div.body-l]
    [:div.grad]
    [:div.header 
    [:div "Recommendation "
     [:span "Application"]]]
    (form-to [:post "/register"]
              [:div.registration
               [:div.login
               (text-field {:placeholder "name"} :name) 
               (text-field {:placeholder "email"} :email) 
               (text-field {:placeholder "username"} :username) 
               (password-field {:placeholder "password"} :password )
               (password-field {:placeholder "repeat password"} :repeat-password )
               [:br]
               [:div.error (session/flash-get :error-message)]
               (submit-button "Register")
               [:br]
               [:br]
               (link-to "/" "Login")]])))

(defn login []
  "Login form"
   (layout/common 
     [:div.body-l]
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
    (= false (db/username-exists? username)) "Sorry, that username does not exist."
    (not= password (:password (db/get-user-by-username username))) "Password is not valid. Please try again."
    :else true))
    
(defn login-user [username password]
  (let [message (verify-login username password)]
    (if (string? message)
      (do
      (session/flash-put! :error-message message)
      (redirect "/"))
      (do
      (session/flash-put! :name (:name (db/get-user-by-username username)))
      (session/flash-put! :username username)
      (redirect "/home")))))

(defn verify-registration
  [name email username password repeat-password]
  (cond 
   (> 3 (.length name)) "Name must be minimum 3 characters long."
   (< 25 (.length name)) "Name must be maximum 25 characters long."
   (not= name (first (re-seq #"[A-Za-z0-9_]+" name))) "Name must be alphanumeric."
   (not= email (first (re-matches #"^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$" email))) 
   "E-mail hasn't been entered in a valid form."
   (= true (db/email-exists? email)) "E-mail address already taken."
   (= true (db/username-exists? username)) "Username already taken."
   (> 4 (.length username)) "Username must be minimum 4 characters long."
   (< 16 (.length username)) "Username must be maximum 16 characters long."
   (not= username (first (re-seq #"[A-Za-z0-9_]+" username))) "Username should be alphanumeric."
   (> 3 (.length password)) "Password field must be minimum 3 characters long."
   (not= password repeat-password) "Password and repeated password don't match."
   :else true))

(defn register-user 
   [name email username password repeat-password]
   (let [message (verify-registration name email username password repeat-password)]
     (if (string? message)
       (do
         (session/flash-put! :error-message message)
         (redirect "/register"))
       (do
         (db/create-new-user name email username password)
         (redirect "/")))))

(defn logout []
  (session/remove! :user)
  (redirect "/"))

(defroutes authentication-routes
  (GET "/register" [] (registration))
  (GET "/" [] (login))
  (POST "/register" [name email username password repeat-password] 
        (register-user name email username password repeat-password))
  (POST "/login" [username password] (login-user username password)))
 






