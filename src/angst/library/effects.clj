(ns angst.library.effects
	(:require [angst.library.utils :refer :all]
			  [angst.library.data :refer :all]
			  [angst.library.setup :refer :all]
			  [angst.library.network :refer :all]
        [angst.library.turn :refer :all]
        [angst.library.gcomponents :as g]))

; TODO: replace #(foo %) syntax with (fn [x] (foo x)) for all top-level effects

(def effects
	;each effect takes one argument (state), and possibly others (documented under args)
  {:new-game
  	;starts a new game
  	#(if (>= (count (:empires %)) 2)
          (-> % (new-game)
                (do-effects effects [[:remove-gcomponent :all] [:add-gcomponent :map] [:add-gcomponent :infobar]]))
          %)

  :save
  	;saves the gamestate to save.txt, returns state unchanged
  	#(do (spit "save.txt" %) %)

  :write-server-data
  	;updates serverdata with necessary data and wipes :extra-update-data
  	#(do (reset! serverdata (select-keys % (into (:extra-update-data %) shared-state)))
         (reset! client-update-required true)
         (assoc-in % [:extra-update-data] []))

  :load
  	;loads gamestate from save.txt
  	;notes: ignores state
  	(fn [x] (load-file "save.txt"))

  :menu
  	;sets gamestate to menu, ends server/disconnects from server
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
  	(fn [state] (.start host-server)
                (reset! connected-players #{})
                (-> state
                    (assoc-in [:online-state] :host)
                    (assoc-in [:clients] [])
                    (assoc-in [:extra-update-data] [])))

  :stop-server
    (fn [state] (.stop host-server)
                (reset! serverdata nil)
                (assoc-in state [:online-state] :offline))

  :join-server
  	(fn [state]
      (if (connect state (get-address state))
          (-> state 
            (assoc-in [:online-state] :client)
            (do-effects effects [[:add-gcomponent :setup-client] [:remove-gcomponent :main-menu]]))
          (do-effects state effects [[:add-gcomponent :could-not-connect-message]])))

  :leave-server
  	(fn [state] (disconnect state (get-address state))
                (assoc-in state [:online-state] :offline))

  :stop-online
    (fn [state] (condp = (:online-state state)
                  :host (do-effects state effects [[:stop-server]])
                  :client (do-effects state effects [[:leave-server]])
                  state))

  :add-gcomponent
    ;adds a graphics component to state
    ;args: component
    (fn [state component]
      (-> state (update-in [:components] #(vec (conj % component)))
                (update-in [:buttons] #(merge % (select-keys button-map (:buttons (component g/components)))))
                (update-in [:active-component] #(into % (:active (component g/components))))
                (update-in [:active-text-input] #(if-let [input (:text-input (component g/components))] (conj % input) %))))

  :remove-gcomponent
    ;removes a graphics component from state (or all if component is :all)
    ;args: component
    (fn [state component]
      (if (= component :all)
        (-> state (assoc-in [:components] [])
                  (assoc-in [:buttons] {})
                  (assoc-in [:active-component] [])
                  (assoc-in [:active-text-input] []))

        (-> state (update-in [:components] #(vec (remove (fn [c] (= c component)) %)))
                  (update-in [:buttons] #(reduce dissoc % (:buttons (component g/components))))
                  (update-in [:active-component] #(vec (remove (fn [c] (= c component)) %)))
                  (update-in [:active-text-input] #(vec (remove (fn [i] (= i (:text-input (component g/components)))) %))))))

  :set-name
    ;sets online name to name-input if input is non-empty, else does nothing
    (fn [state]
      (let [name-val (-> state :text-inputs :name-input :value)]
        (if-not (= name-val "")
          (do (swap! connected-players conj name-val)
              (-> state (assoc-in [:online-name] name-val)
                        (assoc-in [:online-name-key] (keyword (clojure.string/replace name-val " " "")))
                        (do-effects effects [[:remove-gcomponent :choose-name]])))
          state)))
  })