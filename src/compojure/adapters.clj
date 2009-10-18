;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

(ns compojure.adapters
  "Defines some adapters which provide a concise way of attaching a
  handler to a web server."
  (:require [compojure.server.jetty :as jetty]
            [compojure.server.grizzly :as grizzly])
  (:use compojure.http.servlet))

(defmacro make-adapter [server]
  `(defn ~server
     ([~'handler] (~server {} ~'handler))
     ([~'options ~'handler]
        (~(symbol (str server "/run-server"))
          ~'options
          "/*"
          (~'servlet ~'handler)))))

(make-adapter jetty)
(make-adapter grizzly)
