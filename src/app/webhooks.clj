(ns app.webhooks
  (:require [clj-github-app.webhook-signature :as webhook-signature]
            [clojure.data.json :as json]))

(def GITHUB_WEBHOOK_SECRET (System/getenv "GITHUB_WEBHOOK_SECRET"))

(defn post-github
  "Checks if the webhook is valid and handles it."
  [request]
  (let [{:strs [x-github-delivery x-github-event x-hub-signature-256]} (:headers request)
        payload (slurp (:body request))]
    (case (webhook-signature/check-payload-signature-256 GITHUB_WEBHOOK_SECRET x-hub-signature-256 payload)
      ::webhook-signature/missing-signature {:status 400 :body "x-hub-signature-256 header is missing"}
      ::webhook-signature/wrong-signature {:status 401 :body "x-hub-signature-256 does not match"}
      (let [parsed-payload  (json/read-str payload keyword)]
        ;; process your webhook here
        {:status 201 :body "This is fine."}))))


(def smee "")
