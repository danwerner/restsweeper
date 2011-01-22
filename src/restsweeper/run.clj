(ns
  #^{:doc "RESTful implementation of the minesweeper game."
     :author "Daniel Werner <daniel.d.werner bei googlemail point com>"}
  restsweeper.app
  (:gen-class :name restsweeper.Main :main true)
  (:use [restsweeper.app :only [restsweeper-routes]]
        [compojure :only [run-server servlet]]))

(defn run []
  (run-server {:port 8080}
    "/*" (servlet restsweeper-routes)))

(defn -main []
  (.join (run)))
