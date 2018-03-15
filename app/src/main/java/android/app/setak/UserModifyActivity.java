package android.app.setak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class UserModifyActivity extends AppCompatActivity {

    EditText modify_phoneInput, modify_nameInput;
    RadioGroup modify_genderRadio;
    RadioButton modify_genderOption1, modify_genderOption2;
    Button modifySubmitBtn;
    String loginUserId, loginUserHp;
    int loginUserNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modify);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.custom_bar2, null);

        actionBar.setCustomView(actionbarView);

        modify_phoneInput = (EditText) findViewById(R.id.modify_phoneInput);
        modify_nameInput = (EditText) findViewById(R.id.modify_nameInput);
        modify_genderRadio = (RadioGroup) findViewById(R.id.modify_genderRadio) ;
        modify_genderOption1 = (RadioButton) findViewById(R.id.modify_genderOption1) ;
        modify_genderOption2 = (RadioButton) findViewById(R.id.modify_genderOption2) ;
        modifySubmitBtn = (Button) findViewById(R.id.modifySubmitBtn);

        SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
        loginUserId = login.getString("loginUserId", null);
        loginUserHp = login.getString("loginUserHp", null);
        loginUserNo = login.getInt("loginUserNo",0);

        if(loginUserHp != null){
            modify_phoneInput.setText(loginUserHp);
        }
        modify_nameInput.setText(loginUserId);

        modifySubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkRadio = modify_genderRadio.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(checkRadio);
                String phone = modify_phoneInput.getText().toString();
                String name = modify_nameInput.getText().toString();

                String genderCheck = rb.getText().toString();
                String gender = "";
                if (genderCheck.equals("남")) {
                    gender = "M";
                } else if (genderCheck.equals("여")) {
                    gender = "F";
                }

                if (phone.equals("") || phone == null) {
                    Toast.makeText(UserModifyActivity.this, "핸드폰번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (phone.replaceAll("-", "").length() != 11) {
                    Toast.makeText(UserModifyActivity.this, "핸드폰번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (name.equals("") || name == null) {
                    Toast.makeText(UserModifyActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    phone = phone.replaceAll("-", "");
                    // URL 설정.
                    ContentValues values = new ContentValues();
                    values.put("userHp", phone);
                    values.put("userId", name);
                    values.put("gender", gender);
                    values.put("userNo",loginUserNo);
                    modifyUs(values);
                }
            }
        });
    }

    private void modifyUs(ContentValues values) {
        try {
            String s = new NetworkTask("userModify.app", values).execute().get();
            JSONObject obj = new JSONObject(s);
            //로그인결과를 저장할 변수
            final String result = obj.getString("result");
            if (result.equals("exist")) {
                Toast.makeText(UserModifyActivity.this, "이미 가입된 핸드폰번호입니다.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("fail")) {
                Toast.makeText(UserModifyActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserModifyActivity.this, "정보를 수정하였습니다", Toast.LENGTH_SHORT).show();
                /*final int loginUserNo = obj.getInt("userNo");
                final String loginUserName = obj.getString("userId");
                final String loginUserPhone = obj.getString("userHp");
                final String userGrade = obj.getString("userGrade");*/
                // 백그라운드 스레드에서 메인UI를 변경할 경우 사용
                SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = login.edit();
                //SharedPreferences 에 로그인정보 저장
                autoLogin.putInt("loginUserNo", loginUserNo);
                autoLogin.putString("loginUserName", loginUserId);
                autoLogin.putString("loginUserPhone", loginUserHp);
                autoLogin.commit(); //commit 필수

                //액티비티 전환
                Intent intent = new Intent(UserModifyActivity.this, IndexActivity.class);
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
