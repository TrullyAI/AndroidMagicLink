# Trully Magic in Android project example

## How to use it

1. Create a webhook from [webhook.site](https://webhook.site) (Make sure you edit the webhook to enable CORS)
2. Go to client [Dashboard](https://sandboxapp.trully.ai) and copy your API KEY.
3. Clone this repository
4. Open Android Studio
5. Open <i>ChromeCustomTabViewModel<i>. Replace YOUR_API_KEY for the API KEY you got from our Dashboard.
6. Open <i>WebhookService<i>. Replace YOUR_WEBHOOK_TOKEN for the token of the webhook you create. The token is the text you see after the <i>https:webhook.site/</i>. I.e if your url is <i>https:webhook.site/123-456</i> then your token would be <i>123-456</i> 
7. Open <i>ChromeCustomTabFragment<i>. Replace YOUR_USER_ID, YOUR_MAGIC_LINK_TITLE and YOUR_WEBHOOK_URL for any string. Make sure you only use letter and numbers.
8. Run app

## ⚠️ Important
To repeat the tests after a successful try make sure you change the userID