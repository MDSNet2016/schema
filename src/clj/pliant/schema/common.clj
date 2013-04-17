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


(defn divorce
  "Removes the fidelity level from a schema or entity.  This really should 
   only be used on entities, as the results of using it on the schema are  
   not guaranteed to be supported."
  [spouse]
  (dissoc spouse :fidelity))

(defn strict?
  "Checks if the provided value has a fidelity level of :strict.  Value can 
   be a map (schema or entity) or the level keyword."
  [spouse-or-level]
  (if (map? spouse-or-level)
    (= (:fidelity spouse-or-level) :strict)
    (= spouse-or-level :strict)))


(defn strict-in?
  "Checks if the provided value has a fidelity level of :strict-in.  Value can 
   be a map (schema or entity) or the level keyword."
  [spouse-or-level]
  (if (map? spouse-or-level)
    (= (:fidelity spouse-or-level) :strict-in)
    (= spouse-or-level :strict-in)))


(defn strict-out?
  "Checks if the provided value has a fidelity level of :strict-out.  Value can 
   be a map (schema or entity) or the level keyword."
  [spouse-or-level]
  (if (map? spouse-or-level)
    (= (:fidelity spouse-or-level) :strict-out)
    (= spouse-or-level :strict-out)))


(defn drunk?
  "Checks if the provided value has a fidelity level of :drunk.  Value can 
   be a map (schema or entity) or the level keyword."
  [spouse-or-level]
  (if (map? spouse-or-level)
    (= (:fidelity spouse-or-level) :drunk)
    (= spouse-or-level :drunk)))


(defn adhere-in?
  "Checks if the provided level indicates strict adherence for data that is to be persisted."
  [spouse-or-level]
  (or (strict? spouse-or-level) (strict-in? spouse-or-level)))


(defn adhere-out?
  "Checks if the provided level indicates strict adherence for data that is being retrieved."
  [spouse-or-level]
  (or (strict? spouse-or-level) (strict-out? spouse-or-level)))
