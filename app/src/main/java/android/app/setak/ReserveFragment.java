package android.app.setak;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;



public class ReserveFragment extends Fragment {
    ListView listView;
    RelativeLayout noList;
    ArrayList<Order> list_itemArrayList;
    ReserveAdapter reserveAdapter;
    int loginUserNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reserve,null);
        listView = (ListView)view.findViewById(R.id.reserve_booking_listView);
        noList = (RelativeLayout)view.findViewById(R.id.reserve_booking_noList);
        list_itemArrayList = new ArrayList<Order>();
        reserveAdapter = new ReserveAdapter(list_itemArrayList);
        listView.setAdapter(reserveAdapter);

        SharedPreferences login = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        loginUserNo = login.getInt("loginUserNo", 0);
        ContentValues values = new ContentValues();
        values.put("userNo", loginUserNo);

        try {
            String result = new NetworkTask("setakReserve.app", values).execute().get();
            JSONObject obj = new JSONObject(result);
            JSONArray list = new JSONArray(obj.getString("list"));

            System.out.println("수거 대기중 목록 확인 : "+list);
            if(list.length()==0){
                listView.setVisibility(View.GONE);
                noList.setVisibility(View.VISIBLE);
            }else{
                listView.setVisibility(View.VISIBLE);
                noList.setVisibility(View.GONE);

                ArrayList<OrderItem> orderlist = new ArrayList<OrderItem>();
                for(int i=0; i<list.length(); i++) {
                    JSONObject gpObj = list.getJSONObject(i);
                    Order order = new Order();
                    order.setOrderNo(gpObj.getInt("orderNo"));
                    order.setInDate(gpObj.getString("inDate"));

                    JSONArray jsList = gpObj.getJSONArray("orderList");
                    for(int j=0; j<jsList.length(); j++){
                        JSONObject orObj = jsList.getJSONObject(j);
                        OrderItem orderItem = new OrderItem();
                        orderItem.setName(orObj.getString("name"));
                        orderItem.setOrderCount(orObj.getInt("orderCount"));
                        orderlist.add(orderItem);
                    }

                    order.setOrderItemList(orderlist);
                    order.setTotalPrice(gpObj.getString("totalPrice"));
                    order.setState(gpObj.getString("state"));

                    System.out.println("갱신전 주문 확인 : "+order);
                    list_itemArrayList.add(order);
                    reserveAdapter.notifyDataSetChanged(); //변경된내용 갱신
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
