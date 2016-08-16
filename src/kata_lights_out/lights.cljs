(ns kata-lights-out.lights)

(def ^:private light-on 1)
(def ^:private light-off 0)

(defn- neighbors? [[i0 j0] [i j]]
  (or (and (= j0 j) (= 1 (Math/abs (- i0 i))))
      (and (= i0 i) (= 1 (Math/abs (- j0 j))))))

(defn- neighbors [m n pos]
  (for [i (range m)
        j (range n)
        :when (neighbors? pos [i j])]
    [i j]))

(defn light-off? [light]
  (= light light-off))

(defn- flip-light [light]
  (if (light-off? light)
    light-on
    light-off))

(defn- flip [lights pos]
  (update-in lights pos flip-light))

(defn- num-rows [lights]
  (count lights))

(defn- num-colums [lights]
  (count (first lights)))

(defn- flip-neighbors [pos lights]
  (->> pos
       (neighbors (num-rows lights) (num-colums lights))
       (cons pos)
       (reduce flip lights)))

(defn- all-lights-on [m n]
  (vec (repeat m (vec (repeat n light-on)))))

(defn reset-lights! [lights m n]
  (reset! lights (all-lights-on m n)))

(defn flip-lights! [lights pos]
  (swap! lights (partial flip-neighbors pos)))

(defn all-lights-off? [lights]
  (every? zero? (flatten lights)))
