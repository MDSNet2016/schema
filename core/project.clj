(defproject pliant.schema/schema-core "0.1.0-SNAPSHOT"
  :description "Provides an extensible abstract schema for data stores."

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

  :dependencies [[org.clojure/clojure "1.3.0"]])
