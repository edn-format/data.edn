;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  org.edn-format.data.edn.file-util
  "Utility functions for files and directories."
  (:require [clojure.java.io :as io]))

(defn- call-through
  "Recursively call x until it doesn't return a function."
  [x]
  (if (fn? x)
    (recur (x))
    x))

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

(defn ensure-path!
  [path-gen]
  (let [path (call-through path-gen)]
    (ensure-parent-directory! path)
    path))

(defn out-parent
  []
  (str (System/getProperty "user.dir")
       java.io.File/separator
       "output"))

(defn out-dir
  ([parent-dir dir-name]
     (str parent-dir
          java.io.File/separator
          dir-name))
  ([dir-name]
      (out-dir (out-parent) dir-name)))

(defn out-path
  [filename]
  (str (out-parent)
       java.io.File/separator
       filename))

(defn file-name
  []
  (str (java.util.UUID/randomUUID) ".edn"))

(defn typed-file-name
  [type & [suffix]]
  (let [suffix-str (when suffix (str "_" (name suffix)))]
    (format "%s_%s%s.edn"
            (name type)
            (str (java.util.UUID/randomUUID))
            (str suffix-str))))

(defn file-path
  ([]
     (file-path (out-parent) file-name))
  ([parent-dir file-name-gen]
     (str parent-dir
          java.io.File/separator
          (file-name-gen))))

(defn typed-file-path
  [parent-dir & type-params]
  (file-path parent-dir (apply partial (cons typed-file-name type-params))))

(defn file-run-path
  ([run-id]
     (file-run-path (out-parent) run-id))
  ([parent-dir run-id]
      (file-path (out-dir parent-dir (name run-id)) #(typed-file-name run-id))))
