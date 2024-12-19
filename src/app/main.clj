(ns app.main
  (:require
   [clojure.string :as str]
   [clj-github.changeset :as changeset]
   [clj-github-app.client :as c]
   [clj-github-app.token-manager :as token-manager]
   [clj-github.httpkit-client :as github-client]))

(defn inspect [a] (prn a) a)

;; testing push event into webhook

(def commit-message (str "Auto genereted commit by moclojer sync app!!"
                         " "))

(def github-api-url "https://api.github.com")
(def github-app-id "")
(def install-id nil)
(def github-app-private-key (slurp ""))

(def gh-client
  (github-client/new-client
   {:app-id github-app-id
    :private-key  github-app-private-key}))

(def tk
  (token-manager/make-token-manager
   github-api-url
   github-app-id
   github-app-private-key))

(defn get-installation-token
  [installation-id]
  (token-manager/get-installation-token
   tk
   installation-id))

(defn make-authenticated-request [installation-id endpoint]
  (let [token (get-installation-token installation-id)
        headers {"Authorization" (str "Bearer " token)
                 "Accept" "application/vnd.github+json"}]
    ;; Make your HTTP request with the headers
    (github-client/request
     gh-client
     {:method "GET"
      :path (inspect (str github-api-url endpoint))
      :headers (inspect headers)})))

(make-authenticated-request
 install-id "/app")

(def github-client
  (c/make-app-client
   github-api-url
   github-app-id
   github-app-private-key
   {}))

(defn update-file
  [owner repo branch file-path new-content]
  (when (not (or (str/includes? "moclojer" file-path)
                 (str/includes? "mocks" file-path)))
    (str "moclojer/mocks/" (take-last 1 (str/split "/" file-path))))
  (-> (changeset/from-branch! gh-client owner repo branch)
      (changeset/put-content file-path new-content)
      (changeset/commit! (str commit-message \n "carlos"))
      (changeset/update-branch!)))

(defn pull-file
  [org repo base-revision changes file-path]
  (-> (changeset/get-content
       {:client gh-client
        :org  org
        :repo repo
        :base-revision base-revision
        :changes changes}
       file-path)))

(defn main [])

(comment
  (github-client/request gh-client {:path "/api/github/user"
                                    :method :get}))
