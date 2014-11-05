(ns zelene-recepty.view
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(defn- render-with-delimiter [delimiter padding max-items coll]
  (if (> (count coll) max-items)
    (str (string/join delimiter (take max-items coll)) padding)
    (string/join delimiter coll)))

(def ^:private render-list (partial render-with-delimiter "," " ..."))

(html/defsnippet menu-bar "index.html" [:ul#menu [:li (html/nth-of-type 1)]]
  [title link]
  [:li :a] (html/do->
            (html/set-attr :href link)
            (html/content title)))

(html/defsnippet first-letter "listings.html" [:div#ingredients-list [:ul (html/nth-of-type 1)] [:li (html/nth-of-type 1)]]
  [letter]
  [:li] (html/content letter))

(html/defsnippet grouped-list "listings.html" [:div#ingredients-list [:ul (html/nth-of-type 1)]]
  [list-snippet [letter items]]
  [:ul] (html/content
         (cons (first-letter letter)
               (map list-snippet items))))

(html/defsnippet ingredient-list-item "listings.html" [:div#ingredients-list [:ul (html/nth-of-type 1)] [:li (html/nth-of-type 2)]]
  [{:keys [name id]}]
  [:li :a] (html/do->
            (html/set-attr :href (format "recipes?ingredientId=%s#hr" id))
            (html/content name)))

(html/deftemplate ingredients-template "listings.html"
  [site-title ingredients-grouped categories site-name up-text title-text]
  [:div#recipes :h1] (html/content site-title)
  [:div#ingredients-list] (html/content
                           (map (partial grouped-list ingredient-list-item) ingredients-grouped))
  [:ul#menu] (html/content
              (map (fn [{:keys [title link]}]
                     (menu-bar title (name link)))
                   categories))
  [:div#title :h1] (html/content site-name)
  [:a.up ] (html/content up-text)
  [:head :title] (html/content title-text))

(html/defsnippet recipe-list-item "listings.html" [:div#ingredients-list [:ul (html/nth-of-type 1)] [:li (html/nth-of-type 2)]]
  [{:keys [title]}]
  [:li :a] (html/content title))

(html/deftemplate recipes-template "listings.html"
  [site-title recipes-grouped categories site-name up-text title-text]
  [:div#recipes :h1] (html/content site-title)
  [:div#ingredients-list] (html/content
                           (map (partial grouped-list recipe-list-item) recipes-grouped))
  [:ul#menu] (html/content
              (map (fn [{:keys [title link]}]
                     (menu-bar title (name link)))
                   categories))
  [:div#title :h1] (html/content site-name)
  [:a.up ] (html/content up-text)
  [:head :title] (html/content title-text))


(html/defsnippet thumbnail "index.html" [[:div.thumbnail (html/nth-of-type 1)]]
  [thumbnail-link name description]
  [:img] (html/set-attr :src thumbnail-link)
  [:h2] (html/content name)
  [:p] (html/content description))


(html/deftemplate main-template "index.html"
  [recipes categories max-ingredients site-title site-name up-text title-text]
  [:div#thumbnails] (html/content
                     (map (fn [{:keys [title ingredients thumbnail-link]}]
                            (thumbnail thumbnail-link title (render-list 7 ingredients)))
                          recipes))
  [:ul#menu] (html/content
              (map (fn [{:keys [title link]}]
                     (menu-bar title (name link)))
                   categories))
  [:div#recipes :h1] (html/content site-title)
  [:div#title :h1] (html/content site-name)
  [:a.up ] (html/content up-text)
  [:head :title] (html/content title-text))


;; Returns not-found html with dynamic message (first argument) in the content of
;; 'div#title h1' tag and filled categories (sequence of category maps)
;; in the content of 'ul#menu' tag.
(html/deftemplate not-found-template "not-found.html"
  [not-found-message categories]
  [:div#title :h1] (html/content not-found-message)
  [:ul#menu] (html/content
              (map (fn [{:keys [title link]}]
                     (menu-bar title (name link)))
                   categories)))