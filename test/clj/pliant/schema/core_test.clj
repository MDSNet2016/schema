(ns pliant.schema.core-test
  (:use clojure.test
        pliant.schema.core)
  (:require [pliant.schema.attributes :as attrs]
            [pliant.schema.common :as common]
            [pliant.schema.relations :as rels]))

(def base {:abstract true
           :identity [:id]
           :attributes {:id {:required true :unique true}}})

(def tenant {:abstract true
             :relations [{:type :belongs-to :related-to :org :required true}]})

  
(def org {:extends [:base]
          :attributes {:name {:required true :unique true}}})

(def person {:fidelity :strict-in
             :extends [:base :tenant]
             :attributes {:name {:required true :unique true}
                          :age {}
                          :dob {}
                          :available {}
                          :passed {:transient true}
                          :private {:omit true}}
             :relations [{:type :has-many :related-to :address :relationship :has-address :cascade #{:create :update :delete}}
                         {:type :has-many-through :related-to :states :path [:address :states] :transient true}
                         {:type :many-to-many :related-to :roles :relationship :has-roles}]})


(def address {:extends [:base :tenant]
              :attributes {:address1 {:required true :unique true}
                           :address2 {}
                           :address3 {}
                           :city {:required true}
                           :zip {:required true}}
              :relations [{:type :belongs-to :related-to :person :deletable-by-parent true :required true}
                          {:type :has-many :related-to :states :relationship :in-state :name :state}]})

(def states {:extends [:base :tenant]
             :attributes {:code {:required true :unique true}
                          :name {:required true}}
             :relations [{:type :belongs-to :related-to :address}]})


(def roles {:extends [:base :tenant]
            :attributes {:role {:required true :unique true}}
            :relations [{:type :many-to-many :related-to :person :relationship :has-persons}]})


(deftest test-check-fidelity
  (let [schema (create-schema :strict :person person :address address :states states :roles roles)]
    (is (= (check-fidelity :strict) :strict))
    (is (= (check-fidelity :strict-in) :strict-in))
    (is (= (check-fidelity :strict-out) :strict-out))
    (is (= (check-fidelity :drunk) :drunk))
    (is (= (check-fidelity :whatever) :strict))
    (is (= (fidelity schema) :strict))
    (is (= (fidelity schema :address) :strict))
    (is (= (fidelity schema :person) :strict-in))))


(deftest test-no-schema-readability
  (let [schema (create-schema)]
    (is (= (fidelity schema) :strict))
    (is (map? (entities schema)))
    (is (nil? (entity schema :person)))
    (is (nil? (attributes schema :person)))
    (is (nil? (relations schema :person)))
    (is (nil? (seq (typed-relations schema :person :has-many-through))))
    (is (nil? (seq (typed-relations schema :person :has-many-through :states))))
    (is (nil? (typed-relation schema :person :has-many-through :states)))
    (is (nil? (seq (parent-relations schema :address))))
    (is (nil? (seq (parent-relations schema :person))))
    (is (nil? (seq (parent-relations schema :address :person))))
    (is (nil? (seq (parent-relations schema :address :roles))))
    (is (nil? (parent-relation schema :address :person)))
    (is (nil? (parent-relation schema :address :roles)))
    (is (nil? (seq (child-relations schema :person))))
    (is (nil? (seq (child-relations schema :states))))
    (is (nil? (seq (child-relations schema :person :address))))
    (is (nil? (seq (child-relations schema :person :address))))
    (is (nil? (child-relation schema :person :states)))
    (is (nil? (child-relation schema :person :states)))))


(deftest test-just-mode-schema-readability
  (let [schema (create-schema :drunk)]
    (is (= (fidelity schema) :drunk))
    (is (map? (entities schema)))
    (is (nil? (entity schema :person)))
    (is (nil? (attributes schema :person)))
    (is (nil? (relations schema :person)))
    (is (nil? (seq (typed-relations schema :person :has-many-through))))
    (is (nil? (seq (typed-relations schema :person :has-many-through :states))))
    (is (nil? (typed-relation schema :person :has-many-through :states)))
    (is (nil? (seq (parent-relations schema :address))))
    (is (nil? (seq (parent-relations schema :person))))
    (is (nil? (seq (parent-relations schema :address :person))))
    (is (nil? (seq (parent-relations schema :address :roles))))
    (is (nil? (parent-relation schema :address :person)))
    (is (nil? (parent-relation schema :address :roles)))
    (is (nil? (seq (child-relations schema :person))))
    (is (nil? (seq (child-relations schema :states))))
    (is (nil? (seq (child-relations schema :person :address))))
    (is (nil? (seq (child-relations schema :person :address))))
    (is (nil? (child-relation schema :person :states)))
    (is (nil? (child-relation schema :person :states)))))


(deftest test-bad-mode-schema-readability
  (let [schema (create-schema :loose)]
    (is (= (fidelity schema) :strict))
    (is (map? (entities schema)))
    (is (nil? (entity schema :person)))
    (is (nil? (attributes schema :person)))
    (is (nil? (relations schema :person)))
    (is (nil? (seq (typed-relations schema :person :has-many-through))))
    (is (nil? (seq (typed-relations schema :person :has-many-through :states))))
    (is (nil? (typed-relation schema :person :has-many-through :states)))
    (is (nil? (seq (parent-relations schema :address))))
    (is (nil? (seq (parent-relations schema :person))))
    (is (nil? (seq (parent-relations schema :address :person))))
    (is (nil? (seq (parent-relations schema :address :roles))))
    (is (nil? (parent-relation schema :address :person)))
    (is (nil? (parent-relation schema :address :roles)))
    (is (nil? (seq (child-relations schema :person))))
    (is (nil? (seq (child-relations schema :states))))
    (is (nil? (seq (child-relations schema :person :address))))
    (is (nil? (seq (child-relations schema :person :address))))
    (is (nil? (child-relation schema :person :states)))
    (is (nil? (child-relation schema :person :states)))))


(deftest test-simple-map-schema-readability
  (let [schema (create-schema :strict {:person person :address address :states states :roles roles})]
    (is (= (fidelity schema) :strict))
    (is (map? (entities schema)))
    (is (map? (entity schema :person)))
    (is (= 6 (count (attributes schema :person))))
    (is (= 3 (count (relations schema :person))))
    (is (= 1 (count (typed-relations schema :person :has-many-through))))
    (is (= 1 (count (typed-relations schema :person :has-many-through :states))))
    (is (= 0 (count (typed-relations schema :person :has-many-through :roles))))
    (is (map? (typed-relation schema :person :has-many-through :states)))
    (is (nil? (typed-relation schema :person :has-many-through :roles)))
    (is (= 1 (count (parent-relations schema :address))))
    (is (= 0 (count (parent-relations schema :person))))
    (is (= 1 (count (parent-relations schema :address :person))))
    (is (= 0 (count (parent-relations schema :address :roles))))
    (is (map? (parent-relation schema :address :person)))
    (is (nil? (parent-relation schema :address :roles)))
    (is (= 1 (count (child-relations schema :person))))
    (is (= 0 (count (child-relations schema :states))))
    (is (= 1 (count (child-relations schema :person :address))))
    (is (= 0 (count (child-relations schema :person :roles))))
    (is (map? (child-relation schema :person :address)))
    (is (nil? (child-relation schema :person :roles)))))


(deftest test-varargs-schema-readability
  (let [schema (create-schema :strict :person person :address address :states states :roles roles)]
    (is (= (fidelity schema) :strict))
    (is (map? (entities schema)))
    (is (map? (entity schema :person)))
    (is (= 6 (count (attributes schema :person))))
    (is (= 3 (count (relations schema :person))))
    (is (= 1 (count (typed-relations schema :person :has-many-through))))
    (is (= 1 (count (typed-relations schema :person :has-many-through :states))))
    (is (= 0 (count (typed-relations schema :person :has-many-through :roles))))
    (is (map? (typed-relation schema :person :has-many-through :states)))
    (is (nil? (typed-relation schema :person :has-many-through :roles)))
    (is (= 1 (count (parent-relations schema :address))))
    (is (= 0 (count (parent-relations schema :person))))
    (is (= 1 (count (parent-relations schema :address :person))))
    (is (= 0 (count (parent-relations schema :address :roles))))
    (is (map? (parent-relation schema :address :person)))
    (is (nil? (parent-relation schema :address :roles)))
    (is (= 1 (count (child-relations schema :person))))
    (is (= 0 (count (child-relations schema :states))))
    (is (= 1 (count (child-relations schema :person :address))))
    (is (= 0 (count (child-relations schema :person :roles))))
    (is (map? (child-relation schema :person :address)))
    (is (nil? (child-relation schema :person :roles)))))


(deftest test-extends-schema-readability
  (let [schema (create-schema :strict :base base :tenant tenant :org org :person person :address address :states states :roles roles)]
    (is (= (fidelity schema) :strict))
    (is (map? (entities schema)))
    (is (map? (entity schema :person)))
    (is (= 7 (count (attributes schema :person))))
    (is (= 4 (count (relations schema :person))))
    (is (= 1 (count (typed-relations schema :person :has-many-through))))
    (is (= 1 (count (typed-relations schema :person :has-many-through :states))))
    (is (= 0 (count (typed-relations schema :person :has-many-through :roles))))
    (is (map? (typed-relation schema :person :has-many-through :states)))
    (is (nil? (typed-relation schema :person :has-many-through :roles)))
    (is (= 2 (count (parent-relations schema :address))))
    (is (= 1 (count (parent-relations schema :person))))
    (is (= 1 (count (parent-relations schema :address :person))))
    (is (= 0 (count (parent-relations schema :address :roles))))
    (is (map? (parent-relation schema :address :person)))
    (is (nil? (parent-relation schema :address :roles)))
    (is (= 1 (count (child-relations schema :person))))
    (is (= 0 (count (child-relations schema :states))))
    (is (= 1 (count (child-relations schema :person :address))))
    (is (= 0 (count (child-relations schema :person :roles))))
    (is (map? (child-relation schema :person :address)))
    (is (nil? (child-relation schema :person :roles)))))

(deftest test-schema-modifications
  (let [schema (create-schema :strict :person person :address address :states states :roles roles)]
    (is (= :strict (fidelity schema)))
    (modify-schema schema common/drunk)
    (is (= :drunk (fidelity schema)))))

(deftest test-entity-modifications
  (let [schema (create-schema :strict :person person :address address :states states :roles roles)]
    ;; Add Tests
    (is (= 4 (count (entities schema))))
    (add-entity schema :newentity {})
    (is (= 5 (count (entities schema))))
    (is (thrown? UnsupportedOperationException (add-entity schema :address {})))
    ;; Modify Tests
    (is (= :strict (fidelity schema :address)))
    (modify-entity schema :address common/drunk)
    (is (= :drunk (fidelity schema :address)))))


(deftest test-attribute-modifications
  (let [schema (create-schema :strict :person person :address address :states states :roles roles)]
    ;; Add Tests
    (is (thrown? UnsupportedOperationException (add-attribute schema :newentity :newattribute {})))
    (is (thrown? UnsupportedOperationException (add-attribute schema :person :name {})))
    (is (= 6 (count (attributes schema :person))))
    (add-attribute schema :person :newattribute {})
    (is (= 7 (count (attributes schema :person))))
    ;; Modify Tests
    (is (thrown? UnsupportedOperationException (modify-attribute schema :newentity :newattribute nil)))
    (is (thrown? UnsupportedOperationException (modify-attribute schema :person :newattribute nil)))
    (is (thrown? UnsupportedOperationException (modify-attribute schema :person :age nil)))
    (is (not (:required (:age (attributes schema :person)))))
    (modify-attribute schema :person :age common/required)
    (is (:required (:age (attributes schema :person))))
    (is (not (:unique (:age (attributes schema :person)))))
    (modify-attribute schema :person :age #(-> % common/optional attrs/unique))
    (is (not (:required (:age (attributes schema :person)))))
    (is (:unique (:age (attributes schema :person))))))


(deftest test-relation-modifications
  (let [schema (create-schema :strict :tenant tenant :person person :address address :states states :roles roles)]
    ;; Add Tests
    (is (thrown? UnsupportedOperationException (add-relation schema :newentity {:type :belongs-to :related-to :otherentity})))
    (is (thrown? UnsupportedOperationException (add-relation schema :person {:type :belongs-to :related-to :address})))
    (is (thrown? UnsupportedOperationException (add-relation schema :person {:type :belongs-to :related-to :otherentity :name :address})))
    (is (thrown? UnsupportedOperationException (add-relation schema :person {:type :belongs-to :related-to :org}))) ;; found in :extends
    (is (= 4 (count (relations schema :person))))
    (add-relation schema :person {:type :belongs-to :related-to :newrel})
    (is (= 5 (count (relations schema :person))))
    ;; Modify Tests
    (is (thrown? UnsupportedOperationException (modify-relation schema :newentity :otherentity nil)))
    (is (thrown? UnsupportedOperationException (modify-relation schema :person :otherentity nil)))
    (is (thrown? UnsupportedOperationException (modify-relation schema :person :address nil)))
    (is (not (:required (rels/find-relation (relations schema :person) :address))))
    (modify-relation schema :person :address common/required)
    (is (:required (rels/find-relation (relations schema :person) :address)))
    (is (not (:transient (rels/find-relation (relations schema :person) :address))))
    (modify-relation schema :person :address #(-> % common/optional common/transient))
    (is (not (:required (rels/find-relation (relations schema :person) :address))))
    (is (:transient (rels/find-relation (relations schema :person) :address)))))

