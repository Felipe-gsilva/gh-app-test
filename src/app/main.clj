(ns app.main
  (:require [app.webhooks :as webhooks]
            [clj-github.changeset :as changeset]
            [clj-github-app.client :as client]
            [clj-github.httpkit-client :as github-client]))

(def client (github-client/new-client
             {:app-id (System/getenv "APP_ID")
              :private-key (System/getenv "private-key")}))

(github-client/request client {:path "/api/github/..."
                               :method :get})

(def github-api-url "https://api.github.com")
(def github-app-id (System/getenv "GITHUB_APP_ID"))
(def github-app-private-key (System/getenv "GITHUB_APP_PRIVATE_KEY_PEM"))

(def commit-message "Commited by moclojer sync app!!")

(def github-client
  (client/make-app-client github-api-url github-app-id github-app-private-key {}))

(defn update-file [client owner repo branch file-path new-content]
  (-> (changeset/from-branch! client owner repo branch)
      (changeset/put-content file-path new-content)
      (changeset/commit! (str commit-message \n "carlos"))
      (changeset/update-branch!)))

(defn pull-file [client org repo base-revision changes file-path]
  (-> (changeset/get-content
       {:client client
        :org  org
        :repo repo
        :base-revision base-revision
        :changes changes}
       file-path)))
