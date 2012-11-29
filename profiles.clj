{:dev
 {:plugins [[codox "0.6.3"]]
  :codox {:src-dir-uri "http://github.com/enclojure/edn-gen/blob/refactor"
          :src-linenum-anchor-prefix "L"}}
 :release
 {:plugins [[lein-set-version "0.2.1"]]
  :set-version
  {:updates [{:path "README.md" :no-snapshot true}]}}}
