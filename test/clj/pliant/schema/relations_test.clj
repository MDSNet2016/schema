(ns pliant.schema.relations-test
  (:use clojure.test
        pliant.schema.relations
        pliant.schema.core
        [pliant.schema.core-test :only [person address]]))

(def belongs-to1 {:type :belongs-to :related-to :target :relationship :owned-by})
(def belongs-to2 {:type :belongs-to :related-to :target :name :source :relationship :is-papa})
(def has-many1 {:type :has-many :related-to :target})
(def has-many2 {:type :has-many :related-to :target :name :source})
(def has-many-through1 {:type :has-many-through :related-to :target})
(def has-many-through2 {:type :has-many-through :related-to :target :name :source})
(def many-to-many1 {:type :many-to-many :related-to :target})
(def many-to-many2 {:type :many-to-many :related-to :target :name :source})


(deftest test-cardinality
  (is (= :many (first (cardinality belongs-to1))))
  (is (= :many (first (cardinality belongs-to2))))
  (is (= :one (first (cardinality has-many1))))
  (is (= :one (first (cardinality has-many2))))
  (is (= :one (first (cardinality has-many-through1))))
  (is (= :one (first (cardinality has-many-through2))))
  (is (= :many (first (cardinality many-to-many1))))
  (is (= :many (first (cardinality many-to-many2))))
  (is (= :one (second (cardinality belongs-to1))))
  (is (= :one (second (cardinality belongs-to2))))
  (is (= :many (second (cardinality has-many1))))
  (is (= :many (second (cardinality has-many2))))
  (is (= :many (second (cardinality has-many-through1))))
  (is (= :many (second (cardinality has-many-through2))))
  (is (= :many (second (cardinality many-to-many1))))
  (is (= :many (second (cardinality many-to-many2)))))


(deftest test-attribute-name
  (is (= :target (attribute-name belongs-to1)))
  (is (= :source (attribute-name belongs-to2)))
  (is (= :target (attribute-name has-many1)))
  (is (= :source (attribute-name has-many2)))
  (is (= :target (attribute-name has-many-through1)))
  (is (= :source (attribute-name has-many-through2)))
  (is (= :target (attribute-name many-to-many1)))
  (is (= :source (attribute-name many-to-many2))))


(deftest test-attribute-names
  (let [schema (create-schema :strict :person person :address address)]
    (is (= 3 (count (attribute-names (relations schema :person)))))
    (is (some #{:target} (attribute-names (list belongs-to1 belongs-to2 has-many1))))
    (is (some #{:source} (attribute-names (list belongs-to1 belongs-to2 has-many1))))
    (is (some #{:target} (attribute-names (list belongs-to1 belongs-to2 has-many1))))))


(deftest test-relationship-name
  (is (nil? (relationship-name has-many1)))
  (is (= :owned-by (relationship-name belongs-to1)))
  (is (= :is-papa (relationship-name belongs-to2))))


(deftest test-relations-filters
  (let [schema (create-schema :strict :person person :address address)]
    (is (= 3 (count (relations schema :person))))
    (is (= 1 (count (only-transient (relations schema :person)))))
    (is (= 2 (count (only-persistable (relations schema :person)))))
    (is (= 2 (count (relations schema :address))))
    (is (= 1 (count (only-required (relations schema :address)))))
    (is (= 1 (count (only-optional (relations schema :address)))))))


(deftest test-find-relation
  (let [rels (:relations person)]
    (is (nil? (find-relation rels :not-there)))
    (is (= :has-many (:type (find-relation rels :address))))
    (is (= :has-many-through (:type (find-relation rels :states))))
    (is (= :many-to-many (:type (find-relation rels :roles))))))


(deftest test-find-relation-index
  (let [rels (:relations person)]
    (is (nil? (find-relation-index rels :not-there)))
    (is (= 0 (find-relation-index rels :address)))
    (is (= 1 (find-relation-index rels :states)))
    (is (= 2 (find-relation-index rels :roles)))))

(deftest test-merge-relations
  (is (thrown? UnsupportedOperationException (merge-relations [belongs-to1] [belongs-to1])))
  (is (= 2 (count (merge-relations [belongs-to1] [belongs-to2])))))

(deftest test-remove-relations
  (is (= 1 (count (remove-relation [belongs-to1 belongs-to2] :target)))))


(deftest test-strategy-events
  (is (empty? (on-delete-ignore {:on-delete :not-a-hint})))
  (is (= :cascade (:on-delete (on-delete-cascade {:on-delete :not-a-hint}))))
  (is (empty? (on-update-ignore {:on-update :not-a-hint})))
  (is (= :cascade (:on-update (on-update-cascade {:on-update :not-a-hint}))))
  (is (= :cascade-create (:on-update (on-update-cascade-create {:on-update :not-a-hint}))))
  (is (= :cascade-update (:on-update (on-update-cascade-update {:on-update :not-a-hint}))))
  (is (empty? (on-create-ignore {:on-create :not-a-hint})))
  (is (= :cascade (:on-create (on-create-cascade {:on-create :not-a-hint}))))
  (is (= :cascade-create (:on-create (on-create-cascade-create {:on-create :not-a-hint}))))
  (is (= :cascade-update (:on-create (on-create-cascade-update {:on-create :not-a-hint}))))
  (is (empty? (on-retrieve-ignore {:on-retrieve :not-a-hint}))))

