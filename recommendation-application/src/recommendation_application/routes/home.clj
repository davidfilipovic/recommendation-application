(ns recommendation-application.routes.home
  (:require [compojure.core :refer :all]
            [recommendation-application.views.layout :as layout]
            [recommendation-application.get_data :only [t]]
            [noir.session :as session]
            [hiccup.element :refer :all]
            [hickory.select :as s]
           [clojure.string :as string]
           [clojure.data.json :as json]
           [clj-http.client :as client])
  (:use [recommendation-application.routes.authentication :only [logout]]
        [hickory.core]))


(defn home []
  (layout/common 
    [:h1 (str "Hello " (session/flash-get :username))]
    [:h1 (str "Hellssso " (session/flash-get :name))]
    [:h1 (link-to "/logout" "Logout")]
    [:h1]))

(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" [] (logout)))
