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
                         {:type :many-to-many :related-to :roles :relationship :has-roles :omit true}]})


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

(def concrete-entities {:org org :person person :address address :states states :roles roles})
(def abstract-entities {:base base :tenant tenant})
(def all-entities (merge abstract-entities concrete-entities))

(def data-org-1 {:id "ORG1" :name "Organization 1" :badfield "baddata"})
(def data-org-2 {:id "ORG2" :name "Organization 2" :alsobadfield "alsobaddata"})

(def data-role-1 {:id "ROLE1" :role "Boss" :name "North Carolina" :org data-org-1})
(def data-role-2 {:id "ROLE2" :role "Slave" :name "Georgia" :org data-org-2})


(def data-state-sc {:id "STSC" :code "SC" :name "South Carolina" :org data-org-1})
(def data-state-nc {:id "STNC" :code "NC" :name "North Carolina" :org data-org-1})
(def data-state-ga {:id "STGA" :code "GA" :name "Georgia" :org data-org-2})
(def data-state-fl {:id "STFL" :code "FL" :name "Florida" :org data-org-2})

(def data-address-sc {:id "ADSC" :address1 "100 Main St" :city "Charleston" :state data-state-sc :org data-org-1})
(def data-address-nc {:id "ADNC" :address1 "100 Main St" :city "Charlotte" :state data-state-nc :org data-org-1})
(def data-address-ga {:id "ADGA" :address1 "100 Main St" :city "Atlanta" :state data-state-ga :org data-org-2})
(def data-address-fl {:id "ADFL" :address1 "100 Main St" :city "Orlando" :state data-state-fl :org data-org-2})

(def data-person-1 {:id "P1" :name "Bubba" :age 32 :dob "2001-01-01" :available true :passed "Nope" :private true
                    :address [data-address-sc data-address-nc] :roles [data-role-1 data-role-2] :org data-org-1
                    :states [data-state-sc data-state-nc]})

(def data-person-2 {:id "P2" :name "JoeBob" :age 23 :dob "2001-01-01" :available true :passed "Nope" :private true
                    :address [data-address-ga data-address-fl] :roles [data-role-1 data-role-2] :org data-org-2
                    :states [data-state-ga data-state-fl]})

(deftest test-check-fidelity
  (let [schema (create-schema :strict all-entities)]
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
  (let [schema (create-schema :strict concrete-entities)]
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
  (let [schema (create-schema :strict all-entities)]
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
  (let [schema (create-schema :strict all-entities)]
    (is (= :strict (fidelity schema)))
    (modify-schema schema common/drunk)
    (is (= :drunk (fidelity schema)))))

(deftest test-entity-modifications
  (let [schema (create-schema :strict all-entities)]
    ;; Add Tests
    (is (= 7 (count (entities schema))))
    (add-entity schema :newentity {})
    (is (= 8 (count (entities schema))))
    (is (thrown? IllegalArgumentException (add-entity schema :address {})))
    ;; Drop Tests
    (drop-entity schema :newentity)
    (is (= 7 (count (entities schema))))
    (drop-entity schema :newentity)
    (is (= 7 (count (entities schema))))
    ;; Modify Tests
    (is (= :strict (fidelity schema :address)))
    (modify-entity schema :address common/drunk)
    (is (= :drunk (fidelity schema :address)))))


(deftest test-attribute-modifications
  (let [schema (create-schema :strict all-entities)]
    ;; Add Tests
    (is (thrown? IllegalArgumentException (add-attribute schema :newentity :newattribute {})))
    (is (thrown? IllegalArgumentException (add-attribute schema :person :name {})))
    (is (= 7 (count (attributes schema :person))))
    (add-attribute schema :person :newattribute {})
    (is (= 8 (count (attributes schema :person))))
    ;; Drop Tests
    (drop-attribute schema :person :newattribute)
    (is (= 7 (count (attributes schema :person))))
    (drop-attribute schema :person :newattribute)
    (is (= 7 (count (attributes schema :person))))
   ;; Modify Tests
    (is (thrown? IllegalArgumentException (modify-attribute schema :newentity :newattribute nil)))
    (is (thrown? IllegalArgumentException (modify-attribute schema :person :newattribute nil)))
    (is (thrown? IllegalArgumentException (modify-attribute schema :person :age nil)))
    (is (not (:required (:age (attributes schema :person)))))
    (modify-attribute schema :person :age common/required)
    (is (:required (:age (attributes schema :person))))
    (is (not (:unique (:age (attributes schema :person)))))
    (modify-attribute schema :person :age #(-> % common/optional attrs/unique))
    (is (not (:required (:age (attributes schema :person)))))
    (is (:unique (:age (attributes schema :person))))))


(deftest test-relation-modifications
  (let [schema (create-schema :strict all-entities)]
    ;; Add Tests
    (is (thrown? IllegalArgumentException (add-relation schema :newentity {:type :belongs-to :related-to :otherentity})))
    (is (thrown? IllegalArgumentException (add-relation schema :person {:type :belongs-to :related-to :address})))
    (is (thrown? IllegalArgumentException (add-relation schema :person {:type :belongs-to :related-to :otherentity :name :address})))
    (is (thrown? IllegalArgumentException (add-relation schema :person {:type :belongs-to :related-to :org}))) ;; found in :extends
    (is (= 4 (count (relations schema :person))))
    (add-relation schema :person {:type :belongs-to :related-to :newrel})
    (is (= 5 (count (relations schema :person))))
    ;; Drop Tests
    (drop-relation schema :person :newrel)
    (is (= 4 (count (relations schema :person))))
    (drop-relation schema :person :newrel)
    (is (= 4 (count (relations schema :person))))
    ;; Modify Tests
    (is (thrown? IllegalArgumentException (modify-relation schema :newentity :otherentity nil)))
    (is (thrown? IllegalArgumentException (modify-relation schema :person :otherentity nil)))
    (is (thrown? IllegalArgumentException (modify-relation schema :person :address nil)))
    (is (not (:required (rels/find-relation (relations schema :person) :address))))
    (modify-relation schema :person :address common/required)
    (is (:required (rels/find-relation (relations schema :person) :address)))
    (is (not (:transient (rels/find-relation (relations schema :person) :address))))
    (modify-relation schema :person :address #(-> % common/optional common/transient))
    (is (not (:required (rels/find-relation (relations schema :person) :address))))
    (is (:transient (rels/find-relation (relations schema :person) :address)))))


#_(def data-person-1 {:id "P1" :name "Bubba" :age 32 :dob "2001-01-01" :available true :passed "Nope" :private true
                    :address [data-address-sc data-address-nc] :roles [data-role-1 data-role-2] :org data-org-1
                    :states [data-state-sc data-state-nc]})

(deftest test-adhere
  (let [schema (create-schema :strict all-entities)
        counts-fn (fn [msg data base org address address0 address1 roles roles0 roles1 states states0 states1]
                    (is (= base (count data)) msg)
                    (is (= org (count (get-in data [:org]))) msg)
                    (is (= address (count (get-in data [:address]))) msg)
                    (is (= address0 (count (get-in data [:address 0]))) msg)
                    (is (= address1 (count (get-in data [:address 1]))) msg)
                    (is (= roles (count (get-in data [:roles]))) msg)
                    (is (= roles0 (count (get-in data [:roles 0]))) msg)
                    (is (= roles1 (count (get-in data [:roles 1]))) msg)
                    (is (= states (count (get-in data [:states]))) msg)
                    (is (= states0 (count (get-in data [:states 0]))) msg)
                    (is (= states1 (count (get-in data [:states 1]))) msg))]
    (is (thrown? IllegalArgumentException (adhere schema :person data-person-1 :wrong-direction)))
    (is (thrown? IllegalArgumentException (adhere! schema :person data-person-1 :wrong-direction)))
    (counts-fn "Testing default data-person-1" data-person-1 11 3 2 5 5 2 4 4 2 4 4)
    
    ; Verify :strict
    (modify-schema schema common/strict)
    (modify-entity schema :person common/divorce)
    (counts-fn "Testing adhere :in :strict" (adhere schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere :out :strict" (adhere schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    (counts-fn "Testing adhere! :in :strict" (adhere! schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere! :out :strict" (adhere! schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    
    ; Verify :strict-in
    (modify-schema schema common/strict-in)
    (counts-fn "Testing adhere :in :strict-in" (adhere schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere :out :strict-in" (adhere schema :person data-person-1 :out) 11 3 2 5 5 2 4 4 2 4 4)
    (counts-fn "Testing adhere! :in :strict-in" (adhere! schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere! :out :strict-in" (adhere! schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    
    ; Verify :strict-out
    (modify-schema schema common/strict-out)
    (counts-fn "Testing adhere :in :strict-out" (adhere schema :person data-person-1 :in) 11 3 2 5 5 2 4 4 2 4 4)
    (counts-fn "Testing adhere :out :strict-out" (adhere schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    (counts-fn "Testing adhere! :in :strict-out" (adhere! schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere! :out :strict-out" (adhere! schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    
    ; Verify :drunk
    (modify-schema schema common/drunk)
    (counts-fn "Testing adhere :in :drunk" (adhere schema :person data-person-1 :in) 11 3 2 5 5 2 4 4 2 4 4)
    (counts-fn "Testing adhere :out :drunk" (adhere schema :person data-person-1 :out) 11 3 2 5 5 2 4 4 2 4 4)
    (counts-fn "Testing adhere! :in :drunk" (adhere! schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere! :out :drunk" (adhere! schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    
    ; Verify :drunk schema :strict person
    (modify-schema schema common/drunk)
    (modify-entity schema :person common/strict)
    (counts-fn "Testing adhere :in :drunk schema :strict person" 
               (adhere schema :person data-person-1 :in) 9 3 2 5 5 2 4 4 0 0 0)
    (counts-fn "Testing adhere :out :drunk schema :strict person" 
               (adhere schema :person data-person-1 :out) 9 3 2 5 5 0 0 0 2 4 4)
    (counts-fn "Testing adhere! :in :drunk schema :strict person" 
               (adhere! schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere! :out :drunk schema :strict person" 
               (adhere! schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)
    
    ; Verify :drunk schema :strict address :strict roles :strict states
    (modify-schema schema common/drunk)
    (modify-entity schema :person common/divorce)
    (modify-entity schema :address common/strict)
    (modify-entity schema :roles common/strict)
    (modify-entity schema :states common/strict)
    (counts-fn "Testing adhere :in :drunk schema :strict address :strict roles :strict states" 
               (adhere schema :person data-person-1 :in) 11 3 2 5 5 2 3 3 2 4 4)
    (counts-fn "Testing adhere :out :drunk schema :strict address :strict roles :strict states" 
               (adhere schema :person data-person-1 :out) 11 3 2 5 5 2 3 3 2 4 4)
    (counts-fn "Testing adhere! :in :drunk schema :strict address :strict roles :strict states" 
               (adhere! schema :person data-person-1 :in) 9 2 2 5 5 2 3 3 0 0 0)
    (counts-fn "Testing adhere! :out :drunk schema :strict address :strict roles :strict states" 
               (adhere! schema :person data-person-1 :out) 9 2 2 5 5 0 0 0 2 4 4)))