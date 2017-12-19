(ns angst.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.data :refer :all]
            [angst.library.graphics :refer :all]
            [angst.library.gcomponents :refer :all]
            [angst.library.actions :refer :all]
            [angst.library.utils :refer :all]
            [angst.library.setup :refer :all]
            [angst.library.abilities :refer :all]
            [angst.library.effects :refer :all]
            [angst.library.network :refer :all]
            [angst.library.textinput :refer :all]
            [angst.library.turn :refer :all]
            [angst.library.actionlog :as log])
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
  (cond 
        (= (:phase state) "setup")
          false

        (not-empty (:active-component state)) ; A modal or something is active
          false

  		(empty? vec-seq)
          false

        (over-planet? (-> vec-seq first second :x) (-> vec-seq first second :y))
          (first (first vec-seq))

        :else (get-mouse-planet (rest vec-seq) state)))

(defn get-mouse-button 
  "Consumes a vec-seq of buttons and produces a [:button {button-info}] if one is moused over and otherwise false"
  [buttons]
  	(cond (empty? buttons)
  			false

        (over-button? (:x (second (first buttons)))
        			  (:y (second (first buttons)))
        			  (:width (second (first buttons)))
        			  (:height (second (first buttons))))
        	(first buttons)

        :else (get-mouse-button (rest buttons))))

(defn planet-action
  "Performs a basic action on a planet based on the current phase"
  [state planet]
  (if (-> state :planets planet :used)
        state
      (cond
      	(= (:phase state) 0)
      	  (check-altu (use-ability state planet ability-map))
        (= (:phase state) 3)
          (if (> (:resources (empire state)) 1)
            (build-ship state planet)
            state)
        (= (:phase state) 2)
          (begin-command state planet)
        (= (:phase state) 1)
          (get-resources state planet)
        :else state)))

(defn mouse-pressed [state event]
   (let [planet (get-mouse-planet (seq (:planets state)) state)
   		active-buttons (if (not-empty (:active-component state))
   							 (select-keys button-map (:buttons ((peek (:active-component state)) components)))
   							 (:buttons state))
        button (get-mouse-button active-buttons)] ;button is [:button {button-info}] or false
    (cond 
      planet
        (cond
          (= (:phase state) 4)
            (colonize state planet)

          (:ship-move (:effects state))
            (continue-move state planet)

          (:active-planet state)
            (cond (= (:phase state) 0)
            		(target-effect state planet)
            	  (= (:phase state) 2)
            	  	(begin-move state planet))

          (and (= (:phase state) 0)
          	   (member? planet (-> state :constant-effects :projects))
          	   (planet-active? state planet))
          	(if (and (= (q/mouse-button) :right) (= (-> state :planets planet :project) "active"))
          		(-> state (end-project planet)
          				  (log/add-log-entry :end-project (:active state) planet))
          		(check-altu (use-ability state planet project-effects)))

          (and (= (:active state) (get-planet-empire state planet))
          	(not (and (effect-active? state :Ryss) (= planet (:Ryss (:effect-details state)))))) ; Rys's ability check
          	(let [newstate (planet-action state planet)]
          		(if (not= state newstate)
                	  (set-planet-value newstate planet :used true)
                	  state))
          :else state)

      button
        (do-effects state effects (:effect (second button)))

      :else state)))

(defn mouse-pressed-wrap [state event]
  (cond (= @online-state "host")
            (do-effects (mouse-pressed state event) effects [[:write-server-data]])
        (= @online-state "client")
            (or (send-new-state (mouse-pressed state event) (get-address state)) state)
        :else
          (mouse-pressed state event)))

(defn keypressed [state other]
	(update-text-input state))

(defn update-state [state]
  (do (reset! planet-info-display (get-mouse-planet (seq (:planets state)) state))
      (cond (= @online-state "client")
              (get-host-state state (get-address state))
            (= @online-state "host")
              (if @host-update-required
                    (let [newstate @host-update-required]
                      (do (reset! host-update-required false)
                          newstate))
                    state)
            :else state)))

(defn shutdown [state]
  (do-effects state effects [[:stop-online]]))

(q/defsketch angst
  :title "Angst in Space"
  :size :fullscreen
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw
  :features [:resizable]
  :mouse-pressed mouse-pressed-wrap
  :key-pressed keypressed
  :on-close shutdown
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])

(defn -main [])