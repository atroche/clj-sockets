# clj-sockets

![Travis build](https://travis-ci.org/atroche/clj-sockets.svg?branch=master)

A Clojure library wrapping Java Sockets. Because you shouldn't have to use interop to do networking.

clj-sockets is synchronous. For asynchronous networking in Clojure, check out [aleph](https://github.com/ztellman/aleph) or [async-sockets](https://github.com/bguthrie/async-sockets).

This library is fully annotated using [core.typed](https://github.com/clojure/core.typed).

## Installation

Just put `[clj-sockets "0.1.0"]` in `:dependencies` in your project.clj.

## Usage

### Connecting to a server

```clojure
(require '[clj-sockets.core :refer [create-socket write-to close
                                    read-line read-lines write-lines])

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

(close socket)
=> nil
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
=> "hello from telnet"

(write-line server "hello there, person using telnet!")
=> nil

(close server)
=> nil
```


## License

Copyright © 2015 Alistair Roche

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
