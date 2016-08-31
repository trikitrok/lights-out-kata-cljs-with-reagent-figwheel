(ns kata-lights-out.core
  (:require
    [kata-lights-out.lights-view :as lights-view]
    [com.stuartsierra.component :as component]
    [kata-lights-out.lights :as lights]
    [kata-lights-out.lights-gateway :as lights-gateway]))

(enable-console-print!)

;; -------------------------
;; Initialize app
(defrecord MainComponent [lights-component m n]
  component/Lifecycle
  (start [this]
    (println ";; Starting main component")
    (lights/reset-lights! lights-component m n)
    (lights-view/mount lights-component)
    this)

  (stop [this]
    (println ";; Stopping lights component")
    this))

(defn main-component [m n]
  (map->MainComponent {:n n :m m}))

(defn init! [m n]
  (component/start
    (component/system-map
      :lights-gateway (lights-gateway/make-api-gateway
                        {:reset-lights-url "http://localhost:3000/reset-lights"
                         :flip-light-url "http://localhost:3000/flip-light"})

      :lights-component (component/using
                          (lights/make-lights)
                          [:lights-gateway])

      :main (component/using
              (main-component m n)
              [:lights-component]))))

(init! 3 3)
