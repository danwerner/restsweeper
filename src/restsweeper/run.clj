(ns
  #^{:doc "RESTful implementation of the minesweeper game."
     :author "Daniel Werner <daniel.d.werner at googlemail dot com>"}
  restsweeper.run
  (:gen-class :name restsweeper.Main :main true)
  (:use [restsweeper.app :only [rs-app]]
        [ring.adapter.jetty :only [run-jetty]]))

(defonce *server* nil)

(defn run []
  (if *server*
    (.stop *server*))
  (alter-var-root #'*server*
    (fn [_]
      (run-jetty #'rs-app {:port 8080, :join? false}))))

(defn -main []
  (.join (run)))
