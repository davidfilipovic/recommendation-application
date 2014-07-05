(ns recommendation-application.get-data
 (:require [hickory.core :as hickory]
           [hickory.select :as s]
           [clojure.string :as string]
           [clojure.data.json :as json]
           [clj-http.client :as client])
 (:use [hickory.core]))


(def site-htree (-> (client/get "http://formula1.com/default.html") :body parse as-hickory))


(def t (-> (s/select (s/child (s/class "subCalender") ; sic
                                    (s/tag :div) 
                              (s/id :raceDates) 
                              s/first-child
                              (s/tag :b)) 
                     site-htree) 
           first  :content
          first string/trim))


