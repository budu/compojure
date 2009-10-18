;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

(ns compojure.adapters
  "Defines some adapters which provide a concise way of attaching a
  handler to a web server. It also keep server instances and provides an
  interface to start and stop them."
  (:require [compojure.server.jetty :as jetty]
            [compojure.server.grizzly :as grizzly])
  (:use compojure.http.servlet))

(def *servers* (ref {}))

(defn start-server [port]
  (.start (@*servers* port)))

(defn stop-server [port]
  (.stop (@*servers* port)))

(defn restart-server [port]
  (stop-server port)
  (start-server port))

(defn start-all-servers []
  (doseq [server (vals @*servers*)]
    (.start server)))

(defn stop-all-servers []
  (doseq [server (vals @*servers*)]
    (.stop server)))

(defn restart-all-servers []
  (stop-all-servers)
  (start-all-servers))

(defn dissoc-server [port]
  (dosync (alter *servers* dissoc port)))

(defmacro make-adapter [server]
  `(defn ~server
     ([~'handler] (~server {} ~'handler))
     ([~'options ~'handler]
        (let [~'server (~(symbol (str server "/" server "-server"))
                        ~'options
                        "/*"
                        (~'servlet ~'handler))
              ~'port (~'options :port 80)]
          (.start ~'server)
          (dosync (alter ~'*servers* assoc ~'port ~'server))
          ~'server))))

(make-adapter jetty)
(make-adapter grizzly)
