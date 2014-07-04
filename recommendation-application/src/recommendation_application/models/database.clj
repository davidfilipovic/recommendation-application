(ns recommendation-application.models.database
 (:require [monger.core :as mg]
           [monger.collection :as mc])
 (:import (org.bson.types ObjectId)))

(def connection (mg/connect))

(def db (mg/get-db connection "database"))

(defn insert-admin []
  (mc/insert db "users" {:_id (ObjectId.) :name "David" :email "david.1990@ymail.com" :username "admin" :password "admin"}))

(defn init-db []
   connection
   (insert-admin))

(defn create-new-user [name email username password]
  (mc/insert db "users" {:_id (ObjectId.) :name name :email email :username username :password password }))

(defn username-exists? [username]
   (let [number-of-users (mc/find-maps db "users" {:username username})]
    (> (count number-of-users) 0)))

(defn email-exists? [email]
   (let [emails (mc/find-one-as-map db "users" {:email email})]
    (> (count emails) 0)))

(defn get-user-by-username [username]
  (mc/find-one-as-map db "users" {:username username}))

(defn get-users []
  (mc/find-maps db "users"))