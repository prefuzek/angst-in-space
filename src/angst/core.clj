(ns angst.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.data :refer :all]
            [angst.library.graphics :refer :all]
            [angst.library.actions :refer :all]
            [angst.library.victory :refer :all]
            [angst.library.utils :refer :all]
            [angst.library.setup :refer :all])
  (:gen-class))

(defn over-planet? [x y]
  (< (q/sqrt (+ (q/sq (- (q/mouse-x) (scalex x)))
                (q/sq (- (q/mouse-y) (scaley y)))))
      20))

(defn over-button? [x y width height]
  (and (< (scalex (- x (/ width 2))) (q/mouse-x) (scalex (+ x (/ width 2))))
       (< (scaley (- y (/ height 2))) (q/mouse-y) (scaley (+ y (/ height 2))))))

(defn get-mouse-planet
  "Consumes (:planets state) seq and produces planet info-map if one is moused over and false if not, or if the game is in setup phase"
  [vec-seq state]
  (cond (= (:phase state) "setup") false
  		(empty? vec-seq) false
        (over-planet? (-> vec-seq first second :x) (-> vec-seq first second :y))
        (first (first vec-seq))
        :else (get-mouse-planet (rest vec-seq) state)))

(defn get-mouse-button 
  "Produces a button if one is moused over and otherwise false"
  [buttons]
  (cond (empty? buttons) false
        (over-button? (:x (first buttons)) (:y (first buttons)) (:width (first buttons)) (:height (first buttons)))
        (first buttons)
        :else (get-mouse-button (rest buttons))))

(defn planet-action
  "Performs an action on a planet based on the current phase"
  [state planet]
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
	"Cycles between the four main phases"
  (update-in state [:phase] #(mod (inc %) 4)))

(defn get-next-player
  "Changes the active player"
  [state]
   (assoc-in state [:active] ((:active state) (:next-player-map state))))

(defn reset-all-used
	"Sets all planets to :used false"
	[state]
  (reduce (fn [x y] (set-planet-value x y :used false))
                     state
                     (map first (vec (:planets state)))))

(defn develop-all
	"Increases all the active player's planets development by one"
  [state]
  (reduce (fn [x y] (update-planet-value x y :development #(min 7 (inc %))))
                    state
                    (map first (filter #(= (:colour (second %)) (:colour (empire state))) (vec (:planets state))))))

(defn end-turn
	"Performs end-of-turn updates"
	[state]
  (-> state
      reset-all-used
      develop-all
      end-phase
      (update-empire-value (:active state) :resources
        #(- % (get upkeep (get-num-planets state (:active state)))))
      (add-points (:active state) 1 (= (:major (empire state)) "Immortals"))
      get-next-player
      imperial-points))

(defn press-game-button
  "Performs the actions associated with various buttons"
  [state button]
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
        (= (:label button) "Quit")
          setup-state
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
          [:phase] 3)) ;kinda kludgy but works
      :else state)))

(defn press-setup-button
	"Handles button presses in the setup phase"
	[state button]
	(cond (and (= (:label button) "Start new game!") (> (count (:empires state)) 1))
			(new-game state)
		  (= (:label button) "Load from save")
		  	(load-file "save.txt")
		  (clojure.string/includes? (:label button) ": No")
		    (-> state
		    	(update-in [:empires] #(conj % (:empire button)))
		    	(update-in [:buttons (:index button) :label] #(clojure.string/replace % #": No" ": Yes")))
		  (clojure.string/includes? (:label button) ": Yes")
		  	(-> state
		    	(update-in [:empires] #(disj % (:empire button)))
		    	(update-in [:buttons (:index button) :label] #(clojure.string/replace % #": Yes" ": No")))
	      (clojure.string/includes? (:label button) ": Off")
		  	(-> state
		    	(update-in [:options] #(conj % (:option button)))
		    	(update-in [:buttons (:index button) :label] #(clojure.string/replace % #": Off" ": On")))
		  (clojure.string/includes? (:label button) ": On")
		  	(-> state
		    	(update-in [:empires] #(disj % (:option button)))
		    	(update-in [:buttons (:index button) :label] #(clojure.string/replace % #": On" ": Off")))
		  :else state))

(defn mouse-pressed [state event]
   (let [planet (get-mouse-planet (seq (:planets state)) state)
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
          	(if (= (:phase state) "setup")
          		  (press-setup-button state button)
            	(press-game-button state button))
          :else state)))

(defn update-state [state]
  (assoc-in state [:display :planet] (get-mouse-planet (seq (:planets state)) state)))

(q/defsketch angst
  :title "Angst in Space"
  :size :fullscreen
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:resizable]
  :mouse-pressed mouse-pressed
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])

(defn -main
  []
  (use 'angst.core :reload-all true))