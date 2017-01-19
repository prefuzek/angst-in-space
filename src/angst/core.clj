(ns angst.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.planets :refer :all]
            [angst.library.graphics :refer :all]
            [angst.library.actions :refer :all])
  (:gen-class))

(def planet-rad 20)

(defn setup []
  ; Set frame rate to 10 frames per second.
  (q/frame-rate 10)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; Set line color to grey
  (q/stroke 100)
  ;Set text preferences
  (q/text-align :center)
  (q/text-size 12)
  ; Initialize state
  {:planets planet-map
   :display {:planet false
             :infobar-width 300}
   :empire {:Sheep {:name "Sheep" :colour "Blue" :resources 8}
            :Gopher {:name "Gopher" :colour "Green" :resources 8}
            :Muskox {:name "Muskox" :colour "Red" :resources 8}
            :Llama {:name "Llama" :colour "Yellow" :resources 8}}
   :active :Sheep
   :phase 0 ;0: Specialization, 1 Production, 2 Command, 3 Construction 
   ; Buttons coordinates given for center of button
   :buttons [{:label "End Specialization Phase"
              :x (- (q/width) 150)
              :y (- (/ (q/height) 2) 40) 
              :width 200
              :height 50}
             {:label "Save"
              :x 950
              :y 40
              :width 80
              :height 40}
             {:label "Load"
              :x 850
              :y 40
              :width 80
              :height 40}]
   :commanding false ; Either false or planet keyword
   :ship-move false ; Either false or {:planet :numships}
   :next-player-map {:Sheep :Gopher :Gopher :Muskox :Muskox :Llama :Llama :Sheep}
   })

(defn empire [state]
  ((:active state) (:empire state)))

(defn over-planet? [x y]
  (< (q/sqrt (+ (q/sq (- (q/mouse-x) x))
                (q/sq (- (q/mouse-y) y))))
      planet-rad))

(defn over-button? [x y width height]
  (and (< (- x (/ width 2)) (q/mouse-x) (+ x (/ width 2)))
       (< (- y (/ height 2)) (q/mouse-y) (+ y (/ height 2)))))

(defn get-mouse-planet
  "Consumes (:planets state) seq and produces planet info-map if one is moused over and false if not"
  [vec-seq]
  (cond (empty? vec-seq) false
        (over-planet? (-> vec-seq first second :x) (-> vec-seq first second :y))
        (first (first vec-seq))
        :else (get-mouse-planet (rest vec-seq))))

(defn get-mouse-button 
  "Produces a button if one is moused over and otherwise false"
  [buttons]
  (cond (empty? buttons) false
        (over-button? (:x (first buttons)) (:y (first buttons)) (:width (first buttons)) (:height (first buttons)))
        (first buttons)
        :else (get-mouse-button (rest buttons))))

(defn planet-action [state planet]
  (if (-> state :planets planet :used)
      state
      (cond
        (= (:phase state) 3)
          (if (> (:resources (empire state)) 1)
            (build-ship state planet)
            state)
        (= (:phase state) 2)
          (begin-command state planet)
        (= (:phase state) 1)
          (get-resources state planet)
        :else state)))

(defn end-phase [state]
  (update-in state [:phase] #(mod (inc %) 4)))

(defn get-next-player [state]
  (let [next-player-map {:Sheep :Gopher :Gopher :Muskox :Muskox :Llama :Llama :Sheep}]
    (assoc-in state [:active] ((:active state) next-player-map))))

(defn reset-all-used [state]
  (reduce (fn [x y] (set-planet-value x y :used false))
                     (get-next-player state)
                     (map first (vec (:planets state)))))

(defn end-turn [state]
  (-> state
      reset-all-used
      end-phase
      (update-empire-value (:active state) :resources
        #(- % (get upkeep (get-num-planets state (:active state)))))))

(defn press-button [state button]
  (cond (= (:label button) "Done Command")
          (-> state
              (assoc-in [:buttons 0 :label] "End Command Phase")
              (assoc-in [:commanding] false))
        (= (:label button) "Cancel Move")
          (-> state
            (assoc-in [:buttons 0 :label] "Done Command")
            (assoc-in [:ship-move] false))
        (= (:label button) "Save")
         (do
          (spit "save.txt" state)
          state)
        (= (:label button) "Load")
          (load-file "save.txt")
        :else
    (cond (= (:phase state) 0)
        (end-phase (assoc-in state [:buttons 0 :label] "End Production Phase"))
      (= (:phase state) 1)
        (end-phase (assoc-in state [:buttons 0 :label] "End Command Phase"))
      (= (:phase state) 2)
        (end-phase (assoc-in state [:buttons 0 :label] "End Construction Phase"))
      (= (:phase state) 3)
        (let [potential-colonies (map first (filter #(and (> (:ships (second %)) 0)
                                                           (= (:ship-colour (second %)) (:colour ((:active state) (:empire state))))
                                                           (= (:colour (second %)) "Black"))
                                                    (:planets state)))]
          (if (empty? potential-colonies)
            (end-turn (assoc-in state [:buttons 0 :label] "End Specialization Phase"))
            (assoc-in
              (assoc-in state [:phase] "Colonization")
              [:buttons 0 :label] "Done Colonizing")))
      (= (:phase state) "Colonization")
        (end-turn (assoc-in
          (assoc-in state [:buttons 0 :label] "End Specialization Phase")
          [:phase] 3)) ;kinda kludgy
      :else state)))

(comment (defn mouse-pressed [state event]
  (let [planet (get-mouse-planet (seq (:planets state)))
        button (get-mouse-button (:buttons state))]
    (cond (and planet (or (= (:colour (empire state)) (-> state :planets planet :colour))
                          (= (:phase state) "Colonization")
                          (:commanding state)))
           (let [newstate (planet-action state planet)]
            (if (= newstate state)
              state
              (planet-action state planet)))
          button
           (press-button state button)
          :else state))))

(defn mouse-pressed [state event]
   (let [planet (get-mouse-planet (seq (:planets state)))
        button (get-mouse-button (:buttons state))]
    (cond planet
            (cond
              (= (:phase state) "Colonization")
                (colonize state planet)
              (:ship-move state)
                (continue-move state planet)
              (:commanding state)
                (begin-move state planet)
              (= (:colour (empire state)) (-> state :planets planet :colour))
                (-> state
                    (planet-action planet)
                    (set-planet-value planet :used true))
              :else state)
          button
            (press-button state button)
          :else state)))

(defn update-state [state]
  (assoc-in state [:display :planet] (get-mouse-planet (seq (:planets state)))))
 
(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  ; Draw the infobar
  (set-fill "White") (draw-infobar state)
  ; Draw all buttons
  (draw-buttons state)
  (set-fill "White") (text-buttons state)
  ; Draw the planet connections
  (set-fill "Green") (draw-connections state)
  ; Draw the planets
  (doseq [p (seq (:planets state))]
    (set-fill (:colour (second p)))
    (q/ellipse (:x (second p)) (:y (second p)) planet-rad planet-rad))
  ; Name the planets
  (q/text-align :center)
  (doseq [p (seq (:planets state))]
    (set-fill "White")
    (q/text (:name (second p)) (:x (second p)) (- (:y (second p)) 15)))
  ; Draw the ships
  (doseq [p (seq (:planets state))]
    (set-fill (:ship-colour (second p)))
    (draw-ships (:x (second p)) (:y (second p)) (range (:ships (second p)))))
  ; Diagnostics:
  )

(q/defsketch angst
  :title "Angst in Space"
  :size :fullscreen
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features []
  :mouse-pressed mouse-pressed
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])

(defn -main
  []
  (use 'angst.core :reload-all true))