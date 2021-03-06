(ns pliant.schema.relations
  "Provides functions for working with entity relations.")


(defn cardinality
  "Determines whether the relationship can have 1 or many target entities that it refers to."
  [relation]
  ((:type relation)
     {:belongs-to [:many :one]
      :has-many [:one :many]
      :has-many-through [:one :many]
      :many-to-many [:many :many]}))


(defmulti attribute-name
  "Returns the name of the attribute the data of the relationship is stored in.  By 
   default, if a value is provided in the :name property that value is returned,
   else the :related-to value is returned.
   
   To modify how the attribute-name function provides the relevant attribute name, 
   you can add a defmethod that uses a dispatch value based on the type of 
   relationship you are getting the name for."
   :type)

(defmethod attribute-name :default
  [{:keys [related-to name]}]
  (or name (if (keyword? related-to)
             related-to 
             (keyword related-to))))


(defn attribute-names
  "Gets the sequence of names from the provided relations."
  [relations]
  (map #(attribute-name %) relations))

(defn find-relation
  "Finds a relation that is configured to have a specific attribute-name."
  [relations name]
  (first (filter #(= name (attribute-name %)) relations)))

(defn find-relation-index
  "Finds a relation that is configured to have a specific attribute-name."
  [relations name]
  (loop [rels relations
         index 0]
    (cond
      (nil? (seq rels)) nil
      (= name (attribute-name (first rels))) index
      :else
      (recur (next rels) (inc index)))))

(defn merge-relations
  "Merges two collections of relations together."
  [rels1 rels2]
  (cond 
    (nil? (seq rels1)) rels2
    (nil? (seq rels2)) rels1
    :else
    (reduce (fn [coll rel] 
              (if (find-relation coll (attribute-name rel))
                (throw (UnsupportedOperationException. "Can not merge collections of relations where there is overlap in the relation attribute name."))
                (conj coll rel))) rels1 rels2)))


(defn remove-relation
  "Removes a relation from a relation collection."
  [relations name]
  (into [] (filter #(not= name (attribute-name %)) relations)))


(defn relationship-name
  "Gets the name that identifies the relationship within the datastore.  Need for graph dbs to name the edges."
  [relation]
  (:relationship relation))


(defn only-transient
  "Filters out any entity relations that are not transient."
  [relations]
  (and relations (filter (fn [relation] (true? (:transient relation))) relations)))


(defn only-persistable
  "Filters out any entity relations that are not persistable to the datastore."
  [relations]
  (if relations (filter (fn [relation] (not (true? (:transient relation)))) relations)))


(defn only-required
  "Filters out any entity relations that are not required to be persisted to the datastore."
  [relations]
  (and relations (filter (fn [relation] (true? (:required relation))) relations)))


(defn only-optional
  "Filters out any entity relations that are required to be persisted to the datastore."
  [relations]
  (if relations (filter (fn [relation] (not (true? (:required relation)))) relations)))

; Need to revisit how omitted is determined
(defn only-omitted
  "Filters out any entity relations that are not omitted from response structures."
  [relations]
  (if relations (filter (fn [relation] (not (true? (:omit relation)))) relations)))


(defn only-included
  "Filters out any entity relations that are omitted from response structures."
  [relations]
  (if relations (filter (fn [relation] (not (true? (:omit relation)))) relations)))


(defn belongs-to
  "Modifies a relation to be a :belongs-to type."
  [relation]
  (assoc relation :type :belongs-to))


(defn has-many
  "Modifies a relation to be a :has-many type."
  [relation]
  (assoc relation :type :has-many))


(defn has-many-through
  "Modifies a relation to be a :has-many-through type."
  [relation]
  (assoc relation :type :has-many-through))


(defn many-to-many
  "Modifies a relation to be a :many-to-many type."
  [relation]
  (assoc relation :type :many-to-many))

(defn on-delete-cascade
  "Modifies a relation to indicate that relative targets should 
   be deleted when the parent entity is deleted."
  [relation]
  (assoc relation :on-delete :cascade))

(defn on-delete-ignore
  "Modifies a relation to that nothing should be done to entities 
   associated through this relation when this entity is deleted."
  [relation]
  (dissoc relation :on-delete))

(defn on-update-cascade
  "Modifies a relation to indicate that relative targets should be
   updated or created accordingly when the parent entity is updated 
   and the data being updated has reference data to the relation. 
   Generally, if the reference data has no values that represent its
   IDENTITY, it would be considered a candidate for creation.  If it 
   does have IDENTITY values, it is a candidate for update."
  [relation]
  (assoc relation :on-update :cascade))

(defn on-update-cascade-create
  "Modifies a relation to indicate that relative targets should be
   created accordingly when the parent entity is updated 
   and the data being updated has reference data to the relation. 
   Generally, if the reference data has no values that represent its
   IDENTITY, it would be considered a candidate for creation."
  [relation]
  (assoc relation :on-update :cascade-create))

(defn on-update-cascade-update
  "Modifies a relation to indicate that relative targets should be
   updated accordingly when the parent entity is updated 
   and the data being updated has reference data to the relation. 
   Generally, if the reference data has IDENTITY values, it is a 
   candidate for update."
  [relation]
  (assoc relation :on-update :cascade-update))

(defn on-update-ignore
  "Modifies a relation to that nothing should be done to entities 
   associated through this relation when this entity is updated."
  [relation]
  (dissoc relation :on-update))

(defn on-create-cascade
  "Modifies a relation to indicate that relative targets should be
   updated or created accordingly when the parent entity is created 
   and the data being updated has reference data to the relation. 
   Generally, if the reference data has no values that represent its
   IDENTITY, it would be considered a candidate for creation.  If it 
   does have IDENTITY values, it is a candidate for update."
  [relation]
  (assoc relation :on-create :cascade))

(defn on-create-cascade-create
  "Modifies a relation to indicate that relative targets should be
   created accordingly when the parent entity is created 
   and the data being updated has reference data to the relation. 
   Generally, if the reference data has no values that represent its
   IDENTITY, it would be considered a candidate for creation."
  [relation]
  (assoc relation :on-create :cascade-create))

(defn on-create-cascade-update
  "Modifies a relation to indicate that relative targets should be
   updated accordingly when the parent entity is created 
   and the data being updated has reference data to the relation. 
   Generally, if the reference data has IDENTITY values, it is a 
   candidate for update."
  [relation]
  (assoc relation :on-create :cascade-update))

(defn on-create-ignore
  "Modifies a relation to that nothing should be done to entities 
   associated through this relation when this entity is created."
  [relation]
  (dissoc relation :on-create))

(defn on-retrieve-ignore
  "Modifies a relation to that nothing should be done to entities 
   associated through this relation when this entity is deleted."
  [relation]
  (dissoc relation :on-retrieve))

