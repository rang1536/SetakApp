package android.app.setak;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class StaffActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    Fragment reserveFragment;
    RelativeLayout reserve_bookingTab, reserve_packageTab;
    RelativeLayout reserve_booking_content, reserve_package_content;

    ImageView reserve_bookingTabImg, reserve_packageTabImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.custom_bar2, null);

        actionBar.setCustomView(actionbarView);

        reserve_bookingTabImg = (ImageView) findViewById(R.id.reserve_bookingTabImg);

        reserve_bookingTab = (RelativeLayout) findViewById(R.id.reserve_bookingTab);
        reserve_booking_content = (RelativeLayout) findViewById(R.id.reserve_booking_content);
        reserve_booking_content.setVisibility(View.VISIBLE);
        reserve_bookingTabImg.setSelected(true);

        reserve_bookingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reserve_booking_content.getVisibility() == View.VISIBLE) {
                    reserve_booking_content.setVisibility(View.GONE);
                    reserve_bookingTabImg.setSelected(false);
                } else {
                    reserve_booking_content.setVisibility(View.VISIBLE);
                    reserve_bookingTabImg.setSelected(true);
                }
            }
        });


        fragmentManager = getFragmentManager();

        reserveFragment = new ReserveFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reserve_booking_content, reserveFragment);
        fragmentTransaction.commit();

    }
}
