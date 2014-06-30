(ns recommendation-application.core)


(defn start-server [] 
  (run-jetty #'app {:port 8080 :join? false})
  (println "\nWelcome to the recommendation application. Please browse to http://localhost:8080 to get started!"))


(defn -main [& args]
  (insert-inital-users) 
  (process-data)
  (start-server))