
Compojure Cookies Example 2011
==============================

![cookie](http://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Cookie.gif/120px-Cookie.gif "")

Clojure, being a relatively new language, uses an even newer web framework: [Compojure][1].  

Compojure, still sporting a sub 1.0 version, being under active development and reduced to a thin veneer over [Ring][2] may prove challenging for developers.  If for any reason because many examples and tutorials are just outdated.

I'm going to demonstrate the use of sessionless cookies in Compojure, with working examples.

### The Bare Essentials ###

[Example 1][3], a simple hello-world application is suitable for running on [Heroku][4].

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

After you check out the project from [GitHub][5], it's easy to see in action:

    $ lein run -m example1
    
### Middleware: A Morality Tale in I Act ###

[Example 2][6], the addition of a very simple form requires some changes.

```clojure
(ns example2
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.params         :only [wrap-params]]))

(defroutes routes
  (POST "/" [name] (str "Thanks " name))
  (GET  "/" [] "<form method='post' action='/'> What's your name? <input type='text' name='name' /><input type='submit' /></form>"))

(def app (wrap-params routes))

(defn -main []
  (run-jetty app {:port (if (nil? (System/getenv "PORT")) 
                          8000 ; localhost or heroku?
                          (Integer/parseInt (System/getenv "PORT")))}) )
```

The new POST route uses the `name` variable from the form.  This is possible because we're now leveraging middleware:

    (def app (wrap-params routes))

Simply put: __middleware is features__.  Rather than forcing you into a one-size-fits all model, it's a way to mix and match whichever features you need.

In this case, we have to process form variables.  `wrap-params` is what does this for us by making the form variable `name` available as a local.

You can also look at from an efficiency point of view: *ALL* we're doing is accessing the form parameters.  We aren't using sessions or a plethora of other features that we may or may not want.

### C is for Clojure ###

[Example3][7], cookie stuffing without sessions.

```clojure
(ns example3
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.cookies        :only [wrap-cookies]]
        [ring.middleware.params         :only [wrap-params]]
        [ring.middleware.keyword-params :only [wrap-keyword-params]]))

(defn main-page [cookies]
  (str "Hi there "
       (if (empty? (:value (cookies "name")))
         "<form method='post' action='/'> What's your name? <input type='text' name='name' /><input type='submit' /></form>"
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
  (run-jetty app {:port (if (nil? (System/getenv "PORT")) 
                          8000 ; localhost or heroku?
                          (Integer/parseInt (System/getenv "PORT")))}) )
```

#### keyword params ####

Starting from the bottom, we're now wrapping cookies and keyword params using [threading][8]:

    (def app (-> #'routes wrap-cookies wrap-keyword-params wrap-params))

Keyword params changes the input parameters from string-based to keyword based.  It's string based because form variables technically can contain spaces, however I've yet to see it in real life.

    {"name" "Hello world"} ; params without keyword-params
    {:name  "Hello world"} ; params with keyword-params
    
#### destructuring syntax ####    
    
The routes have a [startling new syntax][9]:

    (POST "/" {params :params cookies :cookies} (process-form params cookies))

It's because now that we want to manipulate cookies, we're a operating at slightly lower level which allows us to pass in the cookies so they can be read.

#### the return value ####

    {:cookies {"name" name}
     :body (str "<html><head> ...

Because we want to __modify__ the cookies, we need to return something more than a string.  By default returning a string assumes the `:body`, you can [set other various things][10] like headers etc.





[1]: https://github.com/weavejester/compojure "Compojure"
[2]: http://github.com/mmcgrana/ring          "Ring"
[3]: https://github.com/heow/compojure-cookies-example/blob/master/src/example1.clj "Example 1"
[4]: http://devcenter.heroku.com/articles/clojure "Getting Started with Clojure on Heroku/Cedar"
[5]: https://github.com/heow/compojure-cookies-example "GitHub Project"
[6]: https://github.com/heow/compojure-cookies-example/blob/master/src/example2.clj "Example 2"
[7]: https://github.com/heow/compojure-cookies-example/blob/master/src/example3.clj "Examlpe 3"
[8]: http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/-%3E "Threading"
[9]: https://github.com/weavejester/compojure/wiki/Destructuring-Syntax
[10]: https://github.com/mmcgrana/ring/wiki/Creating-responses
