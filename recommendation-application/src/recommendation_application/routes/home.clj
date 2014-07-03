(ns recommendation-application.routes.home
  (:require [compojure.core :refer :all]
            [recommendation-application.views.layout :as layout]
            [noir.session :as session]
            [recommendation-application.routes.authentication :only [logout]]
            [hiccup.element :only [link-to]]))

(defn home []
  (layout/common 
    [:h1 (str "Hello " (session/flash-get :user))]
    [:h1 (link-to "/logout" "Logout")]))

(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" (logout)))
