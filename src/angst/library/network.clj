(ns angst.library.network
	(:require [quil.core :as q]
			  [angst.library.data :as d]
			  [clj-http.client :as client])
	(:use [ring.adapter.jetty]
		  [ring.util.request]))

(def port 3000)

(def shared-state [:phase :options :empires :planets :empire :active :active-planet :next-player-map :constant-effects :effect-details :ship-move :action-log])
(def client-updates [:planets :empire :active :active-planet :next-player-map :constant-effects :effect-details :ship-move :action-log])

(defn get-response [request]
	{:status 200
	 :headers {"Content-Type" "text/html"}
	 :body (str @d/serverdata)})

(defn post-response [request]
	(let [body (read-string (body-string request))]
		(do (reset! d/host-update-required body)
			(reset! d/serverdata body)
		   {:status 203}))) ; Status 203 created

(def error-response {:status 400}) ; Status 400 bad request

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

(defn can-connect?
	[state host-ip]
	(try (client/get host-ip) (catch Exception e false)))

(defn get-host-state
	[state host-ip]
	(let [response (try (client/get host-ip) (catch Exception e false))]
		(if-let [body (read-string (:body response))]
			(merge state body)
			state)))

(defn send-new-state
	"Executed for side effects (post request), returns status"
	[state host-ip]
	(:status (client/post host-ip (create-post (select-keys state client-updates)))))

(defn get-address [state]
	(str "http://" (clojure.string/replace (-> state :text-inputs :ip-input :value) #"[^0-9.]" "") ":" port))