;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn.gen
  "Generate edn by printing using the IPrintble protocol to a writer that impliment IWriter."
  (:require [org.enclojure.print.protocols.printable :as printable]
            [org.enclojure.print.util :as print-util]))

(defn edn
  "Print edn data to writer using opts."
  [data writer opts]
  (printable/print data writer opts))

(defn edn-forms
  "Print the contents of coll to writer using opts"
  [coll writer opts]
  (let [opts-with-defaults (merge {:form-separator " "} opts)]
    (print-util/print-seq-contents coll writer opts (:form-separator opts-with-defaults))))

(defn many-of
  "using an edn-gen-fn (of signiture [data opts])"
  [edn-gen-fn data-generator n opts]
  (for [_ (range n)]
    (let [data (data-generator)]
      {:out (edn-gen-fn data opts)
       :data data})))
