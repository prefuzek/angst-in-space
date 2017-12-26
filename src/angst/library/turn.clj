(ns angst.library.turn
	(:require 
		[angst.library.utils :refer :all]
		[angst.library.actionlog :as log]))

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
								(log/add-log-entry :update-project (:active state) planet)
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

(defn get-next-player
  "Changes the active player"
  [state]
   (assoc-in state [:active] ((:active state) (:next-player-map state))))

(defn online-update-button [state]
	(if (not= (:online-state state) :offline) (update-in state [:buttons] #(dissoc % :end-phase)) state))

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
      (log/add-log-entry :end-turn (:active state) (:resources (empire state)) (:vp (empire state)))
      update-projects      
      update-effects
      imperial-points
      online-update-button))