package android.app.setak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.app.setak.R.id.joinBtn;

public class LoginActivity extends AppCompatActivity {

    EditText loginPhoneInput, loginPwInput;
    Button loginBtn, joinButton;
    SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        loginPwInput = (EditText) findViewById(R.id.loginPwInput); //id
        loginPhoneInput = (EditText) findViewById(R.id.loginPhoneInput); //pw
        joinButton = (Button) findViewById(joinBtn); //회원가입버튼
        loginBtn = (Button) findViewById(R.id.loginSubmitBtn);

        loginBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = loginPhoneInput.getText().toString();
                phone = phone.replaceAll("-", "");
                String pw = loginPwInput.getText().toString();
                if (phone.equals("") || phone == null) {
                    Toast.makeText(LoginActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (pw.equals("") || pw == null) {
                    Toast.makeText(LoginActivity.this, "휴대폰번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put("phone", phone);
                    values.put("pw", pw);
                    loginUs(values);
                }
            }
        });

        joinButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(joinIntent);
            }
        });
    }

    private void loginUs(ContentValues values) {
        try {
            String s = new NetworkTask("login.app", values).execute().get();
            JSONObject obj = new JSONObject(s);
            //로그인결과를 저장할 변수
            final String result = obj.getString("result");
            if (result.equals("noId")) {
                Toast.makeText(LoginActivity.this, "입력하신 이름으로 가입된 회원이 없습니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("noPw")) {
                Toast.makeText(LoginActivity.this, "휴대폰번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                String token = FirebaseInstanceId.getInstance().getToken();
                System.out.println("토큰확인 : "+token);
                values.put("token",token);
                values.put("userNo",obj.getInt("loginUserNo"));

                String r = new NetworkTask("tokenAdd.app", values).execute().get();
                System.out.println("토큰 저장 확인 : "+r);

                final int loginUserNo = obj.getInt("loginUserNo");
                final String loginUserId = obj.getString("loginUserId");
                final String loginUserPhone = obj.getString("loginUserHp");
                final String userGrade = obj.getString("userGrade");
                // 백그라운드 스레드에서 메인UI를 변경할 경우 사용
                SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = login.edit();
                //SharedPreferences 에 로그인정보 저장
                autoLogin.putInt("loginUserNo", loginUserNo);
                autoLogin.putString("loginUserId", loginUserId);
                autoLogin.putString("loginUserPhone", loginUserPhone);
                autoLogin.putString("userGrade",userGrade);
                autoLogin.commit(); //commit 필수

                //액티비티 전환
                Toast.makeText(LoginActivity.this, loginUserId+"님 반갑습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                startActivity(intent);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
//                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

//                    Log.e("UserProfile", userProfile.toString());
//                    Log.e("UserProfile", userProfile.getId() + "");

                    System.out.println("카카오로그인성공");
                    long number = userProfile.getId();


                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ

        }
    }

}