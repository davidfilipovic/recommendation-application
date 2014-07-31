(ns recommendation-application.models.database
 (:require [monger.core :as mg]
           [monger.collection :as mc])
 (:import (org.bson.types ObjectId)))

(def connection (mg/connect))

(def db (mg/get-db connection "database"))

(defn insert-admin []
  (mc/insert db "users" {:id (inc 1) :name "David" :email "david.1990@ymail.com" :username "admin" :password "admin"}))

(defn empty-db [table]
  (mc/remove db table))

(defn init-db []
   connection
  ; (empty-db)
 ; (insert-admin)
 )

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

(defn get-game-by-name [name]
 (mc/find-one-as-map db "games" {:name name}))

(defn save-game
  [game]
  (mc/insert db "games" game))

(defn get-all [table]
  (mc/find-maps db table))

(defn drop-all-data [] 
  (mc/remove db "games"))

(defn get-all-games []
  (mc/find-maps db "games"))

(defn get-by-score [table]
  (mc/distinct db table "score"))

(defn update-game [game new-critic]
 ( mc/update db "games" game new-critic {:multi false}))