package android.app.setak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {

    String loginUserName;
    Intent intent;
    SharedPreferences login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ITmfCN40UczroE7XIrYLWm5uY0g=
        /*getHashKey();*/
        /*String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_Token", token);*/
         //화면 터치 이벤트
        ConstraintLayout screen = (ConstraintLayout) findViewById(R.id.screen);
        screen.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                /*login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                loginUserName = login.getString("loginUserName", null);*/
                loginUserName = null;
                if (loginUserName != null) { //저장된 정보가 있으면 액티비티 전환
                    Toast.makeText(MainActivity.this, loginUserName + "님 자동로그인 입니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, IndexActivity.class);
                }else{
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }

                startActivity(intent); //액티비티 이동

                return true;
            }
        });

    }

    private void getHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("android.app.setak", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("key_hash="+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
