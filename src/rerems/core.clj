(ns rerems.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.set :as set]))

(defn times [factor value]
  (let [new-value (.stripTrailingZeros (.setScale (* (bigdec factor) (bigdec value)) 3 java.math.RoundingMode/HALF_UP))]
    (if (< (.scale new-value) 0)
      (str (.setScale new-value 0))
      (str new-value))))

(def rem-pattern #"(\d+[\d.]*|\.[\d]+)px")

(defn update-rem-value [[_ group]]
  (str (times 0.0625 group) "rem"))

(defn replace-rem-values [subject]
  (str/replace subject rem-pattern update-rem-value))

(defn process-file [filepath]
  (let [content  (slurp filepath)
        modified (replace-rem-values content)]
    (spit filepath modified)))

(def flex-dir (io/file "/Users/liesbeth.wijers/Development/Pep/flux/flex/app"))
(def grid-dir (io/file "/Users/liesbeth.wijers/Development/Pep/flux/grid/app"))

(defn stylesheets [path]
  (let [grammar-matcher (.getPathMatcher
                         (java.nio.file.FileSystems/getDefault)
                         "glob:*.{css,scss,sass}")]
    (->> path
         io/file
         file-seq
         (filter #(.isFile %))
         (filter #(.matches grammar-matcher (.getFileName (.toPath %))))
         (mapv #(.getAbsolutePath %)))))


(def files (stylesheets grid-dir))

(defn process-with-guardrails [files]
  (let [try-process (fn [file] (try
                                   (do
                                     (process-file file)
                                     nil)
                                 (catch Exception e file)))]
    (map try-process files)))

;(def matches (reduce (fn [matches filename] (clojure.set/union matches (set (re-seq rem-pattern (slurp filename))))) #{} files))

(defn process-file-prettier [filepath]
  (let [content  (slurp filepath)
        modified (-> content
                     (str/replace #"0.844rem" "0.85rem")
                     (str/replace #"2.016rem" "2rem")
                     (str/replace #"749.25rem" "750rem")
                     (str/replace #"0.094rem" "0.01rem")
                     (str/replace #"1.406rem" "1.4rem"))]
    (spit filepath modified)))

;(map process-file-prettier files)
;(def result (process-with-guardrails files))
;
;(def errors (filter some? result))
;
;(process-file (nth errors 0))
