package android.app.setak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    String loginUserName;
    Intent intent;
    SharedPreferences login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_Token", token);

        
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
}
