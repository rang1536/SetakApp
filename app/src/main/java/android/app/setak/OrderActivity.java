package android.app.setak;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.ListBtnClickListener {

    String cateItem;
    OrderAdapter orderAdapter;
    TextView orderCountText;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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
    public boolean onCreateOptionsMenu(Menu menu) {
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

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);

       /* actionBar.hide();*/
        return true;
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

