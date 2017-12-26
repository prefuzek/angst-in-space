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
      (condp = (:phase state)
        0 (check-altu (use-ability state planet ability-map))
        1 (get-resources state planet)
        2 (begin-command state planet)
        3 (build-ship state planet)
        :else state)))

(defn planet-clicked [state planet]
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
          :else state))


(defn mouse-pressed [state event]
	(let [planet (get-mouse-planet (seq (:planets state)) state)
   		active-buttons (if (not-empty (:active-component state))
   							 (select-keys button-map (:buttons ((peek (:active-component state)) components)))
   							 (:buttons state))
        button (get-mouse-button active-buttons)]

	(letfn [(object-type-wrapper [state]
				(cond 
			      (and planet
			      	   (or (= (:online-state state) :offline) (active-player? state))) ; When online, can only interact on your turn
			      	(planet-clicked state planet)
			      button 
			      	(do-effects state effects (:effect (second button)))
			      :else state))

			(online-wrapper [state]
				(cond (= (:online-state state) :host)
            			(do-effects state effects [[:write-server-data]])
	        		(= (:online-state state) :client)
	            		(do (send-new-state state (get-address state)) state)
	        		:else state))]

			(-> state
				(object-type-wrapper)
				(online-wrapper)))))

(defn keypressed [state other]
	(update-text-input state))

(defn update-players-message [state]
	(let [old-players (:empires state)
		  new-players @connected-players
		  diff (- (count new-players) (count old-players))]
		(cond 
			(= diff 0)
				(assoc-in state [:empires] new-players)
			(> diff 0)
				(-> state
					(assoc-in [:empires] new-players)
					(log/add-log-entry :player-join (first (vec (clojure.set/difference new-players old-players))))
					(do-effects effects [[:write-server-data]]))
			(< diff 0)
				(-> state
					(assoc-in [:empires] new-players)
					(log/add-log-entry :player-leave (first (vec (clojure.set/difference old-players new-players))))
					(do-effects effects [[:write-server-data]])))))

(defn update-buttons [oldstate newstate]
	(cond
		; Remove action button for non-active players
		(not (active-player? newstate))
			(change-buttons newstate [:end-phase] [])

		; Turn begins: add action button for new active player
		(and (not (active-player? oldstate)) (active-player? newstate))
			(change-buttons newstate [] [:end-phase])

		:else newstate))

(defn update-client-state
	[state]
	(try (-> state 
			(update-buttons (get-host-state state (get-address state)))
			(do-effects effects [[:remove-gcomponent :connection-lost]]))
		 (catch Exception e
		 	(do-effects state effects [[:add-gcomponent :connection-lost]]))))

(defn update-state [state]
  (do (reset! planet-info-display (get-mouse-planet (seq (:planets state)) state))
      (cond (= (:online-state state) :client)
              (update-client-state state)
            (= (:online-state state) :host)
              (if-let [newstate @host-update-required]
                      (do (reset! host-update-required false)
                          (update-buttons state (merge state newstate)))
                    (-> state
                    	(update-players-message)))
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
  :mouse-pressed mouse-pressed
  :key-pressed keypressed
  :on-close shutdown
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])

(defn -main [])