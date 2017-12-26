(ns angst.library.network
	(:require [quil.core :as q]
			  [angst.library.data :as d]
			  [clj-http.client :as client])
	(:use [ring.adapter.jetty]
		  [ring.util.request]))

(def port 3000)

(def error-response {:status 400}) ; Status 400 bad request

(def shared-state [:phase :options :empires :planets :empire :active :active-planet :next-player-map :constant-effects :effect-details :ship-move :action-log])
(def client-updates [:phase :planets :empire :active :active-planet :next-player-map :constant-effects :effect-details :ship-move :action-log])

(defn get-response [request]
	(if @d/client-update-required ; Right now always true. TODO: fix for less bandwidth usage
		{:status 200
		 :headers {"Content-Type" "text/html"}
		 :body (str @d/serverdata)}
		{:status 304})) ; 304 not changed

(defn post-response [request]
	(let [body (read-string (body-string request))]
		(condp = (:type body)
			:update
				(do (reset! d/host-update-required (:data body))
					(swap! d/serverdata #(merge % (:data body)))
				   {:status 203}) ; Status 203 created
			:connect
				(do (swap! d/connected-players conj (:data body))
					{:status 203})
			:disconnect
				(do (swap! d/connected-players #(disj % (:data body)))
					{:status 203})
			error-response)))

(defn create-post
	"Type: :update for sending updated state, :connect for initial connection"
	[type data]
	{:body (str {:type type :data data})
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

(defn connect
	[state host-ip]
	(try (client/post host-ip (create-post :connect (:online-name state))) (catch Exception e false)))

(defn disconnect
	[state host-ip]
	(try (client/post host-ip (create-post :disconnect (:online-name state))) (catch Exception e false)))

(defn get-host-state
	[state host-ip]
	(let [response (try (client/get host-ip) (catch Exception e false))]
		(if-let [body (read-string (:body response))]
			(merge state body)
			state)))

(defn send-new-state
	"Executed for side effects (post request), returns status"
	[state host-ip]
	(try (:status (client/post host-ip (create-post :update (select-keys state client-updates)))) (catch java.net.ConnectException e nil)))

(defn get-address [state]
	(str "http://" (clojure.string/replace (-> state :text-inputs :ip-input :value) #"[^0-9.]" "") ":" port))