package neo.roqkfwk.co.kr.sample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "fcm";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        /** 다음과 같은 경우에 등록 토큰이 변경될 수 있습니다.
         *  앱에서 인스턴스 ID 삭제
         *  새 기기에서 앱 복원
         *  사용자가 앱 삭제/재설치
         *  사용자가 앱 데이터 소거
         */
        Log.d(TAG, "Refreshed token: " + token);

        // OKHTTP를 이용해 웹서버로 토큰값을 날려준다.
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("userId", "8300297")
                .add("type", "new token")
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


    @Override
    public void onMessageReceived(RemoteMessage var1) {

        Log.d(TAG, "getNotification getTitle : " + var1.getNotification().getTitle());
        Log.d(TAG, "getNotification getBody : " + var1.getNotification().getBody());
        Log.d(TAG, "get data : " + var1.getData().get("receive"));

        /*
        try {
            JSONObject json = new JSONObject(var1.getData());
            Log.d(TAG, "json value : " + json.toString());

            Iterator iter = json.keys();
            String key = "";
            while (iter.hasNext()) {
                key = iter.next().toString();
                Log.d(TAG, String.format("key : %s, value : %s", key, json.getString(key)));
            }
        } catch(Exception e){
            Log.d(TAG, "error : " + e.getCause());
        }*/

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "Channel ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(var1.getNotification().getTitle()) // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
                .setContentText(var1.getNotification().getBody());
        mBuilder.setContentIntent(pi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("Channel ID", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(null);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
