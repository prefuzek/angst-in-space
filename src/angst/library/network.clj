(ns angst.library.network
	(:require [quil.core :as q]
			  [angst.library.data :as d]
			  [clj-http.client :as client])
	(:use [ring.adapter.jetty]
		  [ring.util.request]))

(def port 3000)

(defn get-response [request]
	(if @d/client-update-required
		{:status 200
		 :headers {"Content-Type" "text/html"}
		 :body (str @d/serverdata)}
		{:status 304})) ; Status 304 not modified

(defn post-response [request]
	(let [body (read-string (body-string request))]
		(if (not-empty body)
			(do (reset! d/host-update-required body)
				(reset! d/serverdata body)
			   {:status 203}) ; Status 203 created
			{:status 400}))) ; Status 400 bad request (no body)

(def error-response {:status 400})

(defn create-post [state]
	{:body (str state)
	 :content-type :text})

(defn request-handler
	"Directs requests to proper response function"
	[request]
	(cond (= (:request-method request) :get)
			(get-response request)
		  (= (:request-method request) :post)
		  	(post-response request)
		  :else error-response))

(defonce host-server
	(run-jetty request-handler {:port port :join? false}))

(defn get-host-state
	"If the host's state has updated, returns that new state, otherwise returns state unchanged"
	[state host-ip]
	(let [response (client/get host-ip)]
		(if (= (:status response) 200)
			(if-let [body (read-string (:body response))]
				body
				state)
			state)))

(defn send-new-state
	"Executed for side effects (post request), returns status"
	[state host-ip]
	(:status (client/post host-ip (create-post state))))

(defn get-address []
	(str "http://" (slurp "hostaddress.txt") ":" port))