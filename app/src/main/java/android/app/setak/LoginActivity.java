package android.app.setak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.AuthType;
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends AppCompatActivity {

    EditText loginPhoneInput, loginPwInput;
    Button loginBtn, joinButton;
    SessionCallback callback;
    ContentValues values;
    SharedPreferences login;
    private CallbackManager callbackManager;
    LoginButton facebook_login, kakaoLoginBtn;
    ImageView kakao, facebook, naver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        kakao = (ImageView) findViewById(R.id.kakao);
        facebook = (ImageView) findViewById(R.id.facebook);
        /*naver = (ImageView) findViewById(R.id.naver);*/


        // 카카오톡
        kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });
        /*kakaoLoginBtn = (LoginButton) findViewById(R.id.kakaoLoginBtn);*/
        /*findViewById(R.id.kakaoLoginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new KakaoLoginControl(LoginActivity.this).call();
            }
        });*/




        //페이스북
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        facebook_login = findViewById(R.id.facebook_login);
        facebook_login.setReadPermissions("email");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        System.out.print("onSucces LoginResult="+loginResult);
                        System.out.println("LoginResult"+Profile.getCurrentProfile());
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse response) {
                                System.out.println("페이스북 result"+user.toString());

                                try{
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    values = new ContentValues();
                                    values.put("userId",user.getString("name"));
                                    values.put("userEmail", user.getString("email"));
                                    values.put("gender",user.getString("gender"));
                                    values.put("type","faceBook");
                                    values.put("token",token);

                                    String r = new NetworkTask("tokenAdd.app", values).execute().get();
                                    System.out.println("토큰 저장 확인 : "+r);
                                    JSONObject obj = new JSONObject(r);

                                    if(obj.getString("result").equals("newUser")){
                                        // 백그라운드 스레드에서 메인UI를 변경할 경우 사용
                                        login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor autoLogin = login.edit();
                                        autoLogin.clear();

                                        //SharedPreferences 에 로그인정보 저장
                                        autoLogin.putInt("loginUserNo", obj.getInt("userNo"));
                                        autoLogin.putString("loginUserId", obj.getString("userId"));
                                        autoLogin.putString("userGrade",obj.getString("userGrade"));
                                        autoLogin.commit(); //commit 필수

                                        Toast.makeText(LoginActivity.this, user.getString("name")+"님 반갑습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }catch(ExecutionException e){
                                    e.printStackTrace();
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }


                                Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                                startActivity(intent);
                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        // App code
                        System.out.print("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.print("onError");
                    }
                });

        // 페이스북
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebook_login.performClick();
            }
        });

        loginPwInput = (EditText) findViewById(R.id.loginPwInput); //id
        loginPhoneInput = (EditText) findViewById(R.id.loginPhoneInput); //pw
        joinButton = (Button) findViewById(R.id.joinBtn); //회원가입버튼
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
                    values = new ContentValues();
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
                login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = login.edit();
                autoLogin.clear();
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
        callbackManager.onActivityResult(requestCode, resultCode, data);

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
                    System.out.println("카카오img : "+userProfile.getProfileImagePath());
                    System.out.println("카카오썸네일 : "+userProfile.getThumbnailImagePath());
                    String token = FirebaseInstanceId.getInstance().getToken();
                    System.out.println("토큰확인 : "+token);
                    values = new ContentValues();
                    values.put("token",token);
                    values.put("userId",userProfile.getNickname());
                    values.put("type","kakao");

                    try{
                        String r = new NetworkTask("tokenAdd.app", values).execute().get();
                        System.out.println("토큰 저장 확인 : "+r);
                        JSONObject obj = new JSONObject(r);

                        login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor autoLogin = login.edit();
                        if(obj.getString("result").equals("newUser")){
                            // 백그라운드 스레드에서 메인UI를 변경할 경우 사용

                            autoLogin.clear();
                            //SharedPreferences 에 로그인정보 저장
                            autoLogin.putInt("loginUserNo", obj.getInt("userNo"));
                            autoLogin.putString("loginUserId", obj.getString("userId"));
                            autoLogin.putString("userGrade",obj.getString("userGrade"));


                        }
                        autoLogin.putString("userImg", userProfile.getThumbnailImagePath());
                        autoLogin.commit(); //commit 필수
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }catch(ExecutionException e){
                        e.printStackTrace();
                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, userProfile.getNickname()+"님 반갑습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                    startActivity(intent);
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

