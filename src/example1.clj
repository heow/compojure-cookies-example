(ns example1
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core     :only [defroutes GET]]))

(defroutes routes
  (GET  "/" [] "Hi there"))

(defn -main []
  (run-jetty routes {:port (if (nil? (System/getenv "PORT")) 
                             8000 ; localhost or heroku?
                             (Integer/parseInt (System/getenv "PORT")))}) )