(ns recommendation-application.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [recommendation-application.routes.home :refer [home-routes]]
            [recommendation-application.routes.authentication :refer [authentication-routes]]))

(defn init []
  (println "Recommendation application is starting"))

(defn destroy []
  (println "Recommendation application is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes authentication-routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))

