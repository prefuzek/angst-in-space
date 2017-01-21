(defproject angst "0.2.0-SNAPSHOT"
  :description "A space strategy board game for four players"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [quil "2.5.0"]]
  :aot [angst.core]
  :main angst.core)
