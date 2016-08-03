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
(def lights (r/atom []))

(defn init! []
  (reset! lights (l/all-lights-on m n))
  (light-view/mount m n lights))

(init!)