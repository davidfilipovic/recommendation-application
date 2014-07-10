(ns recommendation-application.get-data
 (:require [hickory.core :as hickory]
           [hickory.select :as s]
           [clojure.string :as string]
           [clojure.data.json :as json]
           [clj-http.client :as client])
 (:use [hickory.core]))


