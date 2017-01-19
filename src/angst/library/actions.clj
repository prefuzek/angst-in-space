(ns angst.library.actions
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn empire1 [state]
  ((:active state) (:empire state)))

(defn connected?
  "Checks if two planets are connected"
  [state planet1 planet2]
  (some #(= % planet1) (-> state :planets planet2 :connections)))

(defn set-planet-value
	"Assocs value with type in planet info"
	[state planet type value]
	(assoc-in state [:planets planet type] value))

(defn update-planet-value
	"Updates planet info type according to fun"
	[state planet type fun]
	(update-in state [:planets planet type] fun))

(defn set-empire-value
	"Assocs value with type in empire info"
	[state empire type value]
	(assoc-in state [:empire empire type] value))

(defn update-empire-value
	"Updates empire info type according to fun"
	[state empire type fun]
	(update-in state [:empire empire type] fun))

(defn colonize [state planet]
  (if (and (= (-> state :planets planet :colour) "Black")
  		   (> (-> state :planets planet :ships) 0)
  		   (= (-> state :planets planet :ship-colour) (:colour (empire1 state)))
  		   (> (:resources (empire1 state)) 2))
  	  (-> state
  	  	  (update-planet-value planet :ships dec)
  	  	  (set-planet-value planet :colour (:colour (empire1 state)))
  	  	  (update-in [:empire (:active state) :resources] #(- % 3)))
  	  state))

(defn get-resources [state planet]
  (update-empire-value state (:active state) :resources #(+ % (-> state :planets planet :production))))

(defn begin-command
	"Sets :commanding to a planet key and updates the button"
	[state planet]
  (assoc-in
  	(assoc-in state [:commanding] planet)
  	[:buttons 0 :label]
  	"Done Command"))

(defn begin-move
	"Sets :ship-move to {:planet x :ships 1} and updates the button"
	[state planet]
  (if (and (> (-> state :planets planet :ships) 0)
  		   (or (connected? state planet (:commanding state))
  		   	   (= planet (:commanding state))))
    (assoc-in
    	(assoc-in state [:ship-move] {:planet planet :ships 1})
    	[:buttons 0 :label]
    	"Cancel Move") ; Begins a move from planet
    state))

(defn safe-move
	[state to-planet from-planet ship-num]
	(-> state
		(update-in [:empire (:active state) :resources] dec)
		(update-planet-value from-planet :ships #(- % ship-num))
		(update-planet-value to-planet :ships #(+ % ship-num))
		(set-planet-value to-planet :ship-colour (-> state :planets from-planet :ship-colour))
		(assoc-in [:ship-move] false)
		(assoc-in [:buttons 0 :label] "Done Command")))

(defn attack-move
	[state to-planet from-planet ship-num from-info to-info]
	(let [surviving (- ship-num (inc (:ships to-info)))]
	(assoc-in
		(if (> surviving 0)
			(-> state
				(update-in [:empire (:active state) :resources] dec)
				(set-planet-value to-planet :ships surviving)
				(update-planet-value from-planet :ships #(- % ship-num))
				(set-planet-value to-planet :colour (:colour from-info))
				(set-planet-value to-planet :ship-colour (:colour from-info))
				(set-planet-value to-planet :used true)
				(assoc-in [:ship-move] false))
			(-> state
				(update-in [:empire (:active state) :resources] dec)
				(set-planet-value to-planet :ships (- (inc surviving)))
				(update-planet-value from-planet :ships #(- % ship-num))
				(assoc-in [:ship-move] false)))
		[:buttons 0 :label] "Done Command")))

(defn continue-move [state to-planet]
  (let [from-planet (:planet (:ship-move state))
        ship-num (-> state :ship-move :ships)
        to-info (-> state :planets to-planet)
        from-info (-> state :planets from-planet)]
  (if (= to-planet from-planet)
    (if (> (:ships to-info) ship-num) ; Prevents moving more ships than exist
      (update-in state [:ship-move :ships] inc) ; Increases number of ships moving
      state)
    (if (and (connected? state to-planet from-planet) (> (:resources (empire1 state) 0)))
      (if (> (:ships to-info) 0)
      		(if (= (:ship-colour to-info) (:ship-colour from-info))
      				(safe-move state to-planet from-planet ship-num)
      				(attack-move state to-planet from-planet ship-num from-info to-info))
      		(if (or (= (:colour to-info) "Black") (= (:colour to-info) (:ship-colour from-info)))
      			(safe-move state to-planet from-planet ship-num)
      			(attack-move state to-planet from-planet ship-num from-info to-info)))
      state))))

(defn build-ship [state planet]
    (update-in (update-in state [:empire (:active state) :resources] #(- % 2))
               [:planets planet :ships] inc))