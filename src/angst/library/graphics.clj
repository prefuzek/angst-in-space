(ns angst.library.graphics
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.utils :refer :all]
            [angst.library.data :refer :all]))

(def infobar-width 300)

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
    (= colour "Pink")
      (q/fill 220 255 255)
    (= colour "Black")
      (q/fill 0 0 0)))

(defn draw-distance
  [x1 y1 x2 y2]
  (let [distance (get-distance x1 y1 x2 y2)]
    (if (and (not= distance 0))
      (dotimes [n distance]
          (set-fill "Black")
          (q/ellipse (+ (scalex x1) (* n (/ (- (scalex x2) (scalex x1)) distance)))
                     (+ (scaley y1) (* n (/ (- (scaley y2) (scaley y1)) distance)))
                     5 5)))))

(defn draw-connections [state]
  (doseq [p (seq (:planets state))]
    (doseq [q (:connections (second p))]
      (q/line (scalex (:x (second p)))
              (scaley (:y (second p)))
              (scalex (:x (q (:planets state))))
              (scaley (:y (q (:planets state)))))
      (draw-distance (:x (second p))
                     (:y (second p))
                     (:x (q (:planets state)))
                     (:y (q (:planets state)))))))

(defn draw-ships [x y ships]
  (doseq [s ships]
      (q/ellipse (+ x (* -3 (count ships)) (* s 6) 3) (+ y 15) 5 5)))

(defn draw-planets [state]
  (doseq [p (seq (:planets state))]
    (set-fill (:colour (second p)))
    (if (and (= (:colour (second p)) (:colour (empire state))) (not (:used (second p))))
      (do
      (q/stroke 0 0 255)
      (q/stroke-weight 2)))
    (q/ellipse (scalex (:x (second p))) (scaley (:y (second p))) 21 21)
    (q/stroke 0 0 100)
    (q/stroke-weight 1)))

(defn text-display
  "Consumes a map giving relative alignments and locations of text, and draws it at a given location"
  [text-map x y]
  (doseq [t (seq text-map)]
    (if (string? (first t)) (do
     (q/text-align (:align (second t)))
     (q/text-size (:size (second t)))
     (q/text (first t) (+ x (:x (second t))) (+ y (:y (second t)))))))
  (q/text-size 12))

(defn get-unused-planets
  "Produces a seq of the name strings of planets current empire controls that haven't been used"
  [state]
  (map :name (filter #(and (not (:used %)) (= (:colour (empire state)) (:colour %)))
  					(map second (seq (:planets state))))))

(defn get-message [state]
  (cond 
    (:ship-move (:effects state))
                  (str "Moving " (:ships (:ship-move (:effects state))) " ships from " (:name ((:planet (:ship-move (:effects state))) (:planets state))))
    (= (:phase state) 0)
       (if (:active-planet state)
           (str "Select target for " (:name ((:active-planet state) (:planets state))))
           "Click on planets to use their abilities")
    (= (:phase state) 1)
       "Click on planets to gain resources"
    (= (:phase state) 2)
       (if (:active-planet state)
           (str "Commanding with " (:name ((:active-planet state) (:planets state))) "\nChoose a connected planet to move ships from")
           "Choose a planet to command with")
    (= (:phase state) 3)
        "Click on planets to build ships"
    (= (:phase state) 4)
       "Choose planets to colonize"
    :else "Something's wrong..."))

(defn draw-infobar [state]
  ;vertical line
  (q/line (scalex 1066) 0 (scalex 1066) (q/height))
  ;horizontal partitions
  (q/line (scalex 1066) (scaley 264) (q/width) (scaley 264))
  (q/line (scalex 1066) (scaley 384) (q/width) (scaley 384))
  ;planet info
  (if @planet-info-display
    (let [planet (@planet-info-display (:planets state))]
      (if (>= (:development planet) 0)
        (do
          (set-fill "Black")
          (q/ellipse (+ (scalex 1066) 98 (* 15 (:development planet))) 70 18 18)      
          (set-fill "White")))
      (text-display {(:name planet) {:align :center :x (scalex 150) :y 20 :size 18}
                 (str "Ships: " (:ships planet)) {:align :left :x 20 :y 50 :size 12}
                 (str "Production:  " (clojure.string/join "  " (:production planet))) {:align :left :x 20 :y 75 :size 12}
                 (str (split-text-lines (:ability planet) (scalex 260))) {:align :left :x 20 :y 125 :size 12}
                 (if (:project planet) (str "Project: " (if (= (:project planet) "inactive") "Inactive" (str (:progress planet) "\u00a7"))))
                 (if (:project planet) {:align :left :x 20 :y 100 :size 12})}
                (scalex 1066)
                0)))
  (text-display {(str (:name (empire state)) " Empire") {:align :center :x (scalex 150) :y 20 :size 18}
                 (:major (empire state)) {:align :center :x (scalex 150) :y 42 :size 12}
                 (str "Resources: " (:resources (empire state))) {:align :left :x 20 :y 60 :size 12}
                 (str "Victory Points:" (:vp (empire state))) {:align :left :x 20 :y 80 :size 12}
                 (str "Upkeep: " (get upkeep (get-num-planets state (:active state)))) {:align :left :x 20 :y 100 :size 12}
                 (str "Unused Planets:\n" (apply str (map #(str % "\n") (get-unused-planets state))))
                    {:align :left :x 20 :y 120 :size 12}}
                 (scalex 1066)
                 (scaley 384))
  (let [message (get-message state)]
  	(text-display {message {:align :center :x 0 :y 0 :size 12}}
  				  (scalex 1216)
  				  (scaley 284))))

(defn draw-buttons [state]
  (q/rect-mode :center)
  (q/text-align :center)
  (q/no-fill)
  (doseq [b (map second (vec (:buttons state)))]
    (q/rect (scalex (:x b)) (scaley (:y b)) (scalex (:width b)) (scaley (:height b)) 10))
  (q/fill 255))

(defn text-buttons [state]
  (doseq [b (map second (vec (:buttons state)))]
    (q/text (:label b) (scalex (:x b)) (scaley (:y b)))))

(defn draw-setup [state]
  (q/background 0)
  (text-display {"Angst In Space" {:align :center :x (scalex 683) :y (scaley 100) :size 40}
           "Options" {:align :center :x (scalex 883) :y (scaley 200) :size 20}
           "Empires" {:align :center :x (scalex 483) :y (scaley 200) :size 20}} 0 0)
  (draw-buttons state)
  (set-fill "White") (text-buttons state))
 
(defn draw-game [state]
  (q/background 0)
  ; Draw the infobar
  (set-fill "White") (draw-infobar state)
  ; Draw all buttons
  (draw-buttons state)
  (set-fill "White") (text-buttons state)
  ; Draw the planet connections
  (draw-connections state)
  ; Draw the planets
  (draw-planets state)
  ; Name the planets
  (q/text-align :center)
  (doseq [p (seq (:planets state))]
    (set-fill "White")
    (q/text (:name (second p)) (scalex (:x (second p))) (scaley (- (:y (second p)) 15))))
  ; Draw the ships
  (doseq [p (seq (:planets state))]
    (set-fill (:ship-colour (second p)))
    (draw-ships (scalex (:x (second p))) (scaley (:y (second p))) (range (:ships (second p)))))
  ; Diagnostics:
  )

(defn draw-state
  [state]
  (if (= (:phase state) "setup")
      (draw-setup state)
    (draw-game state)))