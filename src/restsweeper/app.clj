(ns
  #^{:doc "RESTful implementation of the minesweeper game."
     :author "Daniel Werner <daniel.d.werner at googlemail dot com>"}
  restsweeper.app
  (:use [restsweeper game hash]
        [restsweeper.templates :only [page main-page board-page]]
        [restsweeper.utils :only [vectorize badrequest]]
        [ring.util.response :only [content-type redirect response]]
        [ring.middleware.static :only [wrap-static]]
        [net.cgrand.moustache :only [app]]))

(defn parse-boardsize
  "Takes a string of encoded board dimensions with an optional mine count
   in the format \"<height>x<width>x<mines>\" and returns a vector [h w m].
   If mine count was not given, m is nil. If the boardsize string does not
   match the correct format, returns nil."
  [boardsize]
  (if-let [[_ h w m] (re-matches #"(\d+)x(\d+)(?:x(\d+))?" boardsize)]
    (vec (map #(and % (Integer. %)) [h w m]))))

(defn render [body & [styles scripts]]
  (-> (page body styles scripts)
    response
    (content-type "text/html; charset=utf-8")))

(defn main-menu
  []
  (render (main-page)))

(defn new-game
  "Redirects to a newly-generated board with the given dimensions."
  [[h w m]]
  (if (and h w m)
    (if-let [board (make-board h w m)]
      (redirect (format "/game/%dx%d/%s" h w (hash-board board)))
      (badrequest (format "Too many mines -- use %d or less for this board size")))
    (badrequest "Wrong board size syntax, should be (height)x(width)x(mines)")))

(defn step-board
  [[h w _] hash]
  ; Vectorization is needed for random access
  (let [board   (->> hash
                  (unhash-board h w)  (vectorize)
                  (place-numbers h w) (vectorize))
        lost?   (game-lost? board)
        won?    (game-won? board)
        message (cond lost? "You lose :-("
                      won?  "You win :-)")]
    (render (board-page h w board (or lost? won?) message)
            ["/static/css/game.css"]
            ["https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"
             "/static/jquery.rightClick.js"
             "/static/js/game.js"])))

(def rs-app
  (app :get
    (app
      (wrap-static "resources" #{"/static/"})
      []
        (fn [req] (main-menu))
      ["new" [boardsize parse-boardsize]]
        (fn [req] (new-game boardsize))
      ["game" [boardsize parse-boardsize] hash]
        (fn [req] (step-board boardsize hash))
      [&]
        (fn [req] {:status 404, :body "Not found!"}))))
