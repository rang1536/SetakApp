package android.app.setak;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MyReserveBookingFragment extends Fragment {
    ListView listView;
    RelativeLayout noList;
    ArrayList<Order> list_itemArrayList;
    MyReserveBookingAdapter myReserveBookingAdapter;
    int loginUserNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_reserve_booking,null);
        listView = (ListView)view.findViewById(R.id.my_reserve_booking_listView);
        noList = (RelativeLayout)view.findViewById(R.id.my_reserve_booking_noList);
        list_itemArrayList = new ArrayList<Order>();
        myReserveBookingAdapter = new MyReserveBookingAdapter(list_itemArrayList);
        listView.setAdapter(myReserveBookingAdapter);

        SharedPreferences login = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        loginUserNo = login.getInt("loginUserNo", 0);
        ContentValues values = new ContentValues();
        values.put("userNo", loginUserNo);

        try {
            String result = new NetworkTask("mySetakReserve.app", values).execute().get();
            JSONObject obj = new JSONObject(result);
            JSONArray list = new JSONArray(obj.getString("list"));

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

                    list_itemArrayList.add(order);
                    myReserveBookingAdapter.notifyDataSetChanged(); //변경된내용 갱신
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
