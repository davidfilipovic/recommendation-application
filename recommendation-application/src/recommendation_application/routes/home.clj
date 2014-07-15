(ns recommendation-application.routes.home
  (:use [recommendation-application.get-data :only [home-page]]
        [recommendation-application.routes.authentication :only [logout]]
        [recommendation-application.models.database :only [get-game-by-name]])
  (:require [compojure.core :refer :all]))
 
        
(defroutes home-routes
  (GET "/home" [] (home-page))
  (GET "/logout" [] (logout))
  (POST "/home" [name] (home-page name)))

 
