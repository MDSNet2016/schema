(ns pliant.schema.common
  "Provides functions that are common to many parts of a schema."
  (:refer-clojure :exclude [transient]))

(defn required
  "Modifies a relation or attribute to be required when creating the entity."
  [relation]
  (assoc relation :required true))


(defn optional
  "Modifies a relation or attribute to be optional when creating the entity."
  [relation]
  (assoc relation :required false))


(defn transient
  "Modifies a relation or attribute to be transient when creating the entity."
  [relation]
  (assoc relation :transient true))


(defn persistable
  "Modifies a relation or attribute to be persisted when creating the entity."
  [relation]
  (assoc relation :transient false))


(defn strict
  "Modifies a schema or entity to have a fidelity level of :strict, meaning 
   all data to be persisted or retrieved will be cleaned up to adhere to 
   the schema."
  [spouse]
  (assoc spouse :fidelity :strict))


(defn strict-in
  "Modifies a schema or entity to have a fidelity level of :strict-in, meaning 
   all data to be persisted will be cleaned up to adhere to the schema, but 
   data being retrieved is returned as-is."
  [spouse]
  (assoc spouse :fidelity :strict-in))


(defn strict-out
  "Modifies a schema or entity to have a fidelity level of :strict-out, meaning 
   all data to be retrieved will be cleaned up to adhere to the schema, but any 
   data being persisted will be persisted."
  [spouse]
  (assoc spouse :fidelity :strict-out))


(defn drunk
  "Modifies a schema or entity to have a fidelity level of :drunk, meaning 
   the rules of structure and validation of the schema will not be applied
   to data being persisted or retrieved."
  [spouse]
  (assoc spouse :fidelity :drunk))


(defn strict?
  "Checks if a schema or entity has a fidelity level of :strict."
  [spouse]
  (= (:fidelity spouse) :strict))


(defn strict-in?
  "Checks if a schema or entity has a fidelity level of :strict-in."
  [spouse]
  (= (:fidelity spouse) :strict-in))


(defn strict-out?
  "Checks if a schema or entity has a fidelity level of :strict-out."
  [spouse]
  (= (:fidelity spouse) :strict-out))


(defn drunk?
  "Checks if a schema or entity has a fidelity level of :drunk."
  [spouse]
  (= (:fidelity spouse) :drunk))
