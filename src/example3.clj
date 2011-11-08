(ns example3
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.cookies        :only [wrap-cookies]]
        [ring.middleware.params         :only [wrap-params]]
        [ring.middleware.keyword-params :only [wrap-keyword-params]]))

(defn main-page [cookies uri]
  (println "URI: " uri)
  (str "Hi there "
       (if (empty? (:value (cookies "name")))
         "<form method='post' action='/'> What's your name? <input type='text' name='name' class='name' maxlength='10' /><input type='submit' name='submit' value='ok' /></form>"
         (:value (cookies "name")))))

(defn process-form [params cookies]
  (let [name (if (not (empty? (:name params)))
               (:name params)
               (:value (cookies "name")))]

    ;; set cookie, return html
    {:cookies {"name" name}
     :body (str "<html><head><meta HTTP-EQUIV='REFRESH' content='5; url='/'\"</head><body>Thanks!</body></html>")}))
  
(defroutes routes
  (POST "/" {params :params cookies :cookies} (process-form params cookies))
  (GET  "/" {cookies :cookies uri :headers}       (main-page cookies uri)))

(def app (-> #'routes wrap-cookies wrap-keyword-params wrap-params))

(defn -main []
  (run-jetty app {:port (if (nil? (System/getenv "PORT")) 
                          8000 ; localhost or heroku?
                          (Integer/parseInt (System/getenv "PORT")))}) )
