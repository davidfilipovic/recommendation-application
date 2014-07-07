(ns recommendation-application.routes.home
  (:use [recommendation-application.routes.authentication :only [logout]]
        [hickory.core])
  (:require [compojure.core :refer :all]
            [recommendation-application.views.layout :as layout]
            [recommendation-application.get_data :only [t]]
            [noir.session :as session]
            [hiccup.element :refer :all]
            [hickory.select :as s]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-http.client :as client]))

 (defn r [l] (let [content (s/select (s/child (s/class "bookTitle"))
                                  (as-hickory (parse (slurp l))))]
            (map #(str "http://www.goodreads.com" %)
                 (map :href
                     (map :attrs content)))))
 
 (defn get-json 
  [link]
     (slurp (str "http://www.w3.org/2012/pyMicrodata/extract?format=json&uri=" link)))

 (defn prepare-json 
  "Prepare json for further data extraction."
  [body]
  (json/read-str
    (string/replace
      (string/replace body "@" "") "http://schema.org/" "")
    :key-fn keyword))

(defn get-book-data 
  "Get section from json that contains book data."
  [json-text] 
  (second (:list (:md:item json-text))))

(defn home []
  (layout/common 
    [:h1 (str "Hello " (session/flash-get :username))]
    [:h1 (str "Hellssso " (session/flash-get :name))]
    [:h1 (link-to "/logout" "Logout")]
   ; [:h1 (r "http://www.goodreads.com/shelf/show/programming?page=1")]
    [:h1  (get-json "http://www.goodreads.com/book/show/4099.The_Pragmatic_Programmer")]))

(defroutes home-routes
  (GET "/home" [] (home))
  (GET "/logout" [] (logout)))
