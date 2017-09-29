(ns angst.library.utils
	(:require [quil.core :as q]
			  [angst.library.data :as d]))

(def upkeep [0 0 0 1 1 2 3 4 5 6 7 8 10 12 14 16])

(defn empire [state]
  ((:active state) (:empire state)))

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

(defn scaley
  "Scales a number to the user's screen height"
  [n]
  (* n (/ (q/height) 768)))

(defn scalex
  "Scales a number to the user's screen width"
  [n]
  (* n (/ (q/width) 1366)))

(defn get-num-planets [state empire]
  (let [colour (:colour (empire (:empire state)))]
    (count (filter #(= (:colour %) colour) (map second (seq (:planets state)))))))

(defn member?
	"Predicate to determine if item is contained in any of colls"
	[item & colls]
	(reduce #(or %1 (some (fn [x] (= x item)) %2)) false colls))

(defn get-distance
  [x1 y1 x2 y2]
  (int (max 1 (quot (- (q/sqrt (+ (q/sq (- x1 x2)) (q/sq (- y1 y2)))) 80) 33))))

(defn get-connected-distance
	[state p1 p2]
	(loop [acc 0 currlayer [p1] nextlayer [] visited []]
		(cond (empty? currlayer)
		 	   (recur (inc acc) nextlayer [] visited)
			  (= (first currlayer) p2)
			   acc
		 	  :else
		 	  	(recur acc (rest currlayer)
		 	  		  (into nextlayer (remove #(member? % nextlayer visited) (:connections ((first currlayer) (:planets state)))))
		 	  		  (conj visited (first currlayer))))))

(defn get-colour-empire
	"Produces the empire (a keyword) associated with a colour"
	[colour]
	(if (= colour "Black")
		nil
		(first (first (filter #(= (:colour (second %)) colour) (vec d/all-empires))))))

(defn get-planet-empire
	"Produces the empire (a keyword) that controls a planet"
	[state planet]
	(get-colour-empire (:colour (planet (:planets state)))))

(defn get-planet-ship-empire
	"Produces the empire (a keyword) that has ships on a planet"
	[state planet]
	(if (= (-> state :planets planet :ships) 0) "Black"
		(get-colour-empire (:ship-colour (planet (:planets state))))))

(defn planet-active?
	"Predicate to check if planet belongs to active player"
	[state planet]
	(= (get-planet-empire state planet) (:active state)))

(defn planet-owned?
	"Checks if planet controlled by empire"
	[state planet empire]
	(= (:colour (planet (:planets state))) (:colour (empire (:empire state)))))

(defn same-empire?
	"Checks if two planets are controlled by the same empire"
	[state p1 p2]
	(= (get-planet-empire state p1) (get-planet-empire state p2)))

(defn develop-all
	"Increases all the active player's planets development by one"
  [state]
  (reduce (fn [x y] (update-planet-value x y :development #(min 7 (inc %))))
                    state
                    (map first (filter #(= (:colour (second %)) (:colour (empire state))) (vec (:planets state))))))

(defn reset-all-used
	"Sets all planets to :used false"
	[state]
  (reduce (fn [x y] (set-planet-value x y :used false))
                     state
                     (map first (vec (:planets state)))))

(defn toggle-empire
  "Toggles an empire button"
  [state button empire]
  (cond (clojure.string/includes? (:label (button (:buttons state))) ": No")
        (-> state
          (update-in [:empires] #(conj % empire))
          (update-in [:buttons button :label] #(clojure.string/replace % #": No" ": Yes")))
      (clojure.string/includes? (:label (button (:buttons state))) ": Yes")
        (-> state
          (update-in [:empires] #(disj % empire))
          (update-in [:buttons button :label] #(clojure.string/replace % #": Yes" ": No")))
      :else state))

(defn toggle-option
	"Toggles an option button"
	[state button option]
	(cond
		(clojure.string/includes? (:label (button (:buttons state))) ": Off")
		  	(-> state
		    	(update-in [:options] #(conj % option))
		    	(update-in [:buttons button :label] #(clojure.string/replace % #": Off" ": On")))
		  (clojure.string/includes? (:label (button (:buttons state))) ": On")
		  	(-> state
		    	(update-in [:empires] #(disj % option))
		    	(update-in [:buttons button :label] #(clojure.string/replace % #": On" ": Off")))
		:else state))

(defn add-points
	[state empire n & reqs]
	(if (reduce #(and %1 %2) true reqs)
			(update-empire-value state empire :vp #(+ % n))
		state))

(defn imperial-points
	"Updates points for Imperialists"
	[state]
	(let [num-less (count (filter #(> (get-num-planets state (:active state)) (get-num-planets state (first %))) (vec (:empire state))))]
		(add-points state (:active state) num-less (= (:major (empire state)) "Imperialists"))))

(defn get-next-player
  "Changes the active player"
  [state]
   (assoc-in state [:active] ((:active state) (:next-player-map state))))

(defn effect-active?
	"Predicate to determine if an effect is active"
	[state effect]
	(apply member? effect (vals (:constant-effects state))))

(defn update-effects
	"Removes all ongoing effects that should expire"
	[state]
	(-> state
		(assoc-in [:constant-effects (:active state)] [])
		(assoc-in [:constant-effects :turn-end] [])))

(defn add-progress
	[state planet amount]
	(if (and (= (get-planet-empire state planet) (get-planet-empire state :Jaid)) (effect-active? state :Jaid))
		(update-planet-value state planet :progress #(+ % (inc amount)))
		(update-planet-value state planet :progress #(+ % amount))))

(defn update-projects
	[state]
	(letfn [(update-planet
				[state planet]
				(cond (= (-> state :planets planet :project) "active")
						(if (= (get-planet-empire state planet) (:active state))
							(-> state
								(add-progress planet 1)
								(set-planet-value planet :used true))
							state)
					(= (-> state :planets planet :project) "inactive")
						(set-planet-value state planet :progress 0)
					:else state))]
		(reduce update-planet state (map first (vec (:planets state))))))

(defn end-project
	[state planet]
	(if (-> state :planets planet :project)
		(-> state
			(set-planet-value planet :project "inactive")
			(set-planet-value planet :used false)
			;(set-planet-value planet :progress 0)) -- test to solve project structure weirdness
			)
		state))

(defn end-turn
	"Performs end-of-turn updates"
	[state]
  (-> state
      reset-all-used
      develop-all
      (assoc-in [:phase] 0)
      (assoc-in [:buttons :end-phase :label] "End Specialization Phase")
      (update-empire-value (:active state) :resources
        #(- % (get upkeep (get-num-planets state (:active state)))))
      (add-points (:active state) 1 (= (:major (empire state)) "Immortals"))
      get-next-player
      update-projects      
      update-effects
      imperial-points))

(defn change-buttons
	"Consumes two vectors of keys; one of buttons that are removed and one of buttons that are added"
	[state removed added]
	(-> state (update-in [:buttons] #(reduce dissoc % removed))
			  (update-in [:buttons] #(merge % (select-keys d/button-map added)))))

(defn update-phase-label
	"Updates the phase label to match the phase"
	[state]
	(assoc-in state [:buttons :end-phase :label] (d/phase-labels (:phase state))))

(defn do-effect
	[[effect & args] effects state]
	(apply (effect effects) (cons state args)))

(defn do-effects
	[state emap evec]
	(reduce #(do-effect %2 emap %1) state evec))

(defn move-cost
	[state distance]
	(let [modifiers {:Tomaso -2
					 :Byrd (if (= (get-planet-empire state :Byrd) (:active state))
					 			(- (-> state :planets :Byrd :progress))
					 			0)
					 :Brahms 2}]
		(max 0 (reduce-kv #(if (effect-active? state %2) (+ %1 %3) %1) (dec distance) modifiers))))

(defn col-cost
	[state planet]
	(let [modifiers {:Yerba -10}]
		(max 0 (reduce-kv #(if (effect-active? state %2) (+ %1 %3) %1) 3 modifiers))))

(defn planet-defense
	[state planet]
	(let [modifiers {:Zellner (if (and (same-empire? state planet :Zellner)
										(< (get-connected-distance state :Zellner planet) (-> state :planets :Zellner :progress)))
									1 0)
					 :Path (if (= (:Path (:effect-details state)) planet)
					 			2 0)
					 :Thalia (if (same-empire? state planet :Thalia)
					 	(quot (-> state :planets :Thalia :progress) 3)
					 	0)
					 :Quinz (if (= (:Quinz (:effect-details state)) planet)
					 	-1 0)
					 :Froya (if (same-empire? state planet :Froya)
					 	1 0)}]
		(max 0 (reduce-kv #(if (effect-active? state %2) (+ %1 %3) %1) (-> state :planets planet :ships) modifiers))))

(defn check-dengras
	[state colour]
	(if (and (effect-active? state :Dengras) (= (-> state :planets :Dengras :colour) colour))
		(update-empire-value state (:active state) :resources #(max 0 (- % 4)))
		state))

(defn check-shoran
	[state ship-num]
	(if (and (effect-active? state :Shoran) (planet-active? state :Shoran))
		(update-empire-value state (:active state) :resources #(+ % (* 2 ship-num)))
		state))

(defn check-marishka
	[state planet]
	(if (and (effect-active? state :Marishka) (planet-active? state :Marishka))
		(update-planet-value state planet :ships inc)
		state))

(defn check-altu
	[state]
	(if (effect-active? state :Altu)
		(update-empire-value state (:active state) :resources #(+ % 2))
		state))

(defn split-text-lines
	"Consumes a string and produces a new string properly broken into lines for the appropriate width"
	[text width]
	(loop [words (clojure.string/split text #" ")
		   newtext ""]
		   (cond (empty? words)
		   			newtext
		   		 (> (q/text-width (str newtext (first words) " ")) width)
		   		 	(recur (rest words) (str newtext "\n" (first words) " "))
		   		 :else (recur (rest words) (str newtext (first words) " ")))))

(defmacro thrush-list
	[value funs]
	(conj (conj funs value) '->))