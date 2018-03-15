package android.app.setak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class JoinActivity extends AppCompatActivity {
    EditText joinPhoneInput, joinNameInput, joinPwInput, joinPwCheckInput, joinBirthInput, joinFigureInput;
    RadioGroup genderRadio;
    Button joinSubmitBtn;
    CheckBox autoLoginCheck;
    String autoLoginValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        joinPhoneInput = (EditText) findViewById(R.id.joinPhoneInput);
        joinNameInput = (EditText) findViewById(R.id.joinNameInput);
        /*joinPwInput = (EditText) findViewById(R.id.joinPwInput);
        joinPwCheckInput = (EditText) findViewById(R.id.joinPwCheckInput);*/
        genderRadio = (RadioGroup) findViewById(R.id.genderRadio);
        joinSubmitBtn = (Button) findViewById(R.id.joinSubmitBtn);
        autoLoginCheck = (CheckBox) findViewById(R.id.autoLoginCheck);

        autoLoginCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false){
                    Toast.makeText(JoinActivity.this, "자동로그인 기능이 헤제되었습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(JoinActivity.this, "자동로그인 기능이 선택되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        joinSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkRadio = genderRadio.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(checkRadio);
                String phone = joinPhoneInput.getText().toString();
                String name = joinNameInput.getText().toString();

                String genderCheck = rb.getText().toString();
                String gender = "";
                if (genderCheck.equals("남")) {
                    gender = "M";
                } else if (genderCheck.equals("여")) {
                    gender = "F";
                }
                if(autoLoginCheck.isChecked()){
                    autoLoginValue = "checked";
                }else{
                    autoLoginValue = "notCheck";
                }
                if (phone.equals("") || phone == null) {
                    Toast.makeText(JoinActivity.this, "핸드폰번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (phone.replaceAll("-", "").length() != 11) {
                    Toast.makeText(JoinActivity.this, "핸드폰번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (name.equals("") || name == null) {
                    Toast.makeText(JoinActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    phone = phone.replaceAll("-", "");
                    // URL 설정.
                    String token = FirebaseInstanceId.getInstance().getToken();
                    ContentValues values = new ContentValues();
                    values.put("userHp", phone);
                    values.put("userId", name);
                    values.put("gender", gender);
                    values.put("token",token);
                    joinUs(values, autoLoginValue);
                }
            }
        });
    }

    private void joinUs(ContentValues values, String autoLoginValue) {
        try {
            String s = new NetworkTask("join.app", values).execute().get();
            JSONObject obj = new JSONObject(s);
            //로그인결과를 저장할 변수
            final String result = obj.getString("result");
            if (result.equals("exist")) {
                Toast.makeText(JoinActivity.this, "이미 가입된 핸드폰번호입니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("fail")) {
                Toast.makeText(JoinActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(JoinActivity.this, "회원가입성공", Toast.LENGTH_SHORT).show();
                final int loginUserNo = obj.getInt("userNo");
                final String loginUserName = obj.getString("userId");
                final String loginUserPhone = obj.getString("userHp");
                final String userGrade = obj.getString("userGrade");
                // 백그라운드 스레드에서 메인UI를 변경할 경우 사용
                SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = login.edit();
                //SharedPreferences 에 로그인정보 저장
                autoLogin.putInt("loginUserNo", loginUserNo);
                autoLogin.putString("loginUserName", loginUserName);
                autoLogin.putString("loginUserPhone", loginUserPhone);
                autoLogin.putString("userGrade",userGrade);
                autoLogin.commit(); //commit 필수

                //액티비티 전환
                /*CheckBox autoLoginCheck = (CheckBox) findViewById(R.id.autoLoginCheck);*/
                Intent intent;
                if(autoLoginValue == "checked"){
                    intent = new Intent(JoinActivity.this, IndexActivity.class);
                }else{
                    intent = new Intent(JoinActivity.this, LoginActivity.class);
                }
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
