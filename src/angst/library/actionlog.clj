(ns angst.library.actionlog
	(:require [angst.library.utils :refer :all]
			[angst.library.gdata :refer :all]))

(def messages
	{
	:chat (fn [empire message]
		(str empire ": " message))
	:end-turn (fn [empire resources vp]
		(str "The " empire " Empire ends their turn with " resources "\u0398 and " vp "vp.\n------------------------------"))
	:start-project (fn [empire planet progress]
		(str "The " empire " Empire starts a project on " planet " (" progress "\u00A7)."))
	:update-project (fn [empire planet]
		(str "The " empire " Empire's project on " planet " gains 1\u00A7."))
	:end-project (fn [empire planet]
		(str "The " empire " ends their project on " planet "."))
	:produce-resources (fn [empire planet num-resources]
		(str "The " empire " Empire uses " planet " to gain " num-resources "\u0398."))
	:build-ship (fn [empire planet]
		(str "The " empire " Empire builds a ship on " planet "."))
	:begin-command (fn [empire planet]
		(str "The " empire " Empire begins commanding from " planet "."))
	:safe-move (fn [empire ships from-planet to-planet]
		(str "The " empire " Empire moves " (get-plural ships "ship" "ships") " from " from-planet " to " to-planet "."))
	:attack-move (fn [empire ships from-planet to-planet to-ships]
		(str "The " empire " Empire sends " (get-plural ships "ship" "ships") " from " from-planet " to attack " to-planet
			 " ("(get-plural to-ships "defender" "defenders") ")."))
	:conquer (fn [empire to-planet]
		(str "The " empire " Empire conquers " to-planet "!"))
	:unsuccessful-attack (fn [empire to-planet]
		(str to-planet "successfully defends against the " empire " Empire's attack!"))
	:colonize (fn [empire planet]
		(str "The " empire " Empire colonizes " planet "."))
	:gain-points (fn [empire points]
		(str "The " empire " Empire gains " points " points.")) ; Not in use yet
	:player-join (fn [player]
		(str player " has joined the game."))
	:player-leave (fn [player]
		(str player " has left the game."))
	})

(defn get-log-height [log]
	(letfn [(get-message-height [message] )])
	(reduce #(+ %1 (* 20 (inc (or (get (frequencies %2) \newline) 0)))) 0 log))

(defn drop-log-lines [log]
	(loop [new-log log]
		(if (> (get-log-height new-log) message-log-height)
			(recur (drop 1 new-log))
			new-log)))

(defn sanitize [args]
	(map #(clojure.string/replace (str %) ":" "") args))

(defn add-log-entry [state message & args]
	(update-in state [:action-log]
		#(into [] 
			(drop-log-lines 
				(conj % (split-text-lines (apply (message messages) (sanitize args)) (scalex (- infobar-width 10))))))))

(defn add-ability-entry [state message & args]
	(if message
		(update-in state [:action-log]
		#(into [] 
			(drop-log-lines 
				(conj % (split-text-lines (apply message (sanitize args)) (scalex (- infobar-width 10)))))))
		state))