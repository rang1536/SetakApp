package android.app.setak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.ListBtnClickListener {

    String cateItem;
    OrderAdapter orderAdapter;
    TextView orderCountText;
    ListView listView;
    //슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;

    LinearLayout slidingPage01;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_bar, null);

        actionBar.setCustomView(actionbar);

        //UI
        slidingPage01 = (LinearLayout)findViewById(R.id.slidingPage01);

        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);

        getSupportActionBar().getCustomView().findViewById(R.id.actionBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
        getSupportActionBar().getCustomView().findViewById(R.id.actionLogoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //인덱스로?
            }
        });
        getSupportActionBar().getCustomView().findViewById(R.id.actionMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //닫기
                if(isPageOpen){
                    //애니메이션 시작
                    slidingPage01.startAnimation(translateRightAnim);
                }
                //열기
                else{
                    slidingPage01.setVisibility(View.VISIBLE);
                    slidingPage01.startAnimation(translateLeftAnim);
                }
            }
        });

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);

        //회원정보세팅. 쉐어드프리퍼런스에서 아이디 불러와서 DB에서 조회 혹은 로그인시 아예 저장.
        TextView nameText = (TextView) findViewById(R.id.slide_loginNameText);

        SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
        String loginUserId = login.getString("loginUserId", null);

        nameText.setText(loginUserId);
       /* actionBar.hide();*/

        //사이드바 메뉴 인텐트 이동
        RelativeLayout myPageBtn = (RelativeLayout) findViewById(R.id.slide_myPageButton);
        RelativeLayout menu1Btn = (RelativeLayout) findViewById(R.id.slide_menu1Button);
        RelativeLayout menu2Btn = (RelativeLayout) findViewById(R.id.slide_menu2Button);
        RelativeLayout menu3Btn = (RelativeLayout) findViewById(R.id.slide_menu3Button);

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //마이페이지로 이동
            }
        });

        menu1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //메뉴1로 이동
            }
        });

        menu2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //메뉴2로 이동
            }
        });

        menu3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //메뉴3로 이동
            }
        });

        ArrayList<OrderItem> list_itemArrayList= new ArrayList<OrderItem>();;
        listView = (ListView)findViewById(R.id.orderListView);
        System.out.println("리스트값 : "+list_itemArrayList);
        orderAdapter = new OrderAdapter(this, R.layout.order_item, list_itemArrayList, this);

        listView.setAdapter(orderAdapter);

        listLoad(list_itemArrayList);

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // TODO : item click
            }
        }) ;

        final ArrayList<OrderItem> orderResultList = new ArrayList<OrderItem>();
        Button orderBtn = (Button) findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<orderAdapter.getCount();i++){
                    System.out.println(orderAdapter.getItem(i));
                    OrderItem resultOrder = new OrderItem();
                    resultOrder = (OrderItem) orderAdapter.getItem(i);
                    if(resultOrder.getOrderCount() > 0){
                        orderResultList.add((OrderItem)orderAdapter.getItem(i));
                    }
                }
                // 예약화면으로 주문값 전달
                System.out.println("주문값 최종확인 : "+orderResultList);

                Intent intent = new Intent(OrderActivity.this, applyActivity.class);
                intent.putExtra("orderResultList",orderResultList);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onListBtnClick(int position) {
        Toast.makeText(this, Integer.toString(position+1) + " Item is selected..", Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show();
        }
    }


    //애니메이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if(isPageOpen){
                slidingPage01.setVisibility(View.INVISIBLE);
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


    private boolean listLoad(ArrayList<OrderItem> list_itemArrayList){
        ContentValues values = new ContentValues();
        /*values.put("currentPage", currentPage);*/

        if(list_itemArrayList == null){
            list_itemArrayList = new ArrayList<OrderItem>();
        }
        try {
            String result = new NetworkTask("orderItem.app", values).execute().get();
            System.out.println("DB리턴값 확인 : "+result);
            JSONObject obj = new JSONObject(result);
            JSONArray list = new JSONArray(obj.getString("list"));
            if(list.length()==0){
                /*listView.setVisibility(View.GONE);*/
                /*admin_jeju_noList.setVisibility(View.VISIBLE);*/
            }else{
                /*listView.setVisibility(View.VISIBLE);*/
                /*admin_jeju_noList.setVisibility(View.GONE);*/
                for(int i=0; i<list.length(); i++) {
                    JSONObject oiObj = list.getJSONObject(i);
                    OrderItem orderItem = new OrderItem();
                    orderItem.setName(oiObj.getString("name"));
                    orderItem.setPrice(oiObj.getString("price"));
                    orderItem.setNo(oiObj.getInt("no"));
                    orderItem.setOrderCount(0);

                    System.out.println("각 값 확인 : "+orderItem);
                    list_itemArrayList.add(orderItem);
                    orderAdapter.notifyDataSetChanged();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}

