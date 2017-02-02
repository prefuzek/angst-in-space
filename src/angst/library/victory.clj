(ns angst.library.victory
	(:require [angst.library.utils :refer :all]))

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