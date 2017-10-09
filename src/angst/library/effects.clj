(ns angst.library.effects
	(:require [angst.library.utils :refer :all]
			  [angst.library.data :refer :all]
			  [angst.library.setup :refer :all]
			  [angst.library.network :refer :all]))

; idea: add a permissions parameter to each effect, governing whether host/client can use the effect
; alternatively, add a tag to the buttons?

(def effects
	;each effect takes one argument, state, and possibly others (documented under args)
  {:new-game
  	;starts a new game
  	#(if (>= (count (:empires %)) 2) (new-game %) %)
  :save
  	;saves the gamestate to save.tstatet, returns state unchanged
  	#(do (spit "save.tstatet" %) %)
  :write-server-data
  	;updates serverdata to new state and signals that clients need to update
  	#(do (reset! serverdata %) (reset! client-update-required true) %)
  :load
  	;loads gamestate from save.tstatet
  	;notes: ignores state
  	(fn [x] (load-file "save.txt"))
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
  :start-server
  	(fn [state] (do (.start host-server)
  				(reset! online-state "host")
  				(reset! serverdata state)
  				state))
  :stop-online
  	(fn [state] (do (if (= @online-state "host")
  					(.stop host-server)
  					(reset! serverdata nil))
  	 			(reset! online-state nil)
  	 			state))
  :join-server
  	(fn [state] (do (if (get-host-state state (get-address)) 
  						(reset! online-state "client")	)
  				state))
  :leave-server
  	(fn [state] (do (reset! online-state nil)))
  	})