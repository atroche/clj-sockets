;; ## A Clojure library wrapping Java Sockets

;; I noticed there were a number of projects in the Clojure ecosystem
;; using native Java sockets to work with HTTP, IRC, etc., and it seemed
;; a shame that they were all forced to use the same ugly Java interop code.
;; What if they could just write idiomatic Clojure instead?

(ns clj-sockets.core
  "Contains all of the functions that make up the public API of this project."
  (:require [clojure.core.typed :as t :refer [ann]]
            [clojure.java.io :refer [writer reader]])
  (:refer-clojure :exclude [read-line])
  (:import (java.net Socket ServerSocket)
           (java.io BufferedWriter BufferedReader)
           (clojure.lang Seqable)))

;; We need to annotate these Clojure functions if we want to call them,
;; so that core.typed knows what they take as arguments and what they return.
;; Most clojure.core functions are already annotated, but some still aren't.
(ann ^:no-check clojure.java.io/writer [Socket -> BufferedWriter])
(ann ^:no-check clojure.java.io/reader [Socket -> BufferedReader])
(ann ^:no-check clojure.core/line-seq [BufferedReader -> (Seqable String)])

(ann create-socket [String Integer -> Socket])
(defn create-socket
  "Connect a socket to a remote host. The call blocks until
   the socket is connected."
  [^String hostname ^Integer port]
  (Socket. hostname port))

(ann close-socket [Socket -> nil])
(defn close-socket
  "Close the socket, and also closes its input and output streams."
  [^Socket socket]
  (.close socket))

(ann write-to-buffer [BufferedWriter String -> nil])
(defn ^:private write-to-buffer
  "Write a string to a BufferedWriter, then flush it."
  [^BufferedWriter output-stream ^String string]
  (.write output-stream string)
  (.flush output-stream))


(ann write-to [Socket String -> nil])
(defn write-to
  "Send a string over the socket."
  [socket message]
  (write-to-buffer (writer socket) message))

(ann write-line [Socket String -> nil])
(defn write-line
  "Send a line over the socket."
  [socket message]
  (write-to socket (str message "\n")))

; this is memoized so that we always get the same reader for
; a given socket. otherwise the temporary readers could have text
; loaded into their buffers and then never used
(ann get-reader [Socket -> BufferedReader])
(def ^:private get-reader
  "Get the BufferedReader for a socket.

  This is memoized so that we always get the same reader for a given socket.
  If we didn't do this, every time we did e.g. read-char on a socket we'd
  get back a new reader, and that last one would be thrown away despite having
  loaded input into its buffer."
  (memoize (t/ann-form
             (fn [^Socket socket]
              (reader socket))
             (t/IFn [Socket -> BufferedReader]))))

(ann read-char [Socket -> Character])
(defn read-char
  "Read a single character from a socket."
  [socket]
  (let [read-from-buffer (t/ann-form
                           (fn [^BufferedReader input-stream]
                            (.read input-stream))
                           (t/IFn [BufferedReader -> Integer]))]
    (-> socket
        get-reader
        read-from-buffer
        char)))

(ann read-lines [Socket -> (Seqable String)])
(defn read-lines
  "Read all the lines currently loaded into the input stream of a socket."
  [socket]
  (line-seq (get-reader socket)))

;; core.typed is paranoid about Java methods returning nil, but lets you
;; override that if you're fairly sure that it's not going to.
(t/non-nil-return java.io.BufferedReader/readLine :all)

(ann read-line [Socket -> String])
(defn read-line
  "Read a line from the given socket"
  [^Socket socket]
  (let [read-line-from-reader (t/ann-form
                                (fn [^BufferedReader reader]
                                  (.readLine reader))
                                (t/IFn [BufferedReader -> String]))]
    (read-line-from-reader (get-reader socket))))

(ann create-server [Integer -> ServerSocket])
(defn create-server
  "Initialise a ServerSocket on localhost using a port.

  Passing in 0 for the port will automatically assign a port based on what's
  available."
  [^Integer port]
  (ServerSocket. port))

(t/non-nil-return java.net.ServerSocket/accept :all)
(ann listen [ServerSocket -> Socket])
(defn listen
  "Waits for a connection from another socket to come through, then
   returns the server's now-connected Socket."
  [^ServerSocket server-socket]
  (.accept server-socket))

