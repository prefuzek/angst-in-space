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
	[item coll]
	(some #(= % item) coll))

(defn get-distance
  [x1 y1 x2 y2]
  (int (max 1 (quot (- (q/sqrt (+ (q/sq (- x1 x2)) (q/sq (- y1 y2)))) 80) 33))))

(defn get-colour-empire
	"Produces the empire (a keyword) associated with a colour"
	[colour]
	(first (first (filter #(= (:colour (second %)) colour) (vec d/all-empires)))))
