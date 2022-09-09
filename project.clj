(defproject clojure-rest "0.1.0-SNAPSHOT"
  :description "REST API for saving notes in a SQLITE table"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.6.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.xerial/sqlite-jdbc "3.20.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors/ring-cors "0.1.13"]
                 [ring "1.9.5"]
                 [ring/ring-json "0.5.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [java-jdbc/dsl "0.1.3"]]

  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler clojure-rest.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
