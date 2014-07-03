(ns recommendation-application.routes.home
  (:require [compojure.core :refer :all]
            [recommendation-application.views.layout :as layout]
            [noir.session :as session]))

(defn home []
  (layout/common 
    [:h1 "Hello" ]
    (session/flash-get :user)))

(defroutes home-routes
  (GET "/home" [] (home)))
