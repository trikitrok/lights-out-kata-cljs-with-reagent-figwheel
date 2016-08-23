(ns kata-lights-out.core
  (:require
    [kata-lights-out.lights-view :as light-view]
    [cljs.core.async :as async]))

(enable-console-print!)

;; -------------------------
;; Initialize app
(def m 3)
(def n 3)
(def lights-channel (async/chan))

(defn init! []
  (light-view/mount lights-channel m n))

(init!)