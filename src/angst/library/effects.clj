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
  	(fn [state] (load-file "save.txt"))
  	;#(do % (load-file "save.txt")) ; kludgy but works
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
  :change-buttons
  	;wrapper for change-buttons fn
  	;args removed added
  	change-buttons
  	})