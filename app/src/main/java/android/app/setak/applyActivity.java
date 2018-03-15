package android.app.setak;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class applyActivity extends AppCompatActivity {
    String setakListText;
    String loginUserId, loginUserPhone, userHp, sameAddCheck, userImg;
    int loginUserNo;
    Intent intent;
    SharedPreferences login;
    int totalPrice;
    Dialog dialogWeb;
    WebView webView;
    Handler handler;
    EditText apply_add, apply_delivery_add, apply_sangseAdd, apply_name, apply_phone, apply_comment;
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    LinearLayout ll_delivery, slidingPage02;
    ArrayList<OrderItem> orderList;
    CheckBox apply_sameAdd_check;
    //슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;
    User user, userInfo;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.custom_bar, null);

        actionBar.setCustomView(actionbarView);

        //UI
        slidingPage02 = (LinearLayout) findViewById(R.id.slidingPage02);

        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //애니메이션 리스너 설정
        applyActivity.SlidingPageAnimationListener animationListener = new applyActivity.SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);

        /*getSupportActionBar().getCustomView().findViewById(R.id.actionBackBtn).setVisibility(View.GONE);
        getSupportActionBar().getCustomView().findViewById(R.id.actionMenuBtn).setVisibility(View.GONE);*/
        actionBar.getCustomView().findViewById(R.id.actionBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
        actionBar.getCustomView().findViewById(R.id.actionLogoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //인덱스로?
            }
        });
        actionBar.getCustomView().findViewById(R.id.actionMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //닫기
                if(isPageOpen){
                    //애니메이션 시작
                    slidingPage02.startAnimation(translateRightAnim);
                }
                //열기
                else{
                    slidingPage02.setVisibility(View.VISIBLE);
                    slidingPage02.startAnimation(translateLeftAnim);
                }
            }
        });

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbarView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        final Intent intent = getIntent();
        orderList = (ArrayList<OrderItem>)intent.getSerializableExtra("orderResultList");

        System.out.println("주문확인 : "+orderList);

        TextView setakList = (TextView) findViewById(R.id.setakList);
        apply_name = (EditText) findViewById(R.id.apply_name);
        apply_phone = (EditText) findViewById(R.id.apply_phone);
        Button apply_search_addBtn = (Button) findViewById(R.id.apply_search_addBtn);
        apply_add = (EditText) findViewById(R.id.apply_add);
        apply_sangseAdd = (EditText) findViewById(R.id.apply_sangseAdd);
        apply_sameAdd_check = (CheckBox) findViewById(R.id.apply_sameAdd_check);
        apply_delivery_add = (EditText) findViewById(R.id.apply_delivery_add);
        apply_comment = (EditText) findViewById(R.id.apply_comment);
        TextView apply_totalPrice = (TextView) findViewById(R.id.apply_totalPrice);
        Button applyBtn = (Button) findViewById(R.id.applyBtn);
        ll_delivery = (LinearLayout) findViewById(R.id.ll_delivery);

        ll_delivery.setVisibility(View.GONE);
        for(int i=0; i<orderList.size();i++){
            if(i==0){
                setakListText = orderList.get(i).getName() + "("+orderList.get(i).getOrderCount()+"벌)";
                totalPrice = Integer.parseInt(orderList.get(i).getPrice()) * orderList.get(i).getOrderCount();
            }else{
                setakListText += " , "+orderList.get(i).getName() + "("+orderList.get(i).getOrderCount()+"벌)";
                totalPrice += Integer.parseInt(orderList.get(i).getPrice()) * orderList.get(i).getOrderCount();
            }
        }
        setakList.setText(setakListText); //세탁목록 세팅
        //회원정보세팅. 쉐어드프리퍼런스에서 아이디 불러와서 DB에서 조회 혹은 로그인시 아예 저장.
        login = getSharedPreferences("login", Activity.MODE_PRIVATE);
        loginUserId = login.getString("loginUserId", null);
        loginUserPhone = login.getString("loginUserPhone", null);
        loginUserNo = login.getInt("loginUserNo",0);
        userImg = login.getString("userImg",null);

        userInfo = getUser(loginUserNo);
        if(userInfo.getUserAdd() != null){
            apply_add.setText(userInfo.getUserAdd());

        }

        //회원정보세팅. 쉐어드프리퍼런스에서 아이디 불러와서 DB에서 조회 혹은 로그인시 아예 저장.
        ImageView userImgView = (ImageView) findViewById(R.id.userImg1);
        TextView nameText = (TextView) findViewById(R.id.slide_loginNameText1);
        nameText.setText(loginUserId);
        if(userImg != null){
            Picasso.with(getApplicationContext()).load(userImg).resize(300,0).transform(new CircleTransform()).into(userImgView);
        }

        //사이드바 메뉴 인텐트 이동
        RelativeLayout myPageBtn = (RelativeLayout) findViewById(R.id.slide_myPageButton1);
        RelativeLayout menu1Btn = (RelativeLayout) findViewById(R.id.slide_menu1Button1);
        RelativeLayout menu2Btn = (RelativeLayout) findViewById(R.id.slide_menu2Button1);

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //마이페이지로 이동
                Intent intent = new Intent(applyActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });

        menu1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //세탁목록으로 이동
                Intent intent = new Intent(applyActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        menu2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //고객센터로 이동
                Intent intent = new Intent(applyActivity.this, CenterActivity.class);
                startActivity(intent);
            }
        });



        System.out.print("이름, 핸드폰번호 확인 : "+loginUserId+", "+loginUserPhone);
        //핸드폰번호 - 추가 하기
        if(loginUserPhone != null){
            if(loginUserPhone.length() == 10){
                userHp = loginUserPhone.substring(0,3) + "-" + loginUserPhone.substring(3, 6)+"-"+loginUserPhone.substring(6, loginUserPhone.length());
                apply_phone.setText(userHp);
            }else if(loginUserPhone.length() == 11){
                userHp = loginUserPhone.substring(0,3) + "-" + loginUserPhone.substring(3, 7)+"-"+loginUserPhone.substring(7, loginUserPhone.length());
                apply_phone.setText(userHp);
            }
        }
        if(userInfo.getUserHp() != null && loginUserPhone == null){
            if(userInfo.getUserHp().length() == 10){
                userHp = userInfo.getUserHp().substring(0,3) + "-" + userInfo.getUserHp().substring(3, 6)+"-"+userInfo.getUserHp().substring(6, userInfo.getUserHp().length());
                apply_phone.setText(userHp);
            }else if(userInfo.getUserHp().length() == 11){
                userHp = userInfo.getUserHp().substring(0,3) + "-" + userInfo.getUserHp().substring(3, 7)+"-"+userInfo.getUserHp().substring(7, userInfo.getUserHp().length());
                apply_phone.setText(userHp);
            }
        }

        //총가격 계산
        apply_name.setText(loginUserId);
        /*apply_phone.setText(userHp);*/
        apply_sameAdd_check.setChecked(true);
        apply_totalPrice.setText(String.format("%,d", totalPrice)+"원");

        // 주소찾기
        apply_search_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("주소찾기");
                Intent i = new Intent(applyActivity.this, Daum.class);
                startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);

            }
        });

        apply_sameAdd_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false){
                    ll_delivery.setVisibility(View.VISIBLE);
                    apply_delivery_add.setText("");
                    Toast.makeText(applyActivity.this, "배송지를 입력하세요", Toast.LENGTH_SHORT).show();
                }else{
                    if(userInfo.getUserAdd() != null) {
                        apply_delivery_add.setText(userInfo.getUserAdd());
                    }
                    ll_delivery.setVisibility(View.GONE);
                }
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //유효성검사
                if(apply_name.getText().toString() == null || apply_name.getText().toString() == ""){
                    Toast.makeText(applyActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }else if(apply_phone .getText().toString() == null || apply_phone .getText().toString() == ""){
                    Toast.makeText(applyActivity.this, "휴대폰번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }else if(apply_add .getText().toString() == null || apply_add .getText().toString() == ""){
                    Toast.makeText(applyActivity.this, "주소를 입력하세요", Toast.LENGTH_SHORT).show();
                }else if(apply_sangseAdd .getText().toString() == null || apply_sangseAdd .getText().toString() == ""){
                    Toast.makeText(applyActivity.this, "상세주소를 입력하세요", Toast.LENGTH_SHORT).show();
                }else if(apply_delivery_add .getText().toString() == null || apply_delivery_add .getText().toString() == ""){
                    Toast.makeText(applyActivity.this, "배송지를 입력하세요", Toast.LENGTH_SHORT).show();
                }

                ContentValues params = new ContentValues();
                if(apply_sameAdd_check.isChecked()){
                    sameAddCheck = "checked";
                }else{
                    sameAddCheck = "notCheck";
                }

                params.put("userNo", loginUserNo);
                params.put("userId",apply_name.getText().toString());
                params.put("userHp",apply_phone.getText().toString());
                params.put("orderAdd",apply_add.getText().toString());
                params.put("sangseAdd",apply_sangseAdd.getText().toString());
                params.put("deliveryAdd",apply_delivery_add.getText().toString());
                params.put("orderList",orderList.toString());
                params.put("sameAddCheck",sameAddCheck);
                if(apply_comment.getText().toString() != null && apply_comment.getText().toString() != ""){
                    params.put("comment", apply_comment.getText().toString());
                }

                try{
                    String result = new NetworkTask("applyOrder.app", params).execute().get();
                    JSONObject applyCheck = new JSONObject(result);

                    if(applyCheck.getString("result").equals("success")){
                        Toast.makeText(applyActivity.this, "신청이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                        //액티비티 이동
                        Intent nextIntent = new Intent(applyActivity.this, IndexActivity.class);
                        startActivity(nextIntent);

                    }else{
                        Toast.makeText(applyActivity.this, "신청에 실패하였습니다. 고객센터로 문의바랍니다", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case SEARCH_ADDRESS_ACTIVITY:
                if(resultCode == RESULT_OK){
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        apply_add.setText(data);
                        apply_delivery_add.setText(String.format(data));
                    }
                }
                break;
        }
    }

    //애니메이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if(isPageOpen){
                slidingPage02.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else{
                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {

        }
    }

    //회원정보조회
    public User getUser(int userNo){
        ContentValues values = new ContentValues();
        values.put("userNo", userNo);
        try{
            String result = new NetworkTask("getUser.app", values).execute().get();
            System.out.println("DB리턴값 확인 : "+result);
            JSONObject obj2 = new JSONObject(result);
            System.out.println("json확인 : "+obj2);
            JSONObject obj = obj2.getJSONObject("user");
            user = new User();
            if(obj.getString("userAdd") != null){
                user.setUserAdd(obj.getString("userAdd"));
            }
            if(obj.getString("sangseAdd") != null){
                user.setSangseAdd(obj.getString("sangseAdd"));
            }
            if(obj.getString("userHp") != null){
                user.setUserHp(obj.getString("userHp"));
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
