(ns try)

(defn registration []
  "Register form"
  (layout/common
    (form-to [:post "/register"]
             [:h1 "Please provide following information: "]
             [:table
              [:tr
               [:td (label :name "Name:")]]
              [:tr 
               [:td (text-field {:placeholder "name"} :name)]]
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