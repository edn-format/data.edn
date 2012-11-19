(ns edn-data-gen.scratch
  "Short package description.")

(comment
  (make-lists 1 10 ints)
  (make-ints 10)

  #_(defrecord ChildOnlyPrinter [delimiter-maker]
        IPrintEdn
      (to-edn-string [this data] (apply str (map .to-edn-str data))))

  #_(defrecord SeqPrinter [delimiter-maker]
        IPrintEdn
      (to-edn-string [this data]
        (let [chil-str (apply str (map .to-edn-str data))]
          (str (get-prefix this) child-str (get-suffix this))) ))

  (def child-printer (->ChildOnlyPrinter (crazy-delim-maker)))


(defn make-ints
  []
  (writeasfasdf child-printer (gen/list gen/int n)))



;; collections

(to-edn-string [1 2 3] _printer?_)

;; scalars



  #_(defrecord ChildOnlyPrinter [delimiter-maker data]
        IPrintEdn
      (to-edn-string [this] (apply str (map .to-edn-str data))))

(defn make-ints
  []
  (->ChildOnlyPrinter space-delimiter-maker (gen/list gen/int n)))




(defrecord NoBoundsCollectionMaker [data]
  IPrintEdn
  (to-edn-string [this edn-printer] (interpose-delimiter edn-printer data)))

;; scalar
(to-edn-string [this edn-printer] (str this))

(print-edn edn-printer this)



(defn make-ints
  []
  (to-edn-string (NoBoundsCollectionMaker. (gen/list gen/int n))
                 edn-printer))


  )
