(ns clojure-rest.helper
  (:import
   (java.util Calendar)))
(defn get-current-iso-8601-date
  "Returns current ISO 8601 compliant date."
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ssZ") (.getTime (Calendar/getInstance))))

