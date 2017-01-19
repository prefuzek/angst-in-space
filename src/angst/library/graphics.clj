(ns angst.library.graphics
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn empire2 [state]
  ((:active state) (:empire state)))

(defn draw-connections [state]
  (doseq [p (seq (:planets state))]
    (doseq [q (:connections (second p))]
      (q/line (:x (second p)) (:y (second p)) (:x (q (:planets state))) (:y (q (:planets state)))))))

(defn draw-ships [x y ships]
  (doseq [s ships]
      (q/ellipse (+ x (* -3 (count ships)) (* s 6) 3) (+ y 15) 5 5)))

(defn text-display
  "Consumes a map giving relative alignments and locations of text, and draws it at a given location"
  [text-map x y]
  (doseq [t (seq text-map)]
    (q/text-align (:align (second t)))
    (q/text-size (:size (second t)))
    (q/text (first t) (+ x (:x (second t))) (+ y (:y (second t))))))

(defn get-unused-planets
  "Produces a seq of the name strings of planets current empire controls that haven't been used"
  [state]
  (map :name (filter #(and (not (:used %)) (= (:colour (empire2 state)) (:colour %)))
  					(map second (seq (:planets state))))))

(def upkeep [0 0 0 1 1 2 3 4 5 6 7 8 10 12 14 16])

(defn get-num-planets [state empire]
  (let [colour (:colour (empire (:empire state)))]
    (count (filter #(= (:colour %) colour) (map second (seq (:planets state)))))))

(defn get-command-message [state]
	(cond (:ship-move state)
			(str "Moving " (:ships (:ship-move state)) " ships from " (:name ((:planet (:ship-move state)) (:planets state))))
		  (:commanding state)
		  	(str "Commanding with " (:name ((:commanding state) (:planets state)))
		  		"\nChoose a connected planet to move ships from")
		  :else "Choose a planet to command with"))

(defn draw-infobar [state]
  ;vertical line
  (q/line (- (q/width) 300)
           0 (- (q/width) 300) (q/height))
  ;horizontal partitions
  (q/line (- (q/width) 300) (- (/ (q/height) 2) 120) (q/width) (- (/ (q/height) 2) 120))
  (q/line (- (q/width) 300) (/ (q/height) 2) (q/width) (/ (q/height) 2))
  ;planet info
  (if (:planet (:display state))
    (let [planet ((:planet (:display state)) (:planets state))]
      (text-display {(:name planet) {:align :center :x (/ (:infobar-width (:display state)) 2) :y 20 :size 18}
                 (str "Ships: " (:ships planet)) {:align :left :x 20 :y 50 :size 12}
                 (str "Production: " (:production planet)) {:align :left :x 20 :y 75 :size 12}}
                (- (q/width) (:infobar-width (:display state)))
                0)))
  (text-display {(str (:name (empire2 state)) " Empire") {:align :center :x (/ 300 2) :y 20 :size 18}
                 (str "Resources: " (:resources (empire2 state))) {:align :left :x 20 :y 50 :size 12}
                 (str "Upkeep: " (get upkeep (get-num-planets state (:active state)))) {:align :left :x 20 :y 75 :size 12}
                 (str "Unused Planets:\n" (apply str (map #(str % "\n") (get-unused-planets state))))
                    {:align :left :x 20 :y 100 :size 12}}
                 (- (q/width) 300)
                 (/ (q/height) 2))
  (let [message (cond (= (:phase state) 0)
  						"Click on planets to use their abilities"
  					  (= (:phase state) 1)
  					  	"Click on planets to gain resources"
  					  (= (:phase state) 2)
  					  	(get-command-message state)
  					  (= (:phase state) 3)
  					  	"Click on planets to build ships"
  					  (= (:phase state) "Colonization")
  					  	"Choose planets to colonize"
  					  :else "Something's wrong...")]
  	(text-display {message {:align :center :x 0 :y 0 :size 12}}
  				  (- (q/width) 150)
  				  (- (/ (q/height) 2) 100))))

(defn draw-buttons [state]
  (q/rect-mode :center)
  (q/text-align :center)
  (q/no-fill)
  (doseq [b (:buttons state)]
    (q/rect (:x b) (:y b) (:width b) (:height b) 10))
  (q/fill 255))

(defn text-buttons [state]
  (doseq [b (:buttons state)]
    (q/text (:label b) (:x b) (:y b))))

(defn set-fill [colour]
  (cond (= colour "Blue")
	      (q/fill 160 360 255)
		(= colour "White")
		  (q/fill 0 0 255)
		(= colour "Yellow")
		  (q/fill 45 360 255)
		(= colour "Green")
		  (q/fill 100 360 200)
		(= colour "Red")
		  (q/fill 0 255 255)
		(= colour "Black")
		  (q/fill 0 0 0)))