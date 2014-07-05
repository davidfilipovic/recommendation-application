(defproject recommendation-application "0.1.0-SNAPSHOT"
  :description "Clojure recommendation application"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.3.0"]
                 [congomongo "0.4.4"]
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]
                 [lib-noir "0.8.4"]
                 [ring-server "0.3.1"]
                 [com.novemberain/monger "2.0.0"]
                 [hickory "0.5.3"]
                 [clj-http "0.9.2"]
                 [org.clojure/data.json "0.2.5"]]
  :main recommendation-application.repl)
