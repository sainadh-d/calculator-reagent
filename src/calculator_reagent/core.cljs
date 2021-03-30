(ns calculator-reagent.core
  (:require [reagent.core :as reagent :refer [atom]]))

;; TODO: Add documentation for functions
;; TODO: result word is used too many times, use something else
;; TODO: Change styling of the calculator
;; TODO: Update readme and include https://github.com/niinpatel/calculator-react as inspiration
;; TODO: Push to github

(defonce app-state (atom {:display ""}))

(defn backspace [value]
  "Emulates backspace i.e, changes xxx -> xx"
  (subs value 0 (dec (count value))))

(defn calculate [expression]
  (try
    (str (js/eval expression))
    (catch js/Error e
      "Error")))

(defn update-expression [expression value]
  (if (= expression "Error")
    value
    (str expression value)))

(defn update-display [state value]
  (case value
    "C" (assoc state :display "") ;; reset the expression tab
    "←"  (update state :display backspace)
    "="  (update state :display calculate)
    (update state :display update-expression value)))

;; Components
(defn key-row [elements]
 (conj
   (for [element elements]
     [:button {:name element
               :value element
               :on-click #(swap! app-state update-display (-> % .-target .-value))}
       element])
   [:br]))

(defn keypad-component []
  (let [key-groups [["(" ")" "C" "←"]
                    ["1" "2" "3" "+"]
                    ["4" "5" "6" "-"]
                    ["7" "8" "9" "*"]
                    ["." "0" "=" "/"]]]
   [:div.keypad
     (for [key-group key-groups]
       (key-row key-group))]))

(defn display-component [value]
  [:div.display
   [:p value]])

(defn simple-calculator []
  [:div
   [:div.calculator-body
    [:h1 "Simple Calculator"]
    [display-component (:display @app-state)]
    [keypad-component]]])

(defn start []
  (reagent/render-component [simple-calculator]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
