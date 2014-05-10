(ns youllnevergetmealive.core.desktop-launcher
  (:require [youllnevergetmealive.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. youllnevergetmealive "youllnevergetmealive" 800 600)
  (Keyboard/enableRepeatEvents true))
