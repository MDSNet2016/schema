# Current Schema Structure

This is not finished.....

TODO: write [great documentation](http://jacobian.org/writing/great-documentation/what-to-write/)

```clojure

(def schema {:fidelity (or :strict ;; all data must comply to schema
                           :strict-in ;; all data must comply to schema only when persisted
                           :strict-out ;; only data that complies with the schema is returned when retrieved
                           :drunk ;; all data is persisted and retrieved, whether it complies to the schema or not
                        )
             :entities {
                        :entity-name {
                                      :fidelity (or :strict :strict-in :strict-out :drunk nil) ;; entity specific fidelity that overrides the schema fidelity if present.
                                      :abstract false ;; if true then entity is defined as an abstract class.  No real rules around this yet.
                                      :extends [:entity-name1 :entity-name2] ;; collection of entity names that this entity extends.  applies to attributes and relations currently.
                                      :identity [:attribute-name1] ;; collection of attribute IDs that are treated as the identity.
                                      :attributes {
                                                   :attribute-name {
                                                                    :required false ;; if true a non-nil value is needed to persist
                                                                    :transient false ;; if true the attribute will not be persisted to datasore
                                                                    :unique false ;; if true the value must be unique amongst all values of that attribute for that entity type
                                                                    :omit false ;; if true the attribute value will not be returned in the default retrieve strategy
                                                                    :validation ;; function can validate data prior to being persisted.  TBD
                                                                   }
                                                  }
                                      :relations [
                                                  {
                                                   :type (or :belongs-to ;; means a relates to only one b, but many a's can relate to same b' 'many-to-one'
                                                             :has-many  ;; means a can relate to many b's - 'one-to-many'
                                                             :has-many-through : allows you to relate to the target node through non-direct relationships. Use with :relation-path [c].  If :relation-path is [c], all b's are returned where a relates to c's that relate to those b's.
                                                             :many-to-many ;; means a can relate to many b's, and b can be related to by many a's - 'many-to-many'
                                                          )
                                                   :related-to :node-name  ;; The name of the node this relationship is targetting.
                                                   :relationship :owned-by-or-has-item ;; name given to the relationship within the datastore - important in graph dbs.
                                                   :path [:node-name] ;; If type is :has-many-through, defines the relationship path to take in order to find the nodes of the node type stated in the :related-to attribute.
                                                   :name :alt-to-node-name ;; the value of the map key this relationships values are stored in.  If not present, uses name of target node.
                                                   :transient false ;; Defines whether the data item on the data should be persisted to the database on create. 
                                                   :required true ;; If this relationship value must be defined to save the parent node.  ie - all data must belong to an organization for tenancy
                                                   :strategy {
                                                              :delete :? ;; determine if deletes are cascaded or not.  TBD
                                                              :update :? ;; determine how updated data is handled on the related entity if provided in an update of this entity
                                                              :create :? ;; determine how new data is handled on the related entity if provided in an create of this entity
                                                              :retrieve :? ;; determine what information from this relationship is pulled when this entity is pulled.
                                                             }
                                                  }
                                                ]
                                     }
                       }
            })

```