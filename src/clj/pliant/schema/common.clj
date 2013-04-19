(ns pliant.schema.common
  "Provides functions that are common to many parts of a schema."
  (:refer-clojure :exclude [transient]))

(defn required
  "Modifies a relation or attribute to be required when creating the entity."
  [relation-or-attribute]
  (assoc relation-or-attribute :required true))


(defn optional
  "Modifies a relation or attribute to be optional when creating the entity."
  [relation-or-attribute]
  (assoc relation-or-attribute :required false))


(defn transient
  "Modifies a relation or attribute to be transient when creating the entity."
  [relation-or-attribute]
  (assoc relation-or-attribute :transient true))


(defn persistable
  "Modifies a relation or attribute to be persisted when creating the entity."
  [relation-or-attribute]
  (assoc relation-or-attribute :transient false))


(defn strict
  "Modifies a schema or entity to have a fidelity level of :strict, meaning 
   all data to be persisted or retrieved will be cleaned up to adhere to 
   the schema."
  [schema-or-entity]
  (assoc schema-or-entity :fidelity :strict))


(defn strict-in
  "Modifies a schema or entity to have a fidelity level of :strict-in, meaning 
   all data to be persisted will be cleaned up to adhere to the schema, but 
   data being retrieved is returned as-is."
  [schema-or-entity]
  (assoc schema-or-entity :fidelity :strict-in))


(defn strict-out
  "Modifies a schema or entity to have a fidelity level of :strict-out, meaning 
   all data to be retrieved will be cleaned up to adhere to the schema, but any 
   data being persisted will be persisted."
  [schema-or-entity]
  (assoc schema-or-entity :fidelity :strict-out))


(defn drunk
  "Modifies a schema or entity to have a fidelity level of :drunk, meaning 
   the rules of structure and validation of the schema will not be applied
   to data being persisted or retrieved."
  [schema-or-entity]
  (assoc schema-or-entity :fidelity :drunk))


(defn divorce
  "Removes the fidelity level from a schema or entity.  This really should 
   only be used on entities, as the results of using it on the schema are  
   not guaranteed to be supported."
  [schema-or-entity]
  (dissoc schema-or-entity :fidelity))

(defn strict?
  "Checks if the provided value has a fidelity level of :strict.  Value can 
   be a map (schema or entity) or the level keyword."
  [schema-or-entity-or-level]
  (if (map? schema-or-entity-or-level)
    (= (:fidelity schema-or-entity-or-level) :strict)
    (= schema-or-entity-or-level :strict)))


(defn strict-in?
  "Checks if the provided value has a fidelity level of :strict-in.  Value can 
   be a map (schema or entity) or the level keyword."
  [schema-or-entity-or-level]
  (if (map? schema-or-entity-or-level)
    (= (:fidelity schema-or-entity-or-level) :strict-in)
    (= schema-or-entity-or-level :strict-in)))


(defn strict-out?
  "Checks if the provided value has a fidelity level of :strict-out.  Value can 
   be a map (schema or entity) or the level keyword."
  [schema-or-entity-or-level]
  (if (map? schema-or-entity-or-level)
    (= (:fidelity schema-or-entity-or-level) :strict-out)
    (= schema-or-entity-or-level :strict-out)))


(defn drunk?
  "Checks if the provided value has a fidelity level of :drunk.  Value can 
   be a map (schema or entity) or the level keyword."
  [schema-or-entity-or-level]
  (if (map? schema-or-entity-or-level)
    (= (:fidelity schema-or-entity-or-level) :drunk)
    (= schema-or-entity-or-level :drunk)))


(defn adhere-in?
  "Checks if the provided level indicates strict adherence for data that is to be persisted."
  [schema-or-entity-or-level]
  (or (strict? schema-or-entity-or-level) (strict-in? schema-or-entity-or-level)))


(defn adhere-out?
  "Checks if the provided level indicates strict adherence for data that is being retrieved."
  [schema-or-entity-or-level]
  (or (strict? schema-or-entity-or-level) (strict-out? schema-or-entity-or-level)))


(defn clear-delete
  "Modifies an entity or schema to have the no delete strategy."
  [schema-or-entity]
  (dissoc schema-or-entity :delete-strategy))

(defn default-delete
  "Modifies an entity or schema to have the default delete strategy."
  [schema-or-entity]
  (assoc schema-or-entity :delete-strategy :default))

(defn deactivate-delete
  "Modifies an entity or schema to have a deactivating delete strategy."
  [schema-or-entity]
  (assoc schema-or-entity :delete-strategy :deactivate))

(defn expire-delete
  "Modifies an entity or schema to have an expiring delete strategy."
  [schema-or-entity]
  (assoc schema-or-entity :delete-strategy :expire))


(defn clear-update
  "Modifies an entity or schema to have the no update strategy."
  [schema-or-entity]
  (dissoc schema-or-entity :update-strategy))

(defn default-update
  "Modifies an entity or schema to have the default update strategy."
  [schema-or-entity]
  (assoc schema-or-entity :update-strategy :default))


(defn clear-create
  "Modifies an entity or schema to have the no create strategy."
  [schema-or-entity]
  (dissoc schema-or-entity :create-strategy))

(defn default-create
  "Modifies an entity or schema to have the default create strategy."
  [schema-or-entity]
  (assoc schema-or-entity :create-strategy :default))


(defn clear-retrieve
  "Modifies an entity or schema to have the no retrieve strategy."
  [schema-or-entity]
  (dissoc schema-or-entity :retrieve-strategy))

(defn default-retrieve
  "Modifies an entity or schema to have the default retrieve strategy."
  [schema-or-entity]
  (assoc schema-or-entity :retrieve-strategy :default))

(defn attributes-retrieve
  "Modifies an entity or schema to have the default retrieve strategy."
  [schema-or-entity]
  (assoc schema-or-entity :retrieve-strategy :attributes))

(defn relations-retrieve
  "Modifies an entity or schema to have the default retrieve strategy."
  [schema-or-entity]
  (assoc schema-or-entity :retrieve-strategy :relations))

