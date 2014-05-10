(ns youllnevergetmealive.core.desktop-launcher
  (:require [youllnevergetmealive.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. youllnevergetmealive "youllnevergetmealive" 1600 800)
  (Keyboard/enableRepeatEvents true))
