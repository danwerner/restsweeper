(ns restsweeper.templates
  (:use [restsweeper game hash]
        [clojure.contrib.seq :only [indexed]]
        [compojure.html :only [doctype html include-css include-js link-to
                               xhtml-tag]]
        [net.cgrand.enlive-html]
        [ring.util.response :only [response redirect]])
  (:require [clojure.string :as str]))

(def *debug* false)

(def difficulty
  ;           w  h  m
  [["Mini"    5  5  5]
   ["Easy"    8  8 10]
   ["Medium" 16 16 40]
   ; FIXME Hash explodes with IndexOutOfBoundsException
   ;["Hard"   30 16 99]
   ])

(defn html-base
  [headers & body]
  (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
      [:html
        [:head
          [:title "RESTsweeper"]
           (apply concat headers)]
        "\n"
        [:body nil body]])))

(defn cell-format [cell game-over?]
  "Returns a vector [css-class content] to visually represent a cell."
  (cond
    (and (flag? cell) (not (mine? cell)) game-over?)
      ["bad-flag" "⚐"]
    (flag? cell)
      ["flag" "⚑"]
    (and (mine? cell) (uncovered? cell))
      ["boom" "☢"]
    (and (mine? cell) game-over?)
      [nil "☢"]
    (and (uncovered? cell) (numbered? cell))
      [(str "uncovered number-" (:number cell)) (:number cell)]
    (uncovered? cell)
      ["uncovered" nil]
    :else
      ["" nil]))

(defn action-url [action-fn y x h w board]
  (->> (action-fn y x h w board)
    (hash-board)
    (format "/game/%dx%d/%s" h w)))

(defsnippet board-page "templates/game.html" [:body :> any-node]
  [h w board gameover? message]
  [[:tr first-of-type]]
    (clone-for [[y row] (indexed board)]
      [:td.cell]
        (clone-for [[x cell] (indexed row)]
          (let [[divclass cnt] (cell-format cell gameover?)]
            (transformation
              [:div]
                (do->
                  (add-class divclass)
                  (content cnt)
                  (if-not (or gameover? (uncovered? cell))
                    ; Left click -> href, right click -> rel
                    (let [uncover-url (if-not (flag? cell)
                                        (action-url uncover y x h w board))
                          flag-url    (action-url flag y x h w board)]
                      (wrap :a {:href uncover-url
                                :rel flag-url}))
                    identity))))))
  [:td#message]
    (do->
      (set-attr :colspan w)
      (content (or message "")))
  [:p.debug]
    (when *debug*
      (html-content "[" (str/join "<br>" (map (comp str vec) board)) "]")))

(defn board-page* [h w board]
  (let [game-lost  (game-lost? board)
        game-won   (game-won? board)
        game-over  (or game-lost game-won)
        message    (cond game-lost "You lose :-("
                         game-won  "You win :-)")]
    (html-base
      [(include-css "/static/css/game.css")
       (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"
                   "/static/js/jquery.rightClick.js"
                   "/static/js/game.js")]
      [:table#board
        (for [[y row] (indexed board)]
          [:tr
            (for [[x cell] (indexed row)]
              (let [[class content] (cell-format cell game-over)
                    div             [:div {:class class} content]]
                [:td.cell
                  (if-not (or game-over (uncovered? cell))
                    (let [uncover-url     (if-not (flag? cell)
                                            (action-url uncover y x h w board))
                          flag-url        (action-url flag y x h w board)]
                      ; Left click -> href, right click -> rel
                      [:a {:href uncover-url :rel flag-url}
                        div])
                    div)]))])
          [:tr [:td#message {:colspan w} (or message "&nbsp;")]]]

      [:p (link-to "/" "&laquo; Menu")]

      (when *debug*
        [:p "["
          (interpose [:br]
            (for [row board]
              (str (vec row))))
        "]" ])
      )))

(deftemplate page "templates/base.html" [body styles scripts]
  [[:link (attr= :rel "stylesheet")]]
    (clone-for [style styles]
      (set-attr :href style))
  [:script.import]  (clone-for [script scripts]
                      (set-attr :src script))
  [:body]           (content body))

(defsnippet main-page "templates/main.html" [:body :> any-node] []
  [:ul#newgame :li]
  (clone-for [[name w h m] difficulty]
    [:a] (do->
           (set-attr :href (format "/new/%dx%dx%d" w h m))
           (content (format "%s - %dx%d with %d mines"
                            name w h m)))))

(defn main-page* []
  (html-base
    nil
    [:h1 "RESTsweeper"]
    [:h2 "New game"]
    [:ul
      (for [[name w h m] difficulty]
        [:li (link-to (format "/new/%dx%dx%d" w h m)
                      (format "%s - %dx%d with %d mines" name w h m))])]
    [:h2 "Resources"]
    [:ul
      [:li (link-to "http://en.wikipedia.org/wiki/Minesweeper_(video_game)" "Read Manual")]
      [:li (link-to "http://github.com/danwerner/restsweeper" "Download Source")]]))


