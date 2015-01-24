; TODO: file bug about checker defn blowing up when there are no type-hints in an interop method call
; TODO: fully-qualified methods in non-nil-return highlight badly
; TODO: the following causes assert failure:
; (defn connected? [^Socket socket :- Socket] :- Boolean
;(.isConnected Socket))
