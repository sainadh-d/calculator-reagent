(ns calculator-reagent.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state (atom {:display ""}))

(defn backspace [value]
  "Emulates backspace i.e, changes xxx -> xx"
  (subs value 0 (dec (count value))))

(defn evaluate [expression]
  (try
    (str (js/eval expression))
    (catch js/Error e
      "Error")))

(defn get-updated-expression [expression value]
  (case value
    "C"  "" ;; reset the expression to empty string
    "←"  (backspace expression)
    "="  (evaluate expression)
    ;; default case
    (if (= expression "Error")
      value
      (str expression value))))

(defn button-click-handler [event]
  "Callback function which takes the button-click event and updates the display expression"
  ;; (.. event -target -value) is same as (-> event .-target .-value) is same as (.-value (.-target event)
  ;; JS obj properties are accessed using .-property
  (swap! app-state update :display get-updated-expression (.. event -target -value)))

;; Components
(defn key-row [elements]
 (conj
   (for [element elements]
     [:button {:name element
               :value element
               :on-click #(button-click-handler %)}
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
