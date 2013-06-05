(ns pliant.schema.attributes
  "Provides functions for working with an entity's attributes map and individual 
   attribute properties.")

(defn only-transient
  "Filters out any entity attributes that are not transient."
  [attrs]
  (and attrs (into {} (filter (fn [[key props]] (true? (:transient props))) attrs))))

(defn only-persistable
  "Filters out any entity attributes that are not persistable to the datastore."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (not (true? (:transient props)))) attrs))))

(defn only-required
  "Filters out any entity attributes that are not required."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (true? (:required props))) attrs))))

(defn only-optional
  "Filters out any entity attributes that are required."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (not (true? (:required props)))) attrs))))

(defn only-unique
  "Filters out any entity attributes that are not unique."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (true? (:unique props))) attrs))))

(defn only-not-unique
  "Filters out any entity attributes that are unique."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (not (true? (:unique props)))) attrs))))

(defn only-omitted
  "Filters out any entity attributes that are not omitted from response structures."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (true? (:omit props))) attrs))))

(defn only-included
  "Filters out any entity attributes that are omitted from response structures."
  [attrs]
  (if attrs (into {} (filter (fn [[key props]] (not (true? (:omit props)))) attrs))))


(defn unique
  "Modifies an attribute to allow only unique values when creating the entity."
  [relation]
  (assoc relation :unique true))

(defn non-unique
  "Modifies an attribute to allow repetative values when creating the entity."
  [relation]
  (assoc relation :unique false))

(defn omit
  "Modifies an attribute to discard it when retrieving entity data."
  [relation]
  (assoc relation :omit true))

(defn include
  "Modifies an attribute to include it when retrieving entity data."
  [relation]
  (assoc relation :omit false))
