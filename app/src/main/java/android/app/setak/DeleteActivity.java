package android.app.setak;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class DeleteActivity extends AppCompatActivity {

    Button user_remove_btn;
    EditText user_delete_hp;
    String userHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.custom_bar2, null);

        actionBar.setCustomView(actionbarView);

        user_delete_hp = (EditText) findViewById(R.id.user_delete_hp);
        user_remove_btn = (Button) findViewById(R.id.user_remove_btn);
        user_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                userHp = user_delete_hp.getText().toString();
                System.out.println("삭제");
                if(userHp == null || userHp.equals("")){
                    Toast.makeText(DeleteActivity.this, "휴대폰번호를 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                new AlertDialog.Builder(DeleteActivity.this)
                    .setMessage("정말 회원탈퇴를 하시겠습니까?")
                    .setPositiveButton("탈퇴",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                        // 기본적으로 창은 닫히고 추가 작업
                            SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                            int loginUserNo = login.getInt("loginUserNo", 0);

                            ContentValues values = new ContentValues();
                            values.put("userNo", loginUserNo);
                            values.put("userHp", userHp);
                            try{
                                String result = new NetworkTask("userDelete.app", values).execute().get();
                                JSONObject deleteCheck = new JSONObject(result);

                                if(deleteCheck.getString("result").equals("success")){
                                    //액티비티 이동
                                    SharedPreferences.Editor autoLogin = login.edit();
                                    autoLogin.clear();

                                    Intent intent = new Intent(DeleteActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getApplicationContext(), "신청에 실패하였습니다. 고객센터로 문의바랍니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DeleteActivity.this, MypageActivity.class);
                                    startActivity(intent);
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                .show();
            }
        });
    }
}
