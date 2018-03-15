package android.app.setak;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by 206 on 2018-03-14.
 */

public class ReserveAdapter extends BaseAdapter {
    Context context;
    ArrayList<Order> list_itemArrayList;
    ReserveAdapter.ViewHolder viewHolder;
    String setakList, setakState;

    public ReserveAdapter(ArrayList<Order> list_itemArrayList) {
        this.list_itemArrayList = list_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reserve_item, null);

            viewHolder = new ReserveAdapter.ViewHolder();
            viewHolder.myreserve_setakName = (TextView) convertView.findViewById(R.id.reserve_setakName);
            viewHolder.myreserve_setakInDate = (TextView) convertView.findViewById(R.id.reserve_setakInDate);
            viewHolder.myreserve_setakPrice = (TextView) convertView.findViewById(R.id.reserve_setakPrice);
            viewHolder.myreserve_setakState = (TextView) convertView.findViewById(R.id.reserve_setakState);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReserveAdapter.ViewHolder) convertView.getTag();
        }
        setakList = "";

        for(int i=0; i<list_itemArrayList.get(position).getOrderItemList().size(); i++){
            if(i==0){
                setakList += list_itemArrayList.get(position).getOrderItemList().get(i).getName()+"("+list_itemArrayList.get(position).getOrderItemList().get(i).getOrderCount()+"벌)";
            }else {
                setakList += ", "+list_itemArrayList.get(position).getOrderItemList().get(i).getName()+"("+list_itemArrayList.get(position).getOrderItemList().get(i).getOrderCount()+"벌)";
            }
        }
        if(list_itemArrayList.get(position).getState().equals("1")){
            setakState = "수거대기";
        }else if(list_itemArrayList.get(position).getState().equals("2")){
            setakState = "세탁중";
        }else if(list_itemArrayList.get(position).getState().equals("3")){
            setakState = "세탁완료";
        }else if(list_itemArrayList.get(position).getState().equals("4")){
            setakState = "배송중";
        }

        viewHolder.myreserve_setakName.setText(setakList);
        viewHolder.myreserve_setakInDate.setText(list_itemArrayList.get(position).getInDate());
        viewHolder.myreserve_setakPrice.setText(list_itemArrayList.get(position).getTotalPrice());
        viewHolder.myreserve_setakState.setText(setakState);

        viewHolder.myreserve_setakState.setTag(position);
        viewHolder.myreserve_setakState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int pos = Integer.parseInt(v.getTag().toString());
                Order order = list_itemArrayList.get(pos);

                System.out.println("기사님 배정~!!");
                System.out.println("주문확인 : "+order);

                SharedPreferences login = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                int loginUserNo = login.getInt("loginUserNo", 0);
                String loginUserId = login.getString("loginUserId", null);
                ContentValues values = new ContentValues();
                values.put("userNo", loginUserNo);
                values.put("orderNo", order.getOrderNo());

                try{
                    String r = new NetworkTask("setakReceive.app", values).execute().get();
                    JSONObject obj = new JSONObject(r);
                    String result = obj.getString("result");

                    if(result.equals("success")){
                        Toast.makeText(context, loginUserId+" 님이 배정되었습니다", Toast.LENGTH_SHORT).show();
                        //인텐트이동

                    }else if(result.equals("exist")){
                        Toast.makeText(context, "이미 기사님이 배정되었습니다", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "배정에 실패하였습니다. 본사로 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView myreserve_setakName;
        TextView myreserve_setakInDate;
        TextView myreserve_setakPrice;
        TextView myreserve_setakState;
    }
}
