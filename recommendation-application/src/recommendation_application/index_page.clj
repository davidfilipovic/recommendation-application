(ns recommendation-application.index-page
  (:require [noir.session :as session])
  (:use [hiccup.core :only [html]]
        [hiccup.page :only [include-css]]))

(defn template-page 
  "Display header, main and user menu, body content and footer for all pages."
  [title uri content]
  (html 
    [:head
     [:meta {:charset "UTF-8"}] 
     [:title title]
     (include-css "/css/style.css")]
    (let [user (session/get :user)]
      [:body
       [:div#header
        [:div
         [:a.logo]
         (menu uri user)]]
       [:div#body
        [:div.user-info
         (user-menu uri user)]
        content]
       [:div#footer
        [:p "Copyright &copy; 2013. All Rights Reserved"]]])))

  
(defn home-page [uri]
  (template-page 
    "Home page"
    uri
    [:div.body
       [:h1 {:style "border-bottom: 1px solid #ebebeb; padding: 10px;"} 
        "Web application for video games recommendations"]]))