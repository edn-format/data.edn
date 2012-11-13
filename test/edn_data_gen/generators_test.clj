(ns edn-data-gen.generators-test
  (:use edn-data-gen.generators
        clojure.test)
  (:require [clojure.test.generative.generators :as gen]))


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

;; (defspec lots-of-symbols
;;   ns-symbol)
