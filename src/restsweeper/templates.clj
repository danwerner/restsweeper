(ns restsweeper.templates
  (:use [restsweeper game hash])
  (:use [clojure.contrib.seq-utils :only [indexed]]
        [compojure.html])
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

(defn html-base [& body]
  (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
      [:html
        [:head
          [:title "RESTsweeper"]
          [:style "table#board          { border: 3px solid grey; }
                   #board div.uncovered { background-color: white; }
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
        [:body nil body]])))

(defn cell-format [cell]
  "Returns a vector [css-class content] to visually represent a cell."
  (cond
    (flag? cell)
      [nil "⚑"]
    (and (uncovered? cell) (mine? cell))
      ["boom" "☢"]
    (and (uncovered? cell) (numbered? cell))
      [(str "uncovered number-" (:number cell)) (:number cell)]
    (uncovered? cell)
      ["uncovered" nil]
    :else
      ["" nil]))

(defn board-page [h w board]
  (let [game-lost  (game-lost? board)
        game-won   (game-won? board)
        message    (cond game-lost "You lose :-("
                         game-won  "You win :-)")]
    (html-base
      [:table#board
        (for [[y row] (indexed board)]
          [:tr
            (for [[x cell] (indexed row)]
              (let [[class content] (cell-format cell)
                     hash  (hash-board (uncover y x h w board))
                     url   (format "/game/%dx%d/%s" h w hash)
                     div   [:div {:class class} content]]
                [:td
                  (if (and (can-interact? cell) (not game-lost) (not game-won))
                    [:a {:href url} div]
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
