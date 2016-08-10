(ns kata-lights-out.core
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as l]
    [kata-lights-out.lights-view :as light-view]))

(enable-console-print!)

;; -------------------------
;; Initialize app
(def m 3)
(def n 3)

(defn init! []
  (light-view/mount m n))

(init!)