
Compojure Cookies Example 2011
==============================

Clojure, being a relatively new language, uses an even newer web framework: [Compojure][1].  

Compojure, still sporting a sub 1.0 version, being under active development and reduced to a thin veneer over [Ring][2] may prove challenging for developers.  If for any reason is because many examples and tutorials are simply outdated.

It's my goal to demonstrate the use of sessionless cookies in Compojure, with working examples.

### The Basics ###

[Example 1][3] is a simple hello-world application (suitable for running on [Heroku][4])

```clojure
(ns example1
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core     :only [defroutes GET]]))

(defroutes routes
  (GET  "/" [] "Hi there"))

(defn -main []
  (run-jetty routes {:port (if (nil? (System/getenv "PORT")) 
                             8000 ; localhost or heroku?
                             (Integer/parseInt (System/getenv "PORT")))}) )
```

After you check out the project, it's easy to see in action:

> $ lein run -m example1

### TODO: Everything In Between ###

* routes, really the WHY about routes
* mention use/only
* point out Hiccup
* infinite wisdom of keyword params

### The Final Result ###

[Example 0][0] is the final result, say something witty and descriptive here.

```clojure
(ns example0
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.cookies        :only [wrap-cookies]]
        [ring.middleware.params         :only [wrap-params]]
        [ring.middleware.keyword-params :only [wrap-keyword-params]]))

(defn main-page [cookies]
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
  (GET  "/" {cookies :cookies}                (main-page cookies)))

(def app (-> #'routes wrap-cookies wrap-keyword-params wrap-params))

(defn -main []
  (run-jetty routes {:port (if (nil? (System/getenv "PORT")) 
                             8000 ; localhost or heroku?
                             (Integer/parseInt (System/getenv "PORT")))}) )

```

[0]: https://github.com/heow/compojure-cookies-example/blob/master/src/example0.clj "Example 0"
[1]: https://github.com/weavejester/compojure "Compojure"
[2]: http://github.com/mmcgrana/ring          "Ring"
[3]: https://github.com/heow/compojure-cookies-example/blob/master/src/example1.clj "Example 1"
[4]: http://devcenter.heroku.com/articles/clojure "Getting Started with Clojure on Heroku/Cedar"
