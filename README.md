# About

Schema is a small Clojure library that provides a generic data schema structure that can be used to abstract:

* Retrieving of data from datastore
* Persiting of data from datastore
* Adherence of data to schema structure at both the schema and entity level.
* Schema inheritance
* Validation of data values.
* Value conversion between applicaiton and datastore states.

Schema is intended to be extensible so that plugin-based application can add entities, attributes, and relationships to an existing schema to support the plugin operations.

## Usage

Schema is currently an alpha project without functional hooks into a bridge to a datastore.  As the library becomes more functional the documentation will be updated.  The priority of providing bridge implementations to actually datastore endpoints is as follows:

* Graph DBs (initial target is neo4j)
* RDBMS (initial target is )

Copyright © 2012-2013

Distributed under the Eclipse Public License.
