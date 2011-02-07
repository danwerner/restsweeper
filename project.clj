(defproject restsweeper "0.5.0"
  :description "RESTful implementation of the Minesweeper game"
  :main restsweeper.run
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [enlive "1.0.0-SNAPSHOT"]
                 [ring/ring-jetty-adapter "0.3.5"]
                 [ring/ring-core "0.3.5"]
                 [compojure "0.3.2"]]
  :dev-dependencies [[vimclojure/server "2.2.0"]])
