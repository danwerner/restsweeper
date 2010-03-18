(ns
  #^{:doc "RESTful implementation of the minesweeper game."
     :author "Daniel Werner <daniel.d.werner bei googlemail point com>"}
  restsweeper.app
  (:use [restsweeper game hash templates]
        [restsweeper.utils :only [vectorize]]
        [compojure]))

(defn parse-boardsize
  "Takes a string of encoded board dimensions with an optional mine count
   in the format \"<height>x<width>x<mines>\" and returns a vector [h w m].
   If mine count was not given, m is nil. If the boardsize string does not
   match the correct format, returns nil."
  [boardsize]
  (if-let [[_ h w m] (re-matches #"(\d+)x(\d+)(?:x(\d+))?" boardsize)]
    (vec (map #(and % (Integer. %)) [h w m]))))

(defn new-game
  "Redirects to a newly-generated board with the given dimensions."
  [boardsize]
  (let [[h w m] (parse-boardsize boardsize)]
    (if (and h w m)
      (try
        (redirect-to (format "/game/%dx%d/%s" h w
                             (hash-board (make-board h w m))))
        (catch AssertionError e
          [400 (format "Too many mines -- use %d or less for this board size"
                       (max-mines h w))]))
      [400 "Wrong board size syntax, should be (height)x(width)x(mines)"])))

(defn step-board [boardsize hash]
  (if-let [[h w _] (parse-boardsize boardsize)]
    ; Vectorization is needed for random access
    (let [board (->> hash
                  (unhash-board h w)  (vectorize)
                  (place-numbers h w) (vectorize))]
      (board-page h w board))
    [400 "Wrong board size syntax, should be (height)x(width)"]))

(defroutes restsweeper-routes
 (GET "/"
    (main-page))
 (GET "/new/:board-size"
    (new-game (params :board-size)))
 (GET "/game/:board-size/:hash"
    (step-board (params :board-size) (params :hash)))
 (POST "*"
    [405 "Only GET is allowed" {:headers {"Allow" "GET"}}])
 (ANY "*"
    [404 "Not Found"]))
