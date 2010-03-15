(ns
  #^{:doc "RESTful implementation of the minesweeper game."
     :author "Daniel Werner <daniel.d.werner bei googlemail point com>"}
  restsweeper.app
  (:use [restsweeper.app :only [restsweeper-routes]]
        [compojure :only [run-server servlet]]))

(run-server {:port 8080}
  "/*" (servlet restsweeper-routes))
