(ns angst.library.graphics
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [angst.library.utils :refer :all]
            [angst.library.data :refer :all]
            [angst.library.gcomponents :as c]
            [angst.library.textinput :as input]))

(defn draw-buttons [state component]
  (q/fill 0 0 255) ;White
  (q/rect-mode :center)
  (q/text-align :center)
  (q/stroke-weight 2)
  (q/no-fill)
  (let [component-buttons (select-keys (:buttons state) (into (:buttons (component c/components)) (:hidden-buttons (component c/components))))]
    (doseq [b (map second (vec component-buttons))]
      (q/rect (scalex (:x b)) (scaley (:y b)) (scalex (:width b)) (scaley (:height b)) 10)
      (q/text (:label b) (scalex (:x b)) (scaley (:y b))))
    (q/fill 0 0 255)
    (doseq [b (map second (vec component-buttons))]
      (q/text (:label b) (scalex (:x b)) (scaley (:y b))))))

(defn draw [state]
  (q/background 0)
  (doseq [component (:components state)]
    (let [comp-info (component c/components)]
      ((:draw-fn comp-info) state)
      (draw-buttons state component)
      (if (:text-input comp-info) (input/draw-text-input ((:text-input comp-info) (:text-inputs state)))))))