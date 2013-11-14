;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  org.edn-format.data.edn.examples.create-strings
  "Example of creating strings of edn data using the printable.interposed impl of IPrintable."
  (:require [clojure.data.generators :as gen]
            [org.edn-format.data.edn.generators :as edn-gen]
            [org.edn-format.data.edn :as edn]
            [org.edn-format.impls.printable.default :as printable-default]
            [org.edn-format.impls.writer.io-writer :as io-writer]))

(comment
  (edn/string {:a 1 :b 2} nil)
  (edn/string [{:a 1 :b 2} {:a 2 :b 4}] nil)
  (edn/forms-string [{:a 1 :b 2} {:a 2 :b 4}] nil)

  (edn/string (gen/int) nil)
  (edn/string (gen/vec gen/int) nil)
  (edn/forms-string (gen/vec gen/int) nil)

  (edn/forms-string (gen/vec gen/int) {:form-separator "\n"})

  (edn/string (gen/vec edn-gen/number) nil)
  (edn/string (gen/vec edn-gen/scalar) nil)
  (edn/string (edn-gen/hierarchy 3 5 edn-gen/mixed-collection edn-gen/scalar) nil)

  (edn/many-strings gen/int 3 {})
  (edn/many-strings #(gen/vec gen/int 3) 3  {})
  (edn/many-forms-strings #(gen/vec gen/int 3) 3  {})

  (edn/many-forms-strings #(gen/vec gen/int 3) 3  {:form-separator "\n"})
  )
