(defproject clj-sockets "0.1.0"
  :description "Sockets library for Clojure"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-marginalia "0.8.0"]
            [codox "0.8.10"]]
  :codox {:src-dir-uri "http://github.com/atroche/clj-sockets/blob/master/"
          :src-linenum-anchor-prefix "L"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.typed "0.2.77"]])
