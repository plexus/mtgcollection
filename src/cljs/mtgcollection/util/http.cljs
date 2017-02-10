(ns mtgcollection.util.http
  (:require [ajax.core :as ajax]))

(defn http-request [method uri opts]
  (merge
   {:method method
    :uri uri
    :format          (ajax/transit-request-format)
    :response-format (ajax/transit-response-format)
    :on-failure      [:http/request-failed]}
   opts))

(defn GET [uri & {:as opts}]
  (http-request :get uri opts))

(defn POST [uri & {:as opts}]
  (http-request :post uri opts))
