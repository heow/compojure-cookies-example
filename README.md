
Compojure Cookies Example 2011
==============================

Clojure, being a relatively new language, uses an even newer web framework: [Compojure][1].  

Compojure, still sporting a sub 1.0 version, being under active development and reduced to a thin veneer over [Ring][2] may prove challenging for developers.  If for any reason is because many examples and tutorials are simply outdated.

It's my goal to demonstrate the use of sessionless cookies in Compojure, with working examples.

### The Basics ###

[Example 1][3] is a simple hello-world application (suitable for running on [Heroku][4])

```clojure
(ns example1
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET]]))

(defroutes routes
  (GET  "/" [] "Hi there"))

(defn -main []
  (run-jetty routes {:port (if (nil? (System/getenv "PORT")) 
                             8000 ; localhost or heroku?
                             (Integer/parseInt (System/getenv "PORT")))}) )
```



> This is a blockquote.
> 
> This is the second paragraph in the blockquote.
>
> ## This is an H2 in a blockquote

    (def foo "fooberticus")
    
Otherwise `["this" "is" "code"]` har har.



[1]: https://github.com/weavejester/compojure "Compojure"
[2]: http://github.com/mmcgrana/ring          "Ring"
[3]: https://github.com/heow/compojure-cookies-example/blob/master/src/example1.clj "Example 1"
[4]: http://devcenter.heroku.com/articles/clojure "Getting Started with Clojure on Heroku/Cedar"
