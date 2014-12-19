package com.example.loic.formationandroid;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loic.formationandroid.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    private static final String TAG = MainActivity.class.getName();

    private Button send;
    private Button empty;

    private EditText username;
    private EditText password;

    private ProgressBar progressBar;

    private LoginTask loginTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        send = (Button) findViewById(R.id.sendButton);
        empty = (Button) findViewById(R.id.emptyButton);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        empty.setOnClickListener(this);
        send.setOnClickListener(this);

        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        if(loginTask != null) {
            loginTask.cancel(true);
            progressBar.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                if (loginTask != null && loginTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    loginTask.cancel(true);
                }
                loginTask = new LoginTask(username.getText().toString(), password.getText().toString());
                loginTask.execute();
                break;
            case R.id.emptyButton:
                username.setText("");
                password.setText("");
                break;
        }
    }


    private class LoginTask extends AsyncTask<Void, Void, Boolean> {


        private String URL = "http://dev.loicortola.com/parlez-vous-android/connect";

        private Response jsonResponse;
        private String username;
        private String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();

            HttpGet request = new HttpGet(new StringBuilder(URL)
                    .append("/")
                    .append(username)
                    .append("/")
                    .append(password)
                    .toString());

            ObjectMapper mapper = new ObjectMapper();

            try {

                HttpResponse response = client.execute(request);

                InputStream content = response.getEntity().getContent();

                jsonResponse = mapper.readValue(content, Response.class);

                if (jsonResponse.getStatus() == HttpStatus.SC_OK) {
                    return true;
                }
                return false;

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            progressBar.setVisibility(View.GONE);

            if (jsonResponse != null && jsonResponse.getMessage() != null) {
                Toast.makeText(MainActivity.this, jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }
}
