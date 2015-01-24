(ns clj-sockets.core-test
  (:refer-clojure :exclude [read-line])
  (:require [clojure.test :refer :all]
            [clojure.string :refer [split]]
            [clj-sockets.core :refer :all]))

(deftest ^:integration create-echo-server-and-talk-to-it
  (let [echo-server (agent (create-server 0)) ; port 0 means choose free port
        port (.getLocalPort @echo-server)]
    (send echo-server listen)
    (send echo-server (fn [socket]
                        (let [line (read-line socket)]
                          (write-line socket line)
                          (close-socket socket))))
    (let [client (create-socket "localhost" port)]
      (write-line client "hello")
      (let [response (read-line client)]
        (is (= "hello" response))
        (close-socket client)))))

(def popular-hosts ["yahoo.com" "google.com" "wikipedia.org" "qq.com"
                    "twitter.com" "baidu.com"])

(defn http-request-string [hostname]
  (format "GET / HTTP/1.1\nHost: %s\n\n" hostname))

(deftest ^:integration make-http-requests-to-popular-hosts

  (doseq [hostname popular-hosts]
    (let [socket (create-socket hostname 80)]
     (write-to socket (http-request-string hostname))
     (let [response (read-line socket)
           protocol (first (split response #" "))]
       (is (= "HTTP/1.1" protocol))))))
