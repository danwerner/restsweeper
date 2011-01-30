(ns restsweeper.templates
  (:use [restsweeper game hash])
  (:use [clojure.contrib.seq :only [indexed]]
        [compojure.html :only [doctype html link-to xhtml-tag]])
  (:require [clojure.contrib.str-utils2 :as str]))

(def *debug* false)

(def number-color
  {1 "blue"
   2 "green"
   3 "red"
   4 "darkblue"
   5 "darkred"
   6 "cyan"
   7 "magenta"
   8 "black"})

(def difficulty
  ;           w  h  m
  [["Mini"    5  5  5]
   ["Easy"    8  8 10]
   ["Medium" 16 16 40]
   ; FIXME Hash explodes with IndexOutOfBoundsException
   ;["Hard"   30 16 99]
   ])

(defn html-base
  {:arglists '([body] [options body])}
  [& tail]
  (let [options (if (map? (first tail))
                  (first tail)
                  nil)
        body    (if options
                  (rest tail)
                  tail)]
   (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
      [:html
        [:head
          [:title "RESTsweeper"]
          (when (:jquery? options)
            [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js",
                      :type "text/javascript"}])
          [:style "table#board          { border: 3px solid grey; }
                   #board div.uncovered { background-color: white; }
                   #board div.flag      { }
                   #board div.bad-flag  { color: red; }
                   #board div.boom      { background-color: red; }
                   #board div           { font-size: 30pt;
                                          height: 1.25em; width: 1.25em;
                                          text-align: center;
                                          background-color: grey; }
                   #board a             { color: black; text-decoration: none; }
                   #board #message      { font-size: 15pt; font-weight: bold;
                                          text-align: center; }\n"
                  (str/join "\n"
                     (map (fn [[n c]]
                            (format "#board div.number-%d  { color: %s; }" n c))
                          number-color))]]
        "\n"
        [:body nil body]]))))

(defn board-script []
  "$(document).ready(function() {
    $('#board td.cell a').noContext().rightClick(function(e) {
      // Right click will flag the field
      if (e.which === 3) {
        window.location.href = $(this).attr('rel');
        e.preventDefault();
      }
      // Otherwise uncover field
    });
  });
  ")

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

(defn board-page [h w board]
  (let [game-lost  (game-lost? board)
        game-won   (game-won? board)
        game-over  (or game-lost game-won)
        message    (cond game-lost "You lose :-("
                         game-won  "You win :-)")]
    (html-base {:jquery? true}
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

      [:script {:type "text/javascript" :src "/static/jquery.rightClick.js"}]
      [:script {:type "text/javascript"} (board-script)]

      (when *debug*
        [:p "["
          (interpose [:br]
            (for [row board]
              (str (vec row))))
        "]" ])
      )))

(defn main-page []
  (html-base
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
