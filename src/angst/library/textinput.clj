(ns angst.library.textinput
	(:require [quil.core :as q]
			[angst.library.utils :refer :all]
			[angst.library.actionlog :as log]))

(defn draw-text-input [input]
	(if input
		(do (q/no-fill)
			(q/rect-mode (:rect-mode input))
			(q/stroke-weight 2)
			(q/rect (scalex (:x input)) (scaley (:y input)) (scalex (:width input)) (scaley (:height input)))
			(set-fill "White")
			(q/text-align (:text-align input))
			(q/text (or (:value input) "") (scalex (+ (:x input) (:text-offset-x input))) (scaley (+ (:y input) (:text-offset-y input)))))))

(defn update-text-input [state]
	(let [raw-key (char (q/raw-key))
		  active-input (peek (:active-text-input state))]
		  (if active-input
			(cond (= raw-key \u0008)
					(update-in state [:text-inputs active-input :value] #(if (> (count %) 0) (subs % 0 (dec (count %))))) ; Backspace
				(and (= (int raw-key) 10) (= active-input :chat-input)) ; Enter for sending chat messages
					(-> state
						(log/add-log-entry :chat (:active state) (-> state :text-inputs :chat-input :value))
						(assoc-in [:text-inputs :chat-input :value] ""))
				:else
				(update-in state [:text-inputs active-input :value] #(if (< (count %) (-> state :text-inputs active-input :max-length)) (str % raw-key) %))))))