(ns angst.library.setup
	(:require [quil.core :as q]
			  [angst.library.utils :refer :all]
			  [angst.library.data :refer :all]))

(defn set-goals
	"Assigns each empire a unique goal"
	[state]
	(loop [rem-goals '("Immortals" "Warlords" "Conquistadores" "Imperialists" "Slavers")
		   rem-emps (map first (seq (:empire state)))
		   new-state state]
		(if (empty? rem-emps)
			  new-state
			(let [goal (rand-nth rem-goals)]
				(recur (remove #(= % goal) rem-goals)
					   (rest rem-emps)
					   (set-empire-value new-state (first rem-emps) :major goal))))))

(defn get-second-planet [rem-planets choices]
	"Chooses an unoccupied planet from choices"
	(if (empty? choices) nil
		(let [choice (rand-nth choices)]
		(if 
		  (member? choice rem-planets) choice
		  (recur rem-planets (remove #(= % choice) choices))))))

(defn rand-start-planets
	"Gives each player two connected planets at random"
	[state]
	(loop [rem-planets (map first (seq (:planets state)))
		   rem-emps (map first (seq (:empire state)))
		   new-state state]
		(if (empty? rem-emps)
			  new-state
			(let [planet1 (rand-nth rem-planets)
				  planet2 (get-second-planet rem-planets (-> new-state :planets planet1 :connections))
				  colour (:colour ((first rem-emps) (:empire state)))]
				(recur (remove #(or (= % planet1) (= % planet2)) rem-planets)
					   (rest rem-emps)
					   (-> new-state
					   		(update-in [:planets planet1] #(merge % {:colour colour :ship-colour colour :ships 1 :development 3}))
					   		(update-in [:planets planet2] #(merge % {:colour colour :ship-colour colour :ships 1 :development 0}))))))))

(defn fixed-start-planets
	"Sets up a balanced-ish map based on the number of players"
	[state]
	(let [setups
		 {2 [[:Echemmon :Altu] [:VanVogt :Jaid]]
		  3 [[:Brahms :Uchino] [:Path :Lisst] [:Bhowmik :Dengras]]
		  4 [[:Odyssey :Uchino] [:Path :Quinz] [:Erasmus :Iago] [:Bhowmik :Walden]]
		  5 [[:Erasmus :Froya] [:Path :Byrd] [:Chiu :Henz] [:Bhowmik :Walden] [:Valeria :Uchino]]}
		  align-planets (fn [x y] {(first y) (merge ((first y) (planet-maps (count (:empire state)))) {:colour (:colour (second x))
		  																		 :ship-colour (:colour (second x))
		  																		 :ships 1
		  																		 :development 3})
		  						   (second y) (merge ((second y) (planet-maps (count (:empire state)))) {:colour (:colour (second x))
		  																		 :ship-colour (:colour (second x))
		  																		 :ships 1
		  																		 :development 0})})]
		(update-in state [:planets] #(reduce merge % (map align-planets (vec (:empire state)) (setups (count (:empire state))))))))

(defn get-next-player-map
	([empires] (get-next-player-map empires 0 (count empires) {}))
	([empires i end m]
		(if (= i end) m
			(get-next-player-map empires (inc i) end (merge m {(empires i) (empires (mod (inc i) end))})))))

(defn set-players
	[state empires]
	(merge state {:empire (select-keys all-empires empires)
				  :active (first empires)
				  :next-player-map (get-next-player-map (vec empires))}))

(defn new-game
	"Launches a new game according to settings"
	[state]
	(let [add-players (set-players init-state (:empires state))
		  add-planets (assoc-in add-players [:planets] (planet-maps (count (:empires state))))]
	(if (member? "rand-start" (:options state))
		(if (member? "goals" (:options state))
			  (rand-start-planets (set-goals add-planets))
			(rand-start-planets add-planets))
		(if (member? "goals" (:options state))
			  (fixed-start-planets (set-goals add-planets))
			(fixed-start-planets add-planets)))))

(defn setup []
  ; Set frame rate to 10 frames per second.
  (q/frame-rate 10)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; Set line color to grey
  (q/stroke 100)
  ;Set text preferences
  (q/text-align :center)
  (q/text-size 12)
  ; Initialize state
  setup-state)