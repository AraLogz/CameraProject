package com.dev.alopez.cameraproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;;

/**
 * Created by alopez on 10/25/17.
 */

public class twitter{

    private final static String CONSUMER_KEY = "Ppf54pkZyaH8VL5KJ4JkevjIW";
    private final static String CONSUMER_KEY_SECRET = "bqfkNPRRlJOLvCWl5NYFDps3B2jaTsLZx66jGRQuXN4JbVa0eb";
    private final static String ACCESS_TOKEN ="923316791786590209-zxYAHlIvH6kQjthKoWJtaqT2BuvdySl";
    private final static String ACCESS_TOKEN_SECRET = "55hAxhq3Q1c6oxDfnjrqQ63MXcnD3b5miSKBJfPdwUeVp";

    Twitter mTwitter = TwitterFactory.getSingleton();

    public void updateStatus(String status, String imagePath)  throws Exception {
        StatusUpdate update = new StatusUpdate(status);
        //update.setMedia(new File(imagePath));

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_KEY_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        mTwitter = tf.getInstance();

        try {
            update(update);
        } catch (Exception e) {
            throw e;
        }
    }

    private void update(StatusUpdate update){
        AsyncTask<twitter4j.StatusUpdate, Void, twitter4j.Status> task = new AsyncTask<twitter4j.StatusUpdate, Void, twitter4j.Status>() {
            @Override
            protected twitter4j.Status doInBackground(twitter4j.StatusUpdate... params) {
                twitter4j.Status status = null;
                try {
                    status = mTwitter.updateStatus(params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return status;
            }
            @Override
            protected void onPostExecute(twitter4j.Status result) {
                //Log.d("Tweeted", result.get);
            }
        };
        task.execute(update);
    }
}
