(ns recommendation-application.models.database
 (:require [monger.core :as mg]
           [monger.collection :as mc])
   (:import (org.bson.types ObjectId)))

(def connection (mg/connect))

(def db (mg/get-db connection "database"))

(defn insert-admin []
  (mc/insert-and-return db "users" {:name "David" :email "david.1990@ymail.com" :username "admin" :password "admin"}))

(defn init-db []
   connection
   (insert-admin))

(defn check-login [username password]
  (mc/find-one-as-map "users" {:username username :password password}))

(defn create-user [name email username password]
  (mc/insert-and-return "users" {:name name :email email :username username :password password }))

(defn username-exists? [username]
  (nil? (mc/find-maps db "users" {:username username})))

(defn get-password-by-username [username]
  (mc/find-one-as-map db "users" {:username username}))

(defn register [name email username password]
  (if-not (user-exists? username)
    (create-user name email username password)))