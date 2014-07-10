package com.cocofla.sasamifry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class Home extends Activity {
    public static final String TAG = "Home";
    
    private AsyncTwitter _twitter;
    private RequestToken _reqToken;

    private final TwitterListener _listener = new TwitterAdapter() {
        @Override
        public void gotOAuthRequestToken(RequestToken token) {
            Log.d(TAG, "gotOAuthRequestToken");
            _reqToken = token;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_reqToken.getAuthorizationURL()));
            startActivity(intent);
        }

        @Override
        public void gotOAuthAccessToken(AccessToken token) {
            Log.d(TAG, "gotOAuthAccessToken");
            saveTwitterAccessToken(token);
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            Log.d(TAG, te.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate");

        AccessToken token = restoreTwitterAccessToken();
        if (token == null) {
            startTwitterAuth();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // ブラウザからのコールバックで呼ばれる
        final Uri uri = intent.getData();
        if (uri != null) {
            Log.d(TAG, "intent has uri");
            final String verifier = uri.getQueryParameter("oauth_verifier");
            if (verifier != null) {
                Log.d(TAG, "uri has oauth_verifier");
                _twitter.getOAuthAccessTokenAsync(_reqToken, verifier);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d(TAG, "delete the twitter credential");
            SharedPreferences pref = getSharedPreferences(getString(R.string.user_twitter_account_info_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(getString(R.string.access_token_key));
            editor.remove(getString(R.string.access_token_secret_key));
            editor.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTwitterAccessToken(AccessToken token) {
        SharedPreferences pref = getSharedPreferences(getString(R.string.user_twitter_account_info_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.access_token_key), token.getToken());
        editor.putString(getString(R.string.access_token_secret_key), token.getTokenSecret());
        editor.apply();
    }

    private AccessToken restoreTwitterAccessToken() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.user_twitter_account_info_key), MODE_PRIVATE);
        String token = pref.getString(getString(R.string.access_token_key), null);
        String tokenSecret = pref.getString(getString(R.string.access_token_secret_key), null);

        AccessToken accessToken = null;
        if (token != null && tokenSecret != null) {
            accessToken = new AccessToken(token, tokenSecret);
        }

        return accessToken;
    }

    private void startTwitterAuth() {
        Log.d(TAG, "start twitter authentication");
        _twitter = new AsyncTwitterFactory().getInstance();
        _twitter.addListener(_listener);
        _twitter.setOAuthConsumer(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        _twitter.getOAuthRequestTokenAsync("sasamifry://twittercallback");
    }
}
