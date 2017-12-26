(ns angst.library.actions
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.utils :refer :all]
            [angst.library.effects :as e]
            [angst.library.actionlog :as log]
            [angst.library.turn :refer :all]))

(defn connected?
  "Checks if two planets are connected"
  [state planet1 planet2]
  (member? planet1 (-> state :planets planet2 :connections)))

(defn colonize [state planet]
  (if (and (= (-> state :planets planet :colour) "Black")
  		   (> (-> state :planets planet :ships) 0)
  		   (= (-> state :planets planet :ship-colour) (:colour (empire state)))
  		   (> (:resources (empire state)) 2))

  	  (-> state
  	  	  (update-planet-value planet :ships dec)
  	  	  (set-planet-value planet :colour (:colour (empire state)))
          (set-planet-value planet :used true)
          (log/add-log-entry :colonize (:active state) planet)
  	  	  (update-in [:empire (:active state) :resources] #(- % (col-cost state planet))))
  	  state))

(defn get-resources [state planet]
    (let [planet-info (-> state :planets planet)
          resources (get (:production planet-info) (:development planet-info))]
      (-> state
        (update-empire-value (:active state) :resources #(+ % resources))
        (log/add-log-entry :produce-resources (:active state) planet resources)
        (end-project planet))))

(defn reset-moved [state]
  "Sets all ships to unmoved"
	(reduce #(set-planet-value %1 %2 :moved 0) state (keys (:planets state))))

(defn begin-command
	"Sets active-planet to a planet key, updates the button, and sets all :used to 0"
	[state planet]
  	(-> state
  		(assoc-in [:active-planet] planet)
  		(change-buttons [:end-phase] [:done-command])
  		(reset-moved)
        (log/add-log-entry :begin-command (:active state) planet)
      (end-project planet)))

(defn begin-move
	"Sets :ship-move to {:planet x :ships 1} and updates the button"
	[state planet]
  (if (and (= (-> state :planets planet :ship-colour) (:colour (empire state)))
           (> (- (-> state :planets planet :ships) (-> state :planets planet :moved)) 0)
  		     (or (connected? state planet (:active-planet state)) (= planet (:active-planet state)) (effect-active? state :Petiska))
           (not (and (effect-active? state :Kazo) (= planet (-> state :effect-details :Kazo)))))

    (-> state (assoc-in [:effects :ship-move] {:planet planet :ships 1}) ;Begins a move from planet
              (change-buttons [:done-command] [:cancel-move]))
    state))

(defn safe-move
  [state to-planet from-planet ship-num distance]
  (-> state
    (update-empire-value (:active state) :resources #(- % (move-cost state distance)))
    (update-planet-value from-planet :ships #(- % ship-num))
    (update-planet-value to-planet :ships #(+ % ship-num))
    (update-planet-value to-planet :moved #(+ % ship-num))
    (set-planet-value to-planet :ship-colour (-> state :planets from-planet :ship-colour))
    (log/add-log-entry :safe-move (:active state) ship-num from-planet to-planet)
    (assoc-in [:effects :ship-move] false)
    (change-buttons [:cancel-move] [:done-command])))

(defn conquer
  [state planet colour surviving]
  (-> state
    (log/add-log-entry :conquer (:active state) planet)
    (add-points (:active state) 1 (= (:major (empire state)) "Warlords"))
    (add-points (:active state) 3 (= (:major (empire state)) "Conquistadores"))
    (add-points (:active state) 2 (= (:major (empire state)) "Slavers"))
    (add-points (get-planet-empire state planet) -2 (= (:major (empire state)) "Slavers"))
    (update-in [:planets planet] #(merge % {:ships surviving :moved surviving :colour colour :ship-colour colour :used true}))
    (end-project planet)
    (update-planet-value planet :development #(max 0 (- % 3)))
    (check-marishka planet)))

(defn resolve-attack
  [state to-planet to-info from-info surviving]
    (cond (and (not= (:colour to-info) "Black") (> surviving 0))
              (conquer state to-planet (:ship-colour from-info) surviving)
          (> surviving 0)
             (-> state
                (set-planet-value to-planet :ships surviving)
                (set-planet-value to-planet :ship-colour (:colour from-info))
                (set-planet-value to-planet :moved surviving)
                (log/add-log-entry :unsuccessful-attack (:active state) to-planet))
          :else (set-planet-value state to-planet :ships (max 0 (- (inc surviving))))))

(defn attack-move
  [state to-planet from-planet ship-num from-info to-info distance]
  (let [surviving (if (= (:colour to-info) "Black")
              (- ship-num (:ships to-info)) ; attacking uncontrolled planet
              (- ship-num (inc (planet-defense state to-planet))))] ;attacking enemy planet, +1 defense

  (-> state
    (log/add-log-entry :attack-move (:active state) ship-num from-planet to-planet (:ships to-info))
    (resolve-attack to-planet to-info from-info surviving)
    (add-points (:active state) (:ships from-info) (= (:major (empire state)) "Warlords"))
    (update-planet-value from-planet :ships #(- % ship-num))
    (update-empire-value (:active state) :resources #(- % (move-cost state distance)))
    (check-dengras (:colour to-info))
    (check-shoran ship-num)
    (assoc-in [:effects :ship-move] false)
    (change-buttons [:cancel-move] [:done-command]))))

(defn continue-move [state to-planet]
  (let [from-planet (:planet (:ship-move (:effects state)))
        ship-num (-> state :effects :ship-move :ships)
        to-info (-> state :planets to-planet)
        from-info (-> state :planets from-planet)
        distance (get-distance (:x from-info)	(:y from-info) (:x to-info) (:y to-info))]

  (if (= to-planet from-planet)

    (if (> (- (:ships to-info) (:moved to-info)) ship-num) ; Prevents moving more ships than exist
      (update-in state [:effects :ship-move :ships] inc) ; Increases number of ships moving
      state)

    (if (and (connected? state to-planet from-planet) (>= (:resources (empire state)) (move-cost state distance)))

      (if (> (:ships to-info) 0)

      		(if (= (:ship-colour to-info) (:ship-colour from-info))
      				(safe-move state to-planet from-planet ship-num distance)
      				(attack-move state to-planet from-planet ship-num from-info to-info distance))

      		(if (or (= (:colour to-info) "Black") (= (:colour to-info) (:ship-colour from-info)))
      			(safe-move state to-planet from-planet ship-num distance)
      			(attack-move state to-planet from-planet ship-num from-info to-info distance)))
      state))))

(defn build-ship [state planet]
    (if (> (:resources (empire state)) 1)
      (-> state 
          (update-in [:empire (:active state) :resources] #(- % 2))
          (update-in [:planets planet :ships] inc)
          (log/add-log-entry :build-ship (:active state) planet)
          (end-project planet))
      state))

(defn start-progress
    "Adds proper amount of progress when a project starts"
    [state planet]
    (if (not (effect-active? state :Chiu))
        (add-progress state planet 1)
        (add-progress state planet (inc (quot (-> state :planets :Chiu :progress) 2)))))

(defn start-project
    "Adds planet to project effects list, sets it to active, and adds initial progress"
    [state planet]
    (-> state
        (update-in [:constant-effects :projects] #(vec (cons planet %)))
        (assoc-in [:planets planet :project] "active")
        (start-progress planet)
        (log/add-log-entry :start-project (:active state) planet (-> state :planets planet :progress))))