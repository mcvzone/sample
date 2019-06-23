package neo.roqkfwk.co.kr.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "fcm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String url = "https://m.axa.co.kr";
        //SSLConnect ssl = new SSLConnect();
        //ssl.postHttps(url, 1000, 1000);
        //new AppPushTask().execute(true, null, null);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Log.d(TAG, "Current token : " + token);

                        new AppPushTask().execute(token, null, null);

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        //Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("ALL");
    }

    private class AppPushTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... token) {
            //String url = "https://m.axa.co.kr/AjaxMoActionControler.action?screenID=SMOM0019&actionID=U01";
            //String result = getHttpHTML_POST(url);
            //Log.d("test", "result : " + result);

            sendRegistrationToServer(token[0]);
            return null;
        }
    }

    private static String getHttpHTML_POST(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader br;
        BufferedWriter bw;
        String line;
        String result = "";
        String parameter = String.format("param1=%s¶m2=%s",URLEncoder.encode("param1"),URLEncoder.encode("param2"));

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //파라메터가 없으면 지워주면 된다.
            bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            bw.write(parameter);
            //요기까지

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
//          br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"euc-kr"));
            while ((line = br.readLine()) != null)
                result += line + "\n";
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        // OKHTTP를 이용해 웹서버로 토큰값을 날려준다.
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("userId", "8300297")
                .build();

        //request : http://localhost/rest/common/api/1
        Request request = new Request.Builder()
                .url("http://192.168.43.73/rest/common/api/2")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
