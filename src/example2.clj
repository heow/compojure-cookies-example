(ns example2
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.params         :only [wrap-params]]))

(defroutes routes
  (POST "/" [name] (str "Thanks " name))
  (GET  "/" [] "<form method='post' action='/'> What's your name? <input type='text' name='name' class='name' maxlength='10' /><input type='submit' name='submit' value='ok' /></form>"))

(def app (-> #'routes wrap-params))

(defn -main []
  (run-jetty app {:port (if (nil? (System/getenv "PORT")) 
                          8000 ; localhost or heroku?
                          (Integer/parseInt (System/getenv "PORT")))}) )
