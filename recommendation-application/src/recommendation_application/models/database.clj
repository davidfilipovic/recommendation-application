(ns recommendation-application.models.database
  :require [clojure.java.jdbc :as sql])

(def db
  {:classname "com.mysql.jdbc.Driver"
   :subprocotol "mysql"
   :subname "//localhost:3306/baza"})