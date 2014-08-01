(ns recommendation-application.repl
  (:use recommendation-application.handler
        ring.server.standalone
        [ring.middleware file-info file]
        [ring.middleware.reload :only [wrap-reload]]
        [ring.middleware.stacktrace :only [wrap-stacktrace]]
        [ring.middleware.params :only [wrap-params]]
        [recommendation-application.get-data  :only [ajmo]]
        [recommendation-application.models.database :only [init-db empty-db]])
  (:require [clojure.data.json :as json]
            [noir.session :as session]))

(defonce server (atom nil))

(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
    ; Makes static assets in $PROJECT_DIR/resources/public/ available.
    (wrap-stacktrace)
    (wrap-file "resources")
    (wrap-reload)
    (wrap-params)
    ; Content-Type, Content-Length, and Last Modified headers for files in body
    (wrap-file-info)))

(defn start-server
  "Used for starting the server in development mode from REPL"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 9009)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join false}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(defn -main [& args]
  (init-db)
  ;(ajmo)
  (start-server))
