(ns clojure-rest.handler
  (:require
   [clojure.java.jdbc :as jdbc]
   [compojure.core :refer [defroutes GET POST OPTIONS]]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]])
  (:import
   (java.util Calendar)))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db/data.db"})

(defn get-current-iso-8601-date
  "Returns current ISO 8601 compliant date."
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ssZ") (.getTime (Calendar/getInstance))))

(defn get-all-notes
  "execute query and return lazy sequence"
  []
  (jdbc/query db ["select * from notes"]))

(defn getparameter [req pname] (get (:params req) pname))

(defn insert-note-into-db [req]
  (jdbc/insert! db :notes
                {:title (getparameter req :title)
                 :content (getparameter req :content)
                 :id (java.util.UUID/randomUUID)
                 :createdAt (get-current-iso-8601-date)}))

(defn get-note
  [title]
  (jdbc/query db ["select * from notes where title = ?" title]))

(def cors-headers
  "Generic CORS headers"
  {"Access-Control-Allow-Origin" "*"
   "Access-Control-Allow-Headers" "*"
   "Access-Control-Allow-Methods" "GET"})

(defn preflight?
  "Returns true if the request is a preflight request"
  [request]
  (= (request :request-method) :options))

(defn all-cors
  "Allow requests from all origins - also check preflight"
  [handler]
  (fn [request]
    (if (preflight? request)
      {:status 200
       :headers cors-headers
       :body "preflight complete"}
      (let [response (handler request)]
        (update-in response [:headers]
                   merge cors-headers)))))

(defroutes app-routes
  (OPTIONS "/*" [] preflight?)
  (GET "/" [] "<h1> So Long, and Thanks for All the Fish </h1>")
  (GET "/ping" [] "pong")
  (GET "/notes" [] (get-all-notes))
  (POST "/note" [] insert-note-into-db)
  (GET "/note/:title" [title] (get-note title))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      wrap-params
      wrap-json-body
      wrap-json-response
      wrap-json-params
      wrap-keyword-params
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (all-cors)))

(defn -main [& args]
  (jetty/run-jetty app {:port 4125 :join? true}))
