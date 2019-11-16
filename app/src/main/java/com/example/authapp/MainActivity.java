//sudo chown user:user /dev/kvm
//chmod -R u+rwx /dev/kvm

package com.example.authapp;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

// В данном приложении вход /sign_in, выход - /sign_out
public class MainActivity extends AppCompatActivity {
    private final String USERNAME = "username";
    private final String NEWUSERNAME = "new-username";
    private final String PASSWORD = "pass";
    private final String BASE_URL_SERVER = "https://php-api-for-andriod-app.herokuapp.com";
    private String URL_SERVER;
    private EditText username_et;
    private EditText password_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username_et = (EditText) findViewById(R.id.editTextUsername);
        password_et = (EditText) findViewById(R.id.editTextPassword);
    }
    // Кнопка Войти
    public void goEnter(View view) {
        new AsyncRequest().execute(username_et.getText().toString(),
                password_et.getText().toString(), USERNAME);
    }
    // Кнопка Регистрация
    public void newUser(View view) {
        new AsyncRequest().execute(username_et.getText().toString(),
                password_et.getText().toString(), NEWUSERNAME);
    }
    public void showResults(String s) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("json", s);
        startActivity(intent);
    }

    class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            HashMap<String, String> hm = new HashMap<>();
            if (arg[2].equals(USERNAME)) {
                hm.put(USERNAME, arg[0]);
                hm.put(PASSWORD, arg[1]);
                URL_SERVER = BASE_URL_SERVER + "/sign_in";
            } else {
                hm.put(USERNAME, arg[0]);
                hm.put(PASSWORD, arg[1]);
                URL_SERVER = BASE_URL_SERVER + "/sign_up";
            }
            return performPostCall(hm);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String wrong = "wrong password or username";
            String userExists = "user with this login already exists";
            if (s.equals(wrong))
                Toast.makeText(MainActivity.this, wrong, Toast.LENGTH_LONG).show();
            else
                if (s.equals(userExists))
                    Toast.makeText(MainActivity.this, userExists, Toast.LENGTH_LONG).show();
                else
                    showResults(s);
        }
        public String performPostCall(HashMap<String, String> postDataParams) {
            URL url;
            String response = "";
            try {
                url = new URL(URL_SERVER);
                Log.e("URL ", URL_SERVER);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "Err";
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = e.toString();
            }

            return response;
        }
        private String getPostDataString(HashMap<String, String> params) throws
                UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
