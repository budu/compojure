;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.http.session:
;;
;; Functions for creating and updating HTTP sessions.

(ns compojure.http.session
  (:use compojure.str-utils)
  (:use compojure.http.helpers)
  (:use compojure.http.response))

(def memory-sessions (ref {}))

(defn- create-session
  "Create a new in-memory session and return the session ID."
  []
  (dosync
    (let [id (gen-uuid)]
      (alter memory-sessions assoc id (ref {}))
      id)))

(defn- get-session-id
  "Get the session ID from the request or create a new session."
  [request]
  (let [id (get-in request [:cookies :session-id])]
    (if (contains? @memory-sessions id)
      id
      (create-session))))

(defn- set-session-id
  "Create a response with a session ID."
  [response session-id]
  (merge-response
    response
    (set-cookie "session-id" session-id)))

(defn with-session
  "Wrap a handler in a session."
  [handler]
  (fn [request]
    (let [session-id (get-session-id request)
          request    (assoc request :session-id session-id)
          response   (handler request)]
      (set-session-id response session-id))))

(defn get-session
  "Get an in-memory session via a request map augmented by with-session."
  [request]
  (@memory-sessions (:session-id request)))
