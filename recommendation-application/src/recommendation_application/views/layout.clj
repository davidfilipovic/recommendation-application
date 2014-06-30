(ns recommendation-application.views.layout
  (:require [hiccup.page :refer [html5 include-css]]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to recommendation-application"]
     (include-css "/css/style.css")]
    [:body body]))
