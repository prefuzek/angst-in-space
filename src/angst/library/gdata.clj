(ns angst.library.gdata)

(def screen-width 1366)
(def screen-height 768)

; -------
; Infobar
; -------

(def infobar-width 300)
(def infobar-right-edge (- screen-width infobar-width))
(def infobar-center-x (- screen-width (/ infobar-width 2)))
(def planet-info-bottom 230)
(def button-message-y 250)
(def empire-display-top 350)
(def empire-display-bottom 480)
(def message-log-start 495)
(def message-log-height (- screen-height empire-display-bottom 20)) ; Currently 272