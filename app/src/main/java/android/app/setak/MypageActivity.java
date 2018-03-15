package android.app.setak;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MypageActivity extends AppCompatActivity implements Button.OnClickListener {

    LinearLayout my_page_updateBtn, my_page_setakListBtn, my_page_centerBtn, my_page_deleteBtn;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.custom_bar2, null);

        actionBar.setCustomView(actionbarView);

        TextView nameText = (TextView) findViewById(R.id.my_page_name_text);
        ImageView userImgView = (ImageView) findViewById(R.id.myPageUserImg);

        SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
        String loginUserId = login.getString("loginUserId", null);
        String userImg = login.getString("userImg",null);

        nameText.setText(loginUserId);

        if(userImg != null){
            Picasso.with(getApplicationContext()).load(userImg).resize(300,0).transform(new CircleTransform()).into(userImgView);
        }

        my_page_updateBtn = (LinearLayout) findViewById(R.id.my_page_updateBtn);
        my_page_updateBtn.setOnClickListener(this);

        my_page_setakListBtn = (LinearLayout) findViewById(R.id.my_page_setakListBtn);
        my_page_setakListBtn.setOnClickListener(this);

        my_page_centerBtn = (LinearLayout) findViewById(R.id.my_page_centerBtn);
        my_page_centerBtn.setOnClickListener(this);

        my_page_deleteBtn = (LinearLayout) findViewById(R.id.my_page_deleteBtn);
        my_page_deleteBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.my_page_updateBtn :
                //회원정보수정으로 이동
                Toast.makeText(this, "개인정보수정 페이지로 이동합니다", Toast.LENGTH_SHORT).show();
                intent = new Intent(MypageActivity.this, UserModifyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_page_setakListBtn :
                //세탁목록으로 이동
                Toast.makeText(this, "세탁중목록 페이지로 이동합니다", Toast.LENGTH_SHORT).show();
                intent = new Intent(MypageActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            case R.id.my_page_centerBtn :
                //고객센터로 이동
                Toast.makeText(this, "고객센터 페이지로 이동합니다", Toast.LENGTH_SHORT).show();
                intent = new Intent(MypageActivity.this, CenterActivity.class);
                startActivity(intent);
                break;
            case R.id.my_page_deleteBtn :
                //회원탈퇴으로 이동
                Toast.makeText(this, "회원탈퇴 페이지로 이동합니다", Toast.LENGTH_SHORT).show();
                intent = new Intent(MypageActivity.this, DeleteActivity.class);
                startActivity(intent);
                break;
        }
    }

}
