(ns angst.library.effects
	(:require [angst.library.utils :refer :all]
			  [angst.library.data :refer :all]
			  [angst.library.setup :refer :all]
			  [angst.library.network :refer :all]
        [angst.library.turn :refer :all]
        [angst.library.gcomponents :as g]))

; idea: add a permissions parameter to each effect, governing whether host/client can use the effect
; alternatively, add a tag to the buttons?

(def effects
	;each effect takes one argument (state), and possibly others (documented under args)
  {:new-game
  	;starts a new game
  	#(if (>= (count (:empires %)) 2)
          (-> % (new-game)
                (do-effects effects [[:add-gcomponent :map] [:add-gcomponent :infobar] [:remove-gcomponent :main-menu]]))
          %)

  :save
  	;saves the gamestate to save.txt, returns state unchanged
  	#(do (spit "save.txt" %) %)

  :write-server-data
  	;updates serverdata with necessary data and wipes :extra-update-data
  	#(do (reset! serverdata (select-keys % (into (:extra-update-data %) shared-state))) (assoc-in % [:extra-update-data] []))

  :load
  	;loads gamestate from save.txt
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
                    (-> state
                        (assoc-in [:online-state] :host)
                        (assoc-in [:clients] [])
                        (assoc-in [:extra-update-data] []))))

  :stop-online
  	(fn [state] (do (if (= (:online-state state) :host)
  					 (.stop host-server)
  					 (reset! serverdata nil)) ; TODO: Is serverdata still necessary? probably
                    (assoc-in state [:online-state] :offline)))

  :join-server
  	(fn [state] ;(do (if (get-host-state state (get-address state)) 
  				;		(reset! online-state "client"))
  				;state))

                (if (can-connect? state (get-address state))
                    (-> state 
                      (assoc-in [:online-state] :client)
                      (assoc-in [:data-to-send] []))
                    (do-effects state effects [[:add-gcomponent :could-not-connect-message]])))

  :leave-server ; TODO: May be unused, try removing
  	(fn [state] (do (reset! online-state nil)))

  :add-gcomponent
    ;adds a graphics component to state
    ;args: component
    (fn [state component]
      (-> state (update-in [:components] #(vec (conj % component)))
                (update-in [:buttons] #(merge % (select-keys button-map (:buttons (component g/components)))))
                (update-in [:active-component] #(into % (:active (component g/components))))
                (update-in [:active-text-input] #(if-let [input (:text-input (component g/components))] (conj % input) %))))

  :remove-gcomponent
    ;removes a graphics component from state
    ;args: component
    (fn [state component]
      (-> state (update-in [:components] #(vec (remove (fn [c] (= c component)) %)))
                (update-in [:buttons] #(reduce dissoc % (:buttons (component g/components))))
                (update-in [:active-component] #(vec (remove (fn [c] (= c component)) %)))
                (update-in [:active-text-input] #(vec (remove (fn [i] (= i (:text-input (component g/components)))) %)))))
  	})