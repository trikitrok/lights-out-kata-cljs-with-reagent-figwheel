(ns kata-lights-out.core
  (:require
    [kata-lights-out.lights-view :as light-view]
    [cljs.core.async :as async]
    [com.stuartsierra.component :as component]
    [kata-lights-out.lights :as lights]))

(enable-console-print!)

;; -------------------------
;; Initialize app
(defrecord MainComponent [lights-component m n]
  component/Lifecycle
  (start [this]
    (println ";; Starting main component")
    (lights/reset-lights! lights-component m n)
    (light-view/mount lights-component)
    this)

  (stop [this]
    (println ";; Stopping lights component")
    this))

(defn main-component [m n]
  (map->MainComponent {:n n :m m}))

(defn init! [m n]
  (component/start
    (component/system-map
      :lights-component (lights/map->Lights {:lights-channel (async/chan)})
      :main (component/using
              (main-component m n)
              [:lights-component]))))

(init! 3 3)