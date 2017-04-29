(ns angst.library.effects
	(:require [angst.library.utils :refer :all]
			  [angst.library.data :refer :all]
			  [angst.library.setup :refer :all]))

(def effects
	;each effect takes one argument, state, and possibly others (documented under args)
  {:new-game
  	;starts a new game
  	#(if (>= (count (:empires %)) 2) (new-game %) %)
  :save
  	;saves the gamestate to save.txt, returns state unchanged
  	#(do (spit "save.txt" %) %)
  :load
  	;loads gamestate from save.txt
  	;notes: ignores state
  	#(do % (load-file "save.txt")) ; kludgy but works
  :menu
  	;sets gamestate to menu
  	;notes: ignores state
  	(constantly setup-state)
  :toggle-empire
  	;wrapper for toggle-empire fn
  	;args: button empire
  	toggle-empire
  :toggle-option
  	;wrapper for toggle-option fn
  	;args: button option
  	toggle-option
  :end-phase
  	;ends the current phase, including ending the turn after colonization
  	#(if (not= (:phase %) 4)
  				(-> % (update-in [:phase] (fn [x] (mod (inc x) 5)))
  					  (assoc-in [:active-planet] false)
  					  (assoc-in [:constant-effects :phase-end] [])
  					  (update-phase-label))
  				(end-turn %))
  :done-move
  	#(assoc-in % [:effects :ship-move] false)
  :cancel-move
  	;cancels current move
  	#(-> % (assoc-in [:effects :ship-move] false)
  		   (change-buttons [:cancel-move] [:done-command]))
  :done-command
  	;finishes a planet's Command action
  	#(-> % (assoc-in [:active-planet] false)
  		   (change-buttons [:done-command] [:end-phase])
  		   (update-phase-label))
  :cancel-ability
  	;cancels using a planet's ability
  	#(-> % (assoc-in [:active-planet] false)
       	   (update-phase-label)
  		   (change-buttons [:cancel-ability] [:end-phase]))
  :set-active
  	;sets a planet to active
  	;args: planet
  	#(-> %1 (assoc-in [:active-planet] %2)
  					  (change-buttons [:end-phase] [:cancel-ability]))
  :add-resources
  	;adds resources to an empire's total
  	;args: empire amt
  	#(update-empire-value %1 %2 :resources (fn [x] (+ x %3)))
  :add-points
  	;adds points to empire total if all reqs are true
  	;args: empire amt & reqs
  	#(if (reduce (fn [x y] (and x y)) true %&)
					(update-empire-value %1 %2 :vp (fn [x] (x % %3)))
				%1)
  :planet-set
  	;sets planet information according to a type-val map
  	;args planet new-info
  	(fn [state planet new-info] (update-in state [:planets planet] #(merge % new-info)))
  :planet-add
  	;adds a map of type-values to the corresponding info in a planet
  	;args planet add-info
  	(fn [state planet add-info] (update-in state [:planets planet] #(merge-with + % add-info)))
  :conquer
  	;changes ownership of a planet and sets ships, ship-moved to number of surviving ships
  	;args: planet new-colour surviving
  	#(update-in %1 [:planets %2] (fn [x] (merge x {:ships %4 :moved %4 :colour %3 :ship-colour %3 :used true})))
  :change-buttons
  	;wrapper for change-buttons fn
  	;args removed added
  	change-buttons})
