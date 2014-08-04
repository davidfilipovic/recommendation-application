(ns recommendation-application.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [recommendation-application.routes.home :refer [home-routes]]
            [recommendation-application.routes.authentication :refer [authentication-routes]]
            [recommendation-application.views.game-page :refer [game-routes]]
            [noir.util.middleware :as noir-middleware]))
 
(defn init []
  (println "Recommendation application is starting"))

(defn destroy []
  (println "Recommendation application is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Sorry, that page does not exists."))

(def app
  (noir-middleware/app-handler 
    [authentication-routes home-routes game-routes app-routes]))
  