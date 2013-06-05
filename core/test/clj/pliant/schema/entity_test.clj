(ns pliant.schema.entity-test
  (:refer-clojure :exclude [transient])
  (:use clojure.test
        pliant.schema.core
        pliant.schema.entity
        [pliant.schema.core-test :only [base person address]]))

(deftest test-entity-filters
  (let [schema (create-schema :strict :base base :person person :address address)]
    (is (= 3 (count (entities schema))))
    (is (= 1 (count (only-abstract (entities schema)))))
    (is (= 2 (count (only-concrete (entities schema)))))))