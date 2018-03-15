package android.app.setak;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.app.Fragment;

public class ListActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    Fragment myReserveBookingFragment;
    Fragment myReservePackageFragment;

    RelativeLayout my_reserve_bookingTab, my_reserve_packageTab;
    RelativeLayout my_reserve_booking_content, my_reserve_package_content;

    ImageView my_reserve_bookingTabImg, my_reserve_packageTabImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.custom_bar2, null);

        actionBar.setCustomView(actionbarView);

        my_reserve_bookingTabImg = (ImageView) findViewById(R.id.my_reserve_bookingTabImg);
        my_reserve_packageTabImg = (ImageView) findViewById(R.id.my_reserve_packageTabImg);

        my_reserve_bookingTab = (RelativeLayout) findViewById(R.id.my_reserve_bookingTab);
        my_reserve_packageTab = (RelativeLayout) findViewById(R.id.my_reserve_packageTab);
        my_reserve_booking_content = (RelativeLayout) findViewById(R.id.my_reserve_booking_content);
        my_reserve_booking_content.setVisibility(View.VISIBLE);
        my_reserve_bookingTabImg.setSelected(true);
        my_reserve_package_content = (RelativeLayout) findViewById(R.id.my_reserve_package_content);

        my_reserve_bookingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_reserve_booking_content.getVisibility() == View.VISIBLE) {
                    my_reserve_booking_content.setVisibility(View.GONE);
                    my_reserve_bookingTabImg.setSelected(false);
                } else {
                    my_reserve_booking_content.setVisibility(View.VISIBLE);
                    my_reserve_bookingTabImg.setSelected(true);
                }
            }
        });
        my_reserve_packageTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_reserve_package_content.getVisibility() == View.VISIBLE) {
                    my_reserve_package_content.setVisibility(View.GONE);
                    my_reserve_packageTabImg.setSelected(false);
                } else {
                    my_reserve_package_content.setVisibility(View.VISIBLE);
                    my_reserve_packageTabImg.setSelected(true);
                }
            }
        });

        fragmentManager = getFragmentManager();

        myReserveBookingFragment = new MyReserveBookingFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.my_reserve_booking_content, myReserveBookingFragment);
        fragmentTransaction.commit();

        myReservePackageFragment = new MyReservePackageFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.my_reserve_package_content, myReservePackageFragment);
        fragmentTransaction.commit();
    }
}
