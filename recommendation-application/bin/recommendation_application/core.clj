(ns recommendation-application.core
  (:use [compojure.core :only [defroutes GET POST DELETE]]
        [ring.adapter.jetty :only [run-jetty]]))


(defroutes handler
  (GET "/" [] (index-page "/"))
  (route/not-found "Page does not exists. Sorry about that."))

(defn start-server [] 
  (run-jetty #'app {:port 8080 :join? false})
  (println "\nWelcome to the recommendation application. Please browse to http://localhost:8080 to get started!"))


(defn -main [& args]
  ;(insert-inital-users) 
  ;(process-data)
  (start-server))