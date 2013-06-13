(defproject pliant.schema/schema-sql "0.1.0-SNAPSHOT"
  :description "Provides an SQL/RDBMS implementation of the Schema Store."

  :url "https://github.com/pliant/schema"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  
  ;; Keep java source and project definition out of the artifact
  :jar-exclusions [#"^\." #"^*\/\." #"\.java$" #"project\.clj"]
  
  ;; To Run Marginalia us lein marg -d doc/api -m
  :plugins [[lein-marginalia "0.7.1"]]
  :aliases {"doc" ["marg" "-d" "doc/api" "-m"]}

  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/java.jdbc "0.2.3"]])
