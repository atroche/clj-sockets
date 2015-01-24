# clj-sockets

A Clojure library wrapping Java Sockets. Because you shouldn't have to use interop to do networking.

clj-sockets is synchronous. For asynchronous networking in Clojure, check out [aleph](https://github.com/ztellman/aleph).

This library is fully annotated using core.typed.

## Installation

FIXME

## Usage

### Connecting to a server

```clojure
(require '[clj-sockets.core :as s :refer [create-socket write-to])

(def socket (create-socket "google.com" 80))
=> #'clj-sockets.core/socket

socket
=> #<Socket Socket[addr=google.com/150.101.213.167,port=80,localport=57433]>

(write-to socket "GET / HTTP/1.1\nHost: google.com\n\n")
=> nil

(read-line socket)
=> "HTTP/1.1 302 Found"

(read-lines socket)
=> ("Cache-Control: private" "Content-Type: text/html; charset=UTF-8" etc.)
```

### Creating a server

```clojure
(def server (listen (create-server 9871)))
; blocks until a connection is made
; in this case I'm doing "telnet localhost 9871" from the shell
=> #'clj-sockets.core/server

server
=> #<Socket Socket[addr=/0:0:0:0:0:0:0:1%0,port=57437,localport=9871]>

(read-line server)
; blocks until a line is sent (in this case through telnet)
=> "asdf"
```


## License

Copyright © 2015 Alistair Roche

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
