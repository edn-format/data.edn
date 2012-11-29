;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn.data.generators-test
  (:use org.enclojure.edn.data.generators
        clojure.test)
  (:require [clojure.data.generators :as gen]))

(def symbol-start-chars
  (set (map char @#'gen/symbol-start)))

(def symbol-chars
  (set (map char @#'gen/symbol-char)))

(def ns-symbol-chars
  (conj symbol-chars \.))

(defn valid-ns-start?
  [s]
  (contains? symbol-start-chars
             (first s)))

(defn all-valid-ns-chars?
  [s]
  (not (some #(not (contains? ns-symbol-chars %))
             s)))

(defn valid-ns-dot-positions?
  [s]
  (not (or
        (.contains s "..")
        (.endsWith s "."))))

(defn valid-ns?
  [s]
  (and (valid-ns-start? s)
       (all-valid-ns-chars? s)
       (valid-ns-dot-positions? s)))

(deftest ns-str-test
  (testing "namespace generation"
    (doseq [_ (range 100)]
      (is (valid-ns? (ns-str))))
    (doseq [x (range 1 20) y (range 1 40)]
      (is (valid-ns? (ns-str x y))))))

;; is ns part of symbol valid-ns?
;; is char after ns a slash
;; is there only 1 slash
;; is symbol after slash valid



;; (deftest ns-symbol-test
;;   (testing "namespace qualified symbol generation"
;;     (doseq [n (range 1 12)]
;;       (let [sizers (range 1 n)
;;             ns-sizers (butlast sizers)
;;             sym (apply ns-symbol sizers)]

;;         (is (= (max 0 (dec (count ns-sizers)))
;;              (count (re-seq #"\." (namespace sym)))))
;;         (is (= 1 (count (re-seq #"/" (str sym)))))))))

(defn scalar?
  [x]
  (not (coll? x)))

(defn scalar-coll?
  [coll]
  (let [vs (if (map? coll)
             (concat (keys coll) (vals coll))
             coll)]
    (not (some coll? vs))))


(defn depth-count
  ([data]
     (depth-count 0 data))
  ([depth data]
     (if (or (scalar? data)
             (scalar-coll? data))
       depth
       (let [vs (if (map? data)
                  (concat (keys data) (vals data))
                  data)]
         (apply max (map (partial depth-count (inc depth)) vs))))))
