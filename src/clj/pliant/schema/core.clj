(ns pliant.schema.core
  "Provides the building blocks for creating a Schema."
  (:require [pliant.schema.attributes :as attrs]
            [pliant.schema.common :as common]
            [pliant.schema.entity :as ent]
            [pliant.schema.relations :as rels]))


(defn check-fidelity
  "Ensures that the fidelity level is one of the following:

   + :strict - all data must comply to schema
   + :strict-in - all data must comply to schema only when persisted
   + :strict-out - only data that complies with the schema is returned when retrieved
   + :drunk - all data is persisted and retrieved, whether it complies to the schema or not


   Returns the fidelity value passed if it is an acceptable fidelity level, else :strict is returned."
  [fidelity]
  (if (and (not (nil? fidelity)) (contains? #{:strict :strict-in :strict-out :drunk} fidelity))
    fidelity
    :strict))

(defprotocol ISchema
  "Defines the interface/protocol that makes up a Schema."
  (state [this]
          "Returns the current state of the schema.")
  (fidelity [this]
            [this entity-name]
            "Returns the level of fidelity the schema is requesting to be adhered to. 
             Can provide entity-specific fidelity if the entity name is provided.")
  (entities [this]
            "Returns a sequence of all the available entities.")
  (entity [this entity-name]
          "Returns a specific entity in the schema.")
  (attributes [this entity-name] 
              "Returns a sequence of all of the attributes for a specific entity. Can specify an optional list of attribute names to filter by.")
  (relations [this entity-name] 
             "Returns a sequence of all of the relationships for a specific entity.")
  (typed-relations [this entity-name relation-type] 
                   [this entity-name relation-type target-entity-name] 
                   "Returns a sequence of all the relationships of a specific for a 
                    specific entity, providing an optional target entity to filter on.")
  (typed-relation [this entity-name relation-type target-entity-name] 
                  "Returns the first relationship of a specific type to the target entity for a specific entity.")
  (parent-relations [this entity-name]
                    [this entity-name parent-entity-name] 
                    "Returns a sequence of all the :belongs-to relationships for a
                     specific entity, providing an optional target entity to filter on.")
  (parent-relation [this entity-name parent-entity-name] 
                   "Returns the first :belongs-to relationships to the parent entity for a specific entity.")
  (child-relations [this entity-name] 
                   [this entity-name child-entity-name] 
                   "Returns a sequence of all the :has-many relationships for a specific entity, 
                    providing an optional target entity to filter on.")
  (child-relation [this entity-name child-entity-name] 
                  "Returns the first :has-many relationships to the 
                   child entity for a specific entity.")
  (add-entity [this entity-name properties] 
              "Adds an entity to the schema.  If the entity already exists, an exception is thrown.")
  (add-attribute [this entity-name attribute-name properties] 
                 "Adds an attribute to an entity.  If the attribute already exists, an exception is thrown.")
  (add-relation [this entity-name relation] 
                "Adds a relation to an entity.  If the relation already exists, an exception is thrown.")
  (modify-schema [this modifier-fn]
                 "Provides ability to modify the entire schema structure.  You should not use this unless you 
                  really understand the structure of the schema.")
  (modify-entity [this entity-name modifier-fn] 
                 "Modifies an entity on the schema using the modifier function provided.  If the entity 
                  does not exist an exception will be thrown.")
  (modify-attribute [this entity-name attribute-name modifier-fn] 
                    "Modifies an attribute to an entity using the modifier function provided.  
                    If the attribute does not exist, an exception is thrown.")
  (modify-relation [this entity-name relation-name modifier-fn] 
                   "Modifies a relation on an entity using the modifier function provided.  
                    If the relation does not exist, an exception is thrown.")
  (drop-entity [this entity-name] 
               "Drops an entity on the schema.")
  (drop-attribute [this entity-name attribute-name] 
                  "Removes an attribute from an entity.")
  (drop-relation [this entity-name relation-name] 
                 "Removes a relation from an entity."))

(deftype Schema [schema]
  ISchema
    (state [this] @schema)
    (fidelity [this] 
              (:fidelity @schema))
    (fidelity [this entity-name] 
              (let [s @schema]
                (or (get-in s [:entities entity-name :fidelity]) (:fidelity s))))
    (entities [this] 
              (:entities @schema))
    (entity [this entity-name] 
            ((entities this) entity-name))
    (attributes [this entity-name] 
                (let [entity (entity this entity-name)
                      attrs (:attributes entity)
                      extends (seq (:extends entity))]
                  (if extends
                    (merge (reduce #(merge %1 (attributes this %2)) {} extends) attrs)
                    attrs)))
    (relations [this entity-name] 
               (let [entity (entity this entity-name)
                     relates (:relations entity)
                     extends (seq (:extends entity))]
                 (if extends 
                   (reduce #(rels/merge-relations %1 (relations this %2)) relates extends)
                   relates)))
    (typed-relations [this entity-name relation-type] 
                     (filter #(= (:type %) relation-type) (relations this entity-name)))
    (typed-relations [this entity-name relation-type target-entity-name] 
                     (filter #(= (:related-to %) target-entity-name) (typed-relations this entity-name relation-type)))
    (typed-relation [this entity-name relation-type target-entity-name] 
                    (first (typed-relations this entity-name relation-type target-entity-name)))
    (parent-relations [this entity-name] 
                      (typed-relations this entity-name :belongs-to))
    (parent-relations [this entity-name parent-entity-name] 
                      (typed-relations this entity-name :belongs-to parent-entity-name))
    (parent-relation [this entity-name parent-entity-name] 
                     (typed-relation this entity-name :belongs-to parent-entity-name))
    (child-relations [this entity-name] 
                     (typed-relations this entity-name :has-many))
    (child-relations [this entity-name child-entity-name] 
                     (typed-relations this entity-name :has-many child-entity-name))
    (child-relation [this entity-name child-entity-name] 
                    (typed-relation this entity-name :has-many child-entity-name))
    (add-entity [this entity-name properties] 
              (if (entity this entity-name) 
                (throw (UnsupportedOperationException. (str "Can not add entity '" entity-name " to schema, as entity '"
                                                            entity-name "' already exists.  Use modify-entity function instead.")))
                (swap! schema #(assoc-in % [:entities entity-name] properties))))
    (add-attribute [this entity-name attribute-name properties] 
                   (let [entity (entity this entity-name)]
                     (cond
                       (nil? entity)
                       (throw (UnsupportedOperationException. (str "Unable to add attribute '" attribute-name 
                                                                   "' to entity '" entity-name ", as entity '" entity-name 
                                                                   "' does not exist.  Use add-entity function instead.")))
                       (get-in entity [:attributes attribute-name])
                       (throw (UnsupportedOperationException. (str "Unable to add attribute '" attribute-name 
                                                                   "' to entity '" entity-name ", as attribute '" attribute-name 
                                                                   "' already exists.  Use modify-attribute function instead.")))
                       :else
                       (swap! schema #(assoc-in % [:entities entity-name :attributes attribute-name] properties)))))
    (add-relation [this entity-name relation] 
                  (let [entity (entity this entity-name)
                        relation-name (rels/attribute-name relation)
                        relation-names (rels/attribute-names (:relations entity))
                        all-relation-names (rels/attribute-names (relations this entity-name))]
                    (cond
                       (nil? entity)
                       (throw (UnsupportedOperationException. (str "Unable to add relation with attribute-name '" relation-name 
                                                                   "' to entity '" entity-name ", as entity '" entity-name 
                                                                   "' does not exist.  Use add-entity function instead.")))
                       (some #{relation-name} relation-names)
                       (throw (UnsupportedOperationException. (str "Unable to add relation with attribute-name '" relation-name 
                                                                   "' to entity '" entity-name ", as a relation with attribute-name '" 
                                                                   entity-name "' exists.  Use modify-relation function instead.")))
                       (some #{relation-name} all-relation-names)
                       (throw (UnsupportedOperationException. (str "Unable to add relation with attribute-name '" relation-name 
                                                                   "' to entity '" entity-name ", as a relation with attribute-name '" 
                                                                   entity-name "' exists, either with the entity or on an entity it "
                                                                   "extends.  Use modify-relation on the abstract entity the relation exists on.")))
                       :else
                       (swap! schema #(assoc-in % [:entities entity-name :relations] (conj (:relations entity) relation))))))
    
    (modify-schema [this modifier-fn]
                   (swap! schema modifier-fn))
    (modify-entity [this entity-name modifier-fn]
                  (let [entity (entity this entity-name)]
                    (cond
                       (nil? entity)
                       (throw (UnsupportedOperationException. (str "Unable to modify entity '" entity-name ", as entity '" entity-name 
                                                                   "' does not exist.  Use add-entity function instead.")))
                       :else
                       (swap! schema #(update-in % [:entities entity-name] modifier-fn)))))
    (modify-attribute [this entity-name attribute-name modifier-fn]
                  (let [entity (entity this entity-name)
                        attribute (get-in entity [:attributes attribute-name])]
                    (cond
                       (nil? entity)
                       (throw (UnsupportedOperationException. (str "Unable to modify attribute with attribute-name '" attribute-name 
                                                                   "' to entity '" entity-name ", as entity '" entity-name 
                                                                   "' does not exist.  Use add-entity function instead.")))
                       (nil? attribute)
                       (throw (UnsupportedOperationException. (str "Unable to modify attribute with attribute-name '" attribute-name 
                                                                   "' to entity '" entity-name ", as a attribute with attribute-name '" 
                                                                   attribute-name "' does not exist.  Use add-attribute function instead.")))
                       (not (fn? modifier-fn))
                       (throw (UnsupportedOperationException. (str "Unable to modify attribute with attribute-name '" attribute-name 
                                                                   "' to entity '" entity-name ", as modifier-fn is not a function."
                                                                   "  Stop it.  Just stop it.")))
                       :else
                       (swap! schema #(update-in % [:entities entity-name :attributes attribute-name] modifier-fn)))))
    (modify-relation [this entity-name relation-name modifier-fn]
                  (let [entity (entity this entity-name)
                        index (rels/find-relation-index (:relations entity) relation-name)]
                    (cond
                       (nil? entity)
                       (throw (UnsupportedOperationException. (str "Unable to modify relation with attribute-name '" relation-name 
                                                                   "' to entity '" entity-name ", as entity '" entity-name 
                                                                   "' does not exist.  Use add-entity function instead.")))
                       (nil? index)
                       (throw (UnsupportedOperationException. (str "Unable to modify relation with attribute-name '" relation-name 
                                                                   "' to entity '" entity-name ", as a relation with attribute-name '" 
                                                                   relation-name "' does not exist.  Use add-relation function instead.")))
                       (not (fn? modifier-fn))
                       (throw (UnsupportedOperationException. (str "Unable to modify relation with attribute-name '" relation-name 
                                                                   "' to entity '" entity-name ", as modifier-fn is not a function."
                                                                   "  Stop it.  Just stop it.")))
                       :else
                       (swap! schema #(update-in % [:entities entity-name :relations index] modifier-fn)))))
  (drop-entity [this entity-name] 
               (if (entity this entity-name)
                 (swap! schema #(update-in % [:entities] (fn [m] (dissoc m entity-name))))))
  (drop-attribute [this entity-name attribute-name]
                  (if (get-in (attributes this entity-name) [attribute-name])
                    (swap! schema #(update-in % [:entities entity-name :attributes] (fn [m] (dissoc m attribute-name))))))
  (drop-relation [this entity-name relation-name]
                 (if (rels/find-relation (relations this entity-name) relation-name)
                   (swap! schema #(update-in % [:entities entity-name :relations] (fn [coll] (rels/remove-relation coll relation-name)))))))


(defn create-schema
  "Provides the appropriate parameter checking/typing for creating a new Schema."
  ([] (create-schema :strict {}))
  ([fidelity] (create-schema (or fidelity :strict) {}))
  ([fidelity & entities]
    (cond 
      (nil? (seq entities)) 
        (Schema. (atom {:fidelity (check-fidelity fidelity) :entities {}}))
      (map? (first entities))
        (Schema. (atom {:fidelity (check-fidelity fidelity) :entities (first entities)}))
      :else 
        (Schema. (atom {:fidelity (check-fidelity fidelity) :entities (apply hash-map entities)})))))
