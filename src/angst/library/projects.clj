(ns angst.library.projects
	(:require [angst.library.utils :refer :all]
			  [angst.library.data :refer :all]))

(def project-effects
	"For projects that do something when you click on them while active"
	{:Caia {:effect (fn [state progress]
					  (if (< progress 7)
						(-> state (update-empire-value (:active state) :resources #(- % progress))
						   		  (add-progress :Caia 1))
						(assoc-in state [:active-planet] :Caia)))
			:reqs [(fn [state progress] (>= (:resources (empire state)) progress))]}
	:Bhowmik {:effect (fn [state progress]
						(-> state (update-empire-value (:active state) :resources #(+ % progress))
						  	  	  (update-planet-value :Bhowmik :progress dec)))
			  :reqs [(fn [state progress] (> progress 0))]}
	:Xosa {:effect (fn [state progress] (assoc-in state [:active-planet] :Xosa))
		   :reqs [(fn [state progress] (>= progress 3))]}
	:Entli {:effect (fn [state progress] (assoc-in state [:active-planet] :Entli))
			:reqs [(fn [state progress] (>= progress 5))]}
	:Erasmus {:effect (fn [state progress]
						(-> state (update-planet-value :Erasmus :ships #(+ % (- progress 1)))
								  (set-planet-value :Erasmus :progress 0)))
			  :reqs [(fn [state progress] (>= progress 2))]}})

(defn start-progress
	"Adds proper amount of progress when a project starts"
	[state planet]
	(if (not (effect-active? state :Chiu))
		(add-progress state planet 1)
		(add-progress state planet (inc (quot (-> state :planets :Chiu :progress) 2)))))

(defn start-project
	"Adds planet to project effects list, sets it to active, and adds initial progress"
	[state planet]
	(-> state
		(update-in [:constant-effects :projects] #(vec (cons planet %)))
	    (assoc-in [:planets planet :project] "active")
		(start-progress planet)))
