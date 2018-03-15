package android.app.setak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 206 on 2018-03-13.
 */

public class MyReservePackageAdapter extends BaseAdapter {
    Context context;
    ArrayList<Order> list_itemArrayList;
    MyReservePackageAdapter.ViewHolder viewHolder;
    String setakList, setakState;

    public MyReservePackageAdapter(ArrayList<Order> list_itemArrayList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.myreserve_package_item, null);

            viewHolder = new MyReservePackageAdapter.ViewHolder();
            viewHolder.myreserve_setakName = (TextView) convertView.findViewById(R.id.myreserve_setakName2);
            viewHolder.myreserve_setakInDate = (TextView) convertView.findViewById(R.id.myreserve_setakInDate2);
            viewHolder.myreserve_setakPrice = (TextView) convertView.findViewById(R.id.myreserve_setakPrice2);
            viewHolder.myreserve_setakState = (TextView) convertView.findViewById(R.id.myreserve_setakState2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyReservePackageAdapter.ViewHolder) convertView.getTag();
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

        return convertView;
    }

    class ViewHolder {
        TextView myreserve_setakName;
        TextView myreserve_setakInDate;
        TextView myreserve_setakPrice;
        TextView myreserve_setakState;
    }
}
