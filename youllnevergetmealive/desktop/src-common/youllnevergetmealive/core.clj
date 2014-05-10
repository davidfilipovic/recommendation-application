(ns youllnevergetmealive.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def speed 10)
(def distance 50)

(defn create-enemy
  []
  (let [x (+ (rand-int 700) distance)
        y (+ (rand-int 30) distance)]
    (assoc (texture "images.jpg") :x x :y y :width 10 :height 10)))

(defn move
  [entity direction]
  (case direction
    :down (assoc entity :y (- (:y entity) speed))
    :up (assoc entity :y (+ (:y entity) speed))
    :right (assoc entity :x (+ (:x entity) speed))
    :left (assoc entity :x (- (:x entity) speed))
    nil))


(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (add-timer! screen :create-enemy 1 2)
    (assoc (texture "images.jpg")
           :x 50 :y 50 :width 100 :height 100
           :angle 45 :origin-x 0  :origin-y 0))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

   :on-key-down
  (fn [screen entities]
    (cond
      (= (:keycode screen) (key-code :dpad-down))
      (move (first entities) :down)
      (= (:keycode screen) (key-code :dpad-up))
      (move (first entities) :up)
      (= (:keycode screen) (key-code :dpad-right))
      (move (first entities) :right)
      (= (:keycode screen) (key-code :dpad-left))
      (move (first entities) :left)
      :else entities))

  :on-touch-down
  (fn [screen entities]
    (cond
      (> (:input-y screen) (* (game :height) (/ 2 3)))
      (move (first entities) :down)
      (< (:input-y screen) (/ (game :height) 3))
      (move (first entities) :up)
      (> (:input-x screen) (* (game :width) (/ 2 3)))
      (move (first entities) :right)
      (< (:input-x screen) (/ (game :width) 3))
      (move (first entities) :left)))
  
  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :create-enemy (conj entities (create-enemy)))))

(defgame youllnevergetmealive
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(-> main-screen :entities deref)
