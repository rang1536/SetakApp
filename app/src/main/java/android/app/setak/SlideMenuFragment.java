package android.app.setak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class SlideMenuFragment extends Fragment {

    TextView slide_loginNameText;
    RelativeLayout slide_logoutLayout, slide_loginLayout;
    Button slide_loginButton, slide_joinButton;
    RelativeLayout slide_myPageButton, slide_menu1Button, slide_menu2Button, slide_menu3Button, slide_logoutButton, slide_adminButton;
    String loginUserId, loginUserHp;
    SharedPreferences login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide_menu, null);

        slide_logoutLayout = (RelativeLayout) view.findViewById(R.id.slide_logoutLayout);
        slide_loginLayout = (RelativeLayout) view.findViewById(R.id.slide_loginLayout);
        slide_loginButton = (Button) view.findViewById(R.id.slide_loginButton);
        slide_joinButton = (Button) view.findViewById(R.id.slide_joinButton);
        slide_loginNameText = (TextView) view.findViewById(R.id.slide_loginNameText);

        slide_myPageButton = (RelativeLayout) view.findViewById(R.id.slide_myPageButton);
        slide_menu1Button = (RelativeLayout) view.findViewById(R.id.slide_menu1Button);
        slide_menu2Button = (RelativeLayout) view.findViewById(R.id.slide_menu2Button);
        slide_menu3Button = (RelativeLayout) view.findViewById(R.id.slide_menu3Button);
        slide_logoutButton = (RelativeLayout) view.findViewById(R.id.slide_logoutButton);
        slide_adminButton = (RelativeLayout) view.findViewById(R.id.slide_adminButton);

        //로그인 체크
        login = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        loginUserId = login.getString("loginUserId", null);
        if (loginUserId != null) { //저장된 정보가 있으면 액티비티 전환
            slide_loginNameText.setText(loginUserId);
            slide_logoutLayout.setVisibility(View.GONE);
            slide_loginLayout.setVisibility(View.VISIBLE);
            loginUserHp = login.getString("loginUserHp", null);
            if (loginUserHp.equals("01051478323") || loginUserHp.equals("01039002846") || loginUserHp.equals("01038390401")) {
                slide_adminButton.setVisibility(View.VISIBLE);
            }
        } else {
            slide_loginLayout.setVisibility(View.GONE);
            slide_adminButton.setVisibility(View.GONE);
            slide_logoutLayout.setVisibility(View.VISIBLE);
        }

        slide_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().onBackPressed();
                startActivity(intent);
            }
        });

        slide_joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), JoinActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().onBackPressed();
                startActivity(intent);
            }
        });

       /* slide_logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "로그아웃", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = login.edit();
                editor.clear();
                editor.commit();
                getActivity().onBackPressed();
                UtilActivityList.endLoginActivity();
            }
        });*/

        /*slide_myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), MyPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().onBackPressed();
                startActivity(intent);
            }
        });
        slide_menu1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), MyReserveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().onBackPressed();
                startActivity(intent);
            }
        });*/

        /*slide_menu2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), UserInquireDetail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().onBackPressed();
                startActivity(intent);
            }
        });

        *//*쿠폰목록*//*
        slide_menu3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), UserCouponDetail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().onBackPressed();
                startActivity(intent);
                Toast.makeText(getActivity(), "보유쿠폰 확인중..", Toast.LENGTH_SHORT).show();
            }
        });*/

        //관리자페이지
       /* slide_adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), AdminLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getActivity().onBackPressed();
                startActivity(intent);
                getActivity().onBackPressed();
            }
        });*/
        return view;
    }
}