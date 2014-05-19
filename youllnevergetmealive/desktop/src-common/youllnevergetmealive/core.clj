(ns youllnevergetmealive.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [incanter.core :refer :all]))


(def speed 10)
(def slow-down-speed 0.8)
(def distance 50)
(declare youllnevergetmealive main-screen return-player)
(def start-time (java.util.Date.))
(def end-time (java.util.Date.))

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


;;(defn move
 ;; [{:keys [player?] :as entity} direction]
 ;; (if player?
 ;; (case direction
 ;;   :down (assoc entity :y (- (:y entity) speed))
 ;;   :up (assoc entity :y (+ (:y entity) speed))
 ;;   :right (assoc entity :x (+ (:x entity) speed))
 ;;   :left (assoc entity :x (- (:x entity) speed)))
 ;; entity))


(defn return-speed-of-player
  [{:keys [x-speed y-speed]}]
  [(cond 
     (key-pressed? :dpad-down) (* -1 speed)
     (key-pressed? :dpad-up) speed
     :else y-speed)
     (cond 
       (key-pressed? :dpad-left) (* -1 speed)
       (key-pressed? :dpad-right) speed
       :else x-speed)])


(defn get-mouse-position
  [screen input-x input-y]
  (input->screen screen input-x input-y))


(defn calculate-gradient-of-a-line
  [x y x1 y1]
  (let [delta-y (- y1 y)
        delta-x (- x1 x)]
  (/ (* 180 (Math/atan2 delta-y delta-x)) Math/PI)))


(defn rotate
  [player mouse-position]
  (assoc player :angle (calculate-gradient-of-a-line
                        (:x player) (:y player) (:x mouse-position) (:y mouse-position))))


(defn get-speed
  [{:keys [x-speed y-speed max-speed]} mouse-position]
    (let [x (float (- (game :x) (/ (game :width) 2)))
          y (float (- (/ (game :height) 2) (game :y)))
          x-adjust (* max-speed (Math/abs (double (/ x y))))
          y-adjust (* max-speed (Math/abs (double (/ y x))))]
      [(* (Math/signum x) (min max-speed x-adjust))
       (* (Math/signum y) (min max-speed y-adjust))]))

(defn slow-down
  [slow-speed]
  (let [speed1 (* slow-speed slow-down-speed)]
    (if (< ( Math/abs speed1) 0.5)
      0
      speed)))

(defn move 
  [{:keys [delta-time]} {:keys [x y health] :as player} mouse-position]
  (let [[x-speed y-speed] (get-speed player mouse-position)
        x-difference (* x-speed (- (.getTime end-time) (.getTime start-time)))
        y-difference (* y-speed (- (.getTime end-time) (.getTime start-time)))] ;(- (.getTime end-time) (.getTime start-time)))]
       (if (or (not= 0 x-difference) (not= 0 y-difference))
         (assoc player 
           :x (+ x x-speed)
           :y (+ y y-speed)
           :x-speed (slow-down x-speed)
           :y-speed (slow-down y-speed)
           :x-difference x-difference
           :y-difference y-difference)
         player)))


(defn move1 [{:keys [x y] :as player} mouse-position]
  (let [x-distance (- (:x mouse-position) x)
        y-distance (- (:y mouse-position) y)
        distance (Math/sqrt (+ (* x-distance x-distance) (* y-distance y-distance)))]
    (if (> distance 1)
      (assoc player 
             :x (+ x (* x-distance slow-down-speed))
             :y (+ y (* y-distance slow-down-speed)))
      player)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (add-timer! screen :create-enemy 1 2)
  ;; (sound "song.mp3" :play)
    (let [background (texture "space1.jpg")
          player (assoc (texture "images.jpg")
         :x (float (/ (game :width) 2)) 
         :y (float (/ (game :height) 2))
         :width 70 
         :height 70 
         :angle 0 
         :player? true
         :health 50
         :x-speed 0
         :y-speed 0
         :max-speed 4)]
    [background player]))

  :on-render
  (fn [screen entities]
    (clear!)
    (let [player (return-player entities)]
      (map (fn [entity] 
                          (move screen entity)) entities))
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond
      (which-direction) (move-player entities)
      :else entities))

  :on-mouse-moved
  (fn [{:keys [:input-x :input-y] :as screen} entities]
    (let [player (return-player entities)
          mouse-position (get-mouse-position screen input-x input-y)]
      (rotate player mouse-position)))
 
  :on-touch-down 
  (fn [{:keys [input-x input-y button] :as screen} entities]
   (if (= button (button-code :right))
     (let [player (return-player entities)
           mouse-position (get-mouse-position screen input-x input-y)]
    (move screen player mouse-position))
     entities)) 

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :create-enemy (conj entities (create-enemy))))

  :on-resize
  (fn [screen entities]
    (height! screen 800)))


(defn return-player
  [entities]
  (some #(if (:player? %) %) entities))

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
