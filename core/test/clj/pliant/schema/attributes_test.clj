(ns pliant.schema.attributes-test
  (:use clojure.test
        pliant.schema.attributes
        pliant.schema.core
        [pliant.schema.core-test :only [person]]))


(deftest test-attribute-filters
  (let [schema (create-schema :strict :person person)]
    (is (= 6 (count (attributes schema :person))))
    (is (= 1 (count (only-transient (attributes schema :person)))))
    (is (= 5 (count (only-persistable (attributes schema :person)))))
    (is (= 1 (count (only-required (attributes schema :person)))))
    (is (= 5 (count (only-optional (attributes schema :person)))))
    (is (= 1 (count (only-unique (attributes schema :person)))))
    (is (= 5 (count (only-not-unique (attributes schema :person)))))
    (is (= 1 (count (only-omitted (attributes schema :person)))))
    (is (= 5 (count (only-included (attributes schema :person)))))))



(deftest test-attribute-modifiers
  (is (true? (:unique (unique {:unique false}))))
  (is (false? (:unique (non-unique {:unique true}))))
  (is (true? (:omit (omit {:omit false}))))
  (is (false? (:omit (include {:omit true})))))