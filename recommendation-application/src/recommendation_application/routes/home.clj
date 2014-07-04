(ns recommendation-application.routes.home
  (:require [compojure.core :refer :all]
            [recommendation-application.views.layout :as layout]
            [noir.session :as session]
            [hiccup.element :refer :all])
  (:use [recommendation-application.routes.authentication :only [logout]]))

(defn home []
  (layout/common 
    [:h1 (str "Hello " (session/flash-get :username))]
    [:h1 (str "Hello " (session/flash-get :name))]
    [:h1 (link-to "/logout" "Logout")]))

(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" [] (logout)))
