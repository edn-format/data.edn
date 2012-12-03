(ns ^{:author "Tom Hickey, Jim Altieri"}
  com.thortech.data.edn.examples.create-strings
  "Example of creating strings of edn data using the printable.interposed impl of IPrintable."
  (:require [clojure.data.generators :as gen]
            [com.thortech.data.edn.generators :as edn-gen]
            [com.thortech.data.edn :as edn]
            [com.thortech.impls.printable.default :as printable-default]
            [com.thortech.impls.writer.io-writer :as io-writer]))

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
