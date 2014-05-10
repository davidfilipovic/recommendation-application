(ns youllnevergetmealive.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))


(def speed 10)
(def distance 50)
(declare youllnevergetmealive main-screen)


(defn create-enemy
  []
  (let [x (+ (rand-int 700) distance)
        y (+ (rand-int 30) distance)]
    (assoc (texture "images.jpg") :x x :y y :width 10 :height 10)))


(defn which-direction
  []
  (cond
   (key-pressed? :dpad-down) :down
   (key-pressed? :dpad-up) :up
   (key-pressed? :dpad-left) :left
   (key-pressed? :dpad-right) :right))


(defn change-player-position
  [{:keys [player?] :as entity}]
  (if player? 
    (let [direction (which-direction)
          new-x (case direction
                  :left (- (:x entity) speed)
                  :right (+ (:x entity) speed)
                   (:x entity))
          new-y (case direction
                  :up (+ (:y entity) speed)
                  :down (- (:y entity) speed)
                  (:y entity))]
      (assoc entity :x new-x :y new-y))
    entity))


(defn move-player [entities]
  (map change-player-position entities))


(defn move
  [{:keys [player?] :as entity} direction]
  (if player?
  (case direction
    :down (assoc entity :y (- (:y entity) speed))
    :up (assoc entity :y (+ (:y entity) speed))
    :right (assoc entity :x (+ (:x entity) speed))
    :left (assoc entity :x (- (:x entity) speed)))
  entity))


(defn rotate [entity]
  (assoc entity :angle calculate-gradient-of-a-line))


(defscreen main-screen

  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (add-timer! screen :create-enemy 1 2)
  ;; (sound "song.mp3" :play)
    (let [background (texture "space1.jpg")
          player (assoc (texture "images.jpg")
           :x 50 :y 50 :width 100 :height 100 :angle 90 :player? true)]
    [background player]))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

   :on-key-down
  (fn [screen entities]
    (cond
      (which-direction) (move-player entities)
      :else entities))
  
  :on-mouse-moved
  (fn [screen entities]
    (rotate (second entities)))

 ;; :on-touch-down
  ;;(fn [screen entities]
    ;;(cond
      ;;(> (:input-y screen) (* (game :height) (/ 2 3)))
      ;;(move (first entities) :down)
     ;; (< (:input-y screen) (/ (game :height) 3))
     ;; (move (first entities) :up)
     ;; (> (:input-x screen) (* (game :width) (/ 2 3)))
     ;; (move (first entities) :right)
     ;; (< (:input-x screen) (/ (game :width) 3))
     ;; (move (first entities) :left)))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :create-enemy (conj entities (create-enemy))))

  :on-resize
  (fn [screen entities]
    (height! screen 800)))


(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))


(defgame youllnevergetmealive
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! youllnevergetmealive blank-screen)))))

(-> main-screen :entities deref)

;;(app! :post-runnable #(set-screen! youllnevergetmealive main-screen))
