(ns edn-data-gen.edn-files
  (require [clojure.test.generative.generators :as gen]
           [clojure.string :as string]
           [clojure.java.io :as io]
           [edn-data-gen.generators :as edngen]))

(defn- file-exists?
  "returns a bool indicating whether a dir or file exists"
  [path]
  (.exists (java.io.File. path)))

(defn- mkdir
  [path]
  (.mkdir (java.io.File. path)))

(defn ls
  [path]
  (let [file (java.io.File. path)]
    (if (.isDirectory file)
      (seq (.list file))
      (when (.exists file)
        [path]))))

(defn mkdirs [path]
  (.mkdirs (io/file path)))

(defn make-parent-dirs [path]
  (-> path
      io/file
      .getParent
      mkdirs))

(defn ensure-directory!
  [path]
  (when-not (ls path)
    (mkdirs path)))

(defn ensure-parent-directory!
  [path]
  (when-not (ls path)
    (make-parent-dirs path)))

(defn out-parent
  []
  (str (System/getProperty "user.dir")
       java.io.File/separator
       "output"))

(defn out-path
  [filename]
  (str (out-parent)
       java.io.File/separator
       filename))

(defn write-edn
  "Prints out an data structure in end format to a file."
  ([data]
     (write-edn (out-path "out.edn") data))
  ([filename data]
     (binding [*print-dup* true]
       (ensure-parent-directory! filename)
       (spit (str filename)
             (with-out-str (prn data))))))

(def num-gens
  [gen/int
   gen/long
   gen/float
   gen/double])

(defn file-of-ints
  ([n]
     (file-of-ints (out-path "ints.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n gen/int))))

(defn file-of-floats
  ([n]
     (file-of-floats (out-path "floats.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n gen/float))))

(defn file-of-numbers
  ([n]
     (file-of-numbers (out-path "numbers.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n (fn [] ((rand-nth num-gens)))))))

(defn file-of-keywords
  ([n]
     (file-of-keywords (out-path "keywords.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n gen/keyword))))
