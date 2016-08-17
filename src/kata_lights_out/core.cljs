(ns kata-lights-out.core
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as l]
    [kata-lights-out.lights-view :as light-view]
    [cljs.core.async :as async]))

(enable-console-print!)

;; -------------------------
;; Initialize app
(def m 4)
(def n 4)
(def lights-channel (async/chan))

(defn init! []
  (light-view/mount lights-channel m n))

(init!)