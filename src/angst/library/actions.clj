(ns angst.library.actions
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.utils :refer :all]
            [angst.library.effects :as e]))

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
  	  	  (update-in [:empire (:active state) :resources] #(- % (col-cost state planet))))
  	  state))

(defn get-resources [state planet]
  (-> state
    (update-empire-value (:active state) :resources #(+ % (get (-> state :planets planet :production)
  																                             (-> state :planets planet :development))))
   (end-project planet)))

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
  (do-effects state e/effects
              [[:add-resources (:active state) (- (move-cost state distance))]
               [:planet-add from-planet {:ships (- ship-num)}]
               [:planet-add to-planet {:ships ship-num :moved ship-num}]
               [:planet-set to-planet {:ship-colour (-> state :planets from-planet :ship-colour)}]
               [:done-move]
               [:change-buttons [:cancel-move] [:done-command]]]))
(defn conquer
  [state planet colour surviving]
  (-> state
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
              (conquer state to-planet (:colour from-info) surviving)
          (> surviving 0)
             (-> state
                (update-planet-value to-planet :ships surviving)
                (update-planet-value to-planet :ship-colour (:colour from-info))
                (update-planet-value to-planet :moved surviving))
          :else (set-planet-value state to-planet :ships (max 0 (- (inc surviving))))))

(defn attack-move
  [state to-planet from-planet ship-num from-info to-info distance]
  (let [surviving (if (= (:colour to-info) "Black")
              (- ship-num (:ships to-info)) ; attacking uncontrolled planet
              (- ship-num (inc (planet-defense state to-planet))))] ;attacking enemy planet, +1 defense
  (-> state
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
  (-> state (update-in [:empire (:active state) :resources] #(- % 2))
            (update-in [:planets planet :ships] inc)
            (end-project planet)))