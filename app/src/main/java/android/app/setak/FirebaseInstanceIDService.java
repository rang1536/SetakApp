package android.app.setak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.concurrent.ExecutionException;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
        String loginUserId = login.getString("loginUserName", null);

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if(loginUserId == null){
            loginUserId = "none";
        }
        sendRegistrationToServer(refreshedToken, loginUserId);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String userId) {
        // TODO: Implement this method to send token to your app server.
        //로컬
        /*String url = "http://61.247.71.19:8080/www/tokenAdd.app";*/

        //웹서버
        /*String url = "http://seapp.cafe24.com/tokenAdd.app";*/

        ContentValues values = new ContentValues();
        values.put("token",token);
        values.put("userId",userId);
        Log.d(TAG, "되냐마냐 " + values);
        try{
            String s = new NetworkTask("tokenAdd.app",values).execute().get();
            Log.d(TAG, "되냐마냐 " + s);
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }




    }

}


