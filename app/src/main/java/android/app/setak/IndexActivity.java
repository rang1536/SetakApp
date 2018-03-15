package android.app.setak;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class IndexActivity extends AppCompatActivity {

    /*ImageButton mainmenu1, mainmenu2;*/
    ImageButton mainmenu1, mainmenu2;
    Intent intent;
    String userGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /*mainmenu1 = (ImageButton) findViewById(R.id.mainmenu1);
        mainmenu2 = (ImageButton) findViewById(R.id.mainmenu2); */
        mainmenu1 = (ImageButton) findViewById(R.id.mainmenu1);
        mainmenu2 = (ImageButton) findViewById(R.id.mainmenu2);

        SharedPreferences login = getSharedPreferences("login", Context.MODE_PRIVATE);
        userGrade = login.getString("userGrade", null);

        //세탁확인
        mainmenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userGrade.equals("staff")){
                    intent = new Intent(IndexActivity.this, android.app.setak.StaffActivity.class);
                }else if(userGrade.equals("user")){
                    intent = new Intent(IndexActivity.this, android.app.setak.ListActivity.class);
                }
                /*intent.putExtra("menuSelected", "checkList");*/
                startActivity(intent);
            }
        });

        //세탁신청
        mainmenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(IndexActivity.this, OrderActivity.class);
                /*intent.putExtra("menuSelected", "addOrder");*/
                startActivity(intent);
            }
        });
    }
}
