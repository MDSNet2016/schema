(ns pliant.schema.entity
  "Provides functions for working with entities."
  (:refer-clojure :exclude [transient]))

(defn only-abstract
  "Filters out non-abstract entities."
  [entities]
  (and entities (filter (fn [[name prop]] (true? (:abstract prop))) entities)))


(defn only-concrete
  "Filters out abstract entities."
  [entities]
  (and entities (filter (fn [[name prop]] (not (true? (:abstract prop)))) entities)))

