(defproject angst "0.7.0-SNAPSHOT"
  :description "A space strategy board game for 2-5 players"
  :url "https://github.com/prefuzek/angst-in-space"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [quil "2.5.0"]
                 [ring "1.6.2"]
                 [clj-http "3.7.0"]
                 [criterium "0.4.4"]]
  :aot [angst.core]
  :main angst.core)
