;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn.string.generation
  "Functions for generating strings of edn."
  (:require [org.enclojure.edn.gen :as edn-gen]))

(defn string
  "Creates an edn string from data. Passing opts through to a StringWriter."
  [data opts]
  (let [sw (java.io.StringWriter.)
        _ (edn-gen/edn data sw opts)]
    (str sw)))

(defn forms-string
  "Create an edn string with the contents of coll. Passing opts through to a StringWriter."
  [coll opts]
  (let [sw (java.io.StringWriter.)
        _ (edn-gen/edn-forms coll sw opts)]
    (str sw)))

(def many-strings
  (partial edn-gen/many-of string))

(def many-forms-strings
  (partial edn-gen/many-of forms-string))
