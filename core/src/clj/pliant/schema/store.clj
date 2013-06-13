(ns pliant.schema.store
  "Defines the functions and protocol for datastore interface engines.")


(defmacro with-connect
  "Provides the entry point for interacting with a datastore, automatically connecting and disconnecting."
  [bindings & body]
  (assert (= 2 (count bindings)) "Requires a DataStore binding")
  `(let [~(first bindings) (connect ~(second bindings))]
     (try
       ~@body
       (finally
         (disconnect ~(first bindings))))))


(defprotocol IEngine
  "Defines the interface/protocol that makes up a Schema."
  (create [this connection entity-name data] 
          [this connection entity-name data instructions]
          "Creates an entity within the target datastore.  If no instructions are provided, 
           the default instructions are used from the schema")
  (lookup [this connection entity-name selector] 
          [this connection entity-name selector instructions]
          "Deletes one or more entities within the target datastore.  If no instructions are provided, 
           the default instructions are used from the schema")
  (update [this connection entity-name data selector] 
          [this connection entity-name data selector instructions]
          "Updates one or more entities within the target datastore.  If no instructions are provided, 
           the default instructions are used from the schema")
  (delete [this connection entity-name selector] 
          [this connection entity-name selector instructions]
          "Deletes one or more entities within the target datastore.  If no instructions are provided, 
           the default instructions are used from the schema"))


(defprotocol IStore
  "Defines the interface/protocol that bridges a Schema with a datastore."
  (startup 
    [this] 
    [this instructions]
    "Tells the IDataStore to connect to the target datastore.")
  
  (shutdown 
    [this] 
    [this instructions]
    "Tells the IDataStore to disconnect from the target datastore.")
  
  (connect
    [this]
    [this instructions]
    "Creates a connection that can be used to interact with the datastore.  It is the responsibility of the caller to close it.")
  
  (disconnect
    [this]
    [this instructions]
    "Closes a connection to the datastore.")
  
  (execute
    [this func]
    [this func connection]
    "Executes the function against the connection.  
     If no connection is provided one should be created and then closed.")
  
  (transact
    [this func]
    [this func connection]
    "Executes the function against the connection within a transaction.  
     If no connection is provided one should be created and then closed."))

(with [ds myDataStore]
  (retrieve ds :entity
     (where true)
     (ordered-by A)))


(with [ds myDataStore]
  (transact ds
    (create ds :entity {})
    (delete ds :entity
      (where ds true)
      (ordered-by ds A))
    (update ds :entity {}
      (where ds true)
      (ordered-by ds A))))

