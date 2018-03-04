package android.app.setak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-02-18.
 */

public class OrderAdapter extends ArrayAdapter implements View.OnClickListener {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<OrderItem> listViewItemList;
    ViewHolder viewHolder;
    // 생성자로부터 전달된 resource id 값을 저장.
    int resourceId ;
    // 생성자로부터 전달된 ListBtnClickListener  저장.
    private ListBtnClickListener listBtnClickListener ;


    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의.
    public interface ListBtnClickListener {
        void onListBtnClick(int position) ;
    }

    public void onClick(View v) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출.
        if (this.listBtnClickListener != null) {
            this.listBtnClickListener.onListBtnClick((int)v.getTag()) ;
        }
    }

    // ListViewAdapter의 생성자
    public OrderAdapter(Context context, int resource, ArrayList<OrderItem> listViewItemList,ListBtnClickListener clickListener){
        super(context, resource, listViewItemList) ;

        // resource id 값 복사. (super로 전달된 resource를 참조할 방법이 없음.)
        this.resourceId = resource ;
        this.listViewItemList = listViewItemList;
        this.listBtnClickListener = clickListener ;

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resourceId, parent, false);
        }
            /*viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name) ;
            viewHolder.price = (TextView) convertView.findViewById(price) ;
            viewHolder.orderMinusBtn = (Button) convertView.findViewById(R.id.orderMinusBtn);
            viewHolder.orderPlusBtn = (Button) convertView.findViewById(R.id.orderPlusBtn);
            viewHolder.orderCountText = (TextView) convertView.findViewById(R.id.orderCountText);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }*/

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        /*viewHolder = new ViewHolder();
        viewHolder.name = (TextView) convertView.findViewById(R.id.name) ;
        viewHolder.price = (TextView) convertView.findViewById(price) ;*/
        final TextView name = (TextView) convertView.findViewById(R.id.name) ;
        final TextView price = (TextView) convertView.findViewById(R.id.price) ;
        final ImageButton orderMinusBtn = (ImageButton) convertView.findViewById(R.id.orderMinusBtn);
        final ImageButton orderPlusBtn = (ImageButton) convertView.findViewById(R.id.orderPlusBtn);
        final TextView orderCountText = (TextView) convertView.findViewById(R.id.orderCountText);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
       /* OrderItem listViewItem = listViewItemList.get(position);*/
        final OrderItem listViewItem = (OrderItem) listViewItemList.get(pos);

        String priceComma = String.format("%,d",Integer.parseInt(listViewItem.getPrice())); //그린피 컴마 찍기
        // 아이템 내 각 위젯에 데이터 반영
        name.setText(listViewItem.getName());
        price.setText(listViewItem.getPrice());

        orderPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String count = String.valueOf(Integer.parseInt(orderCountText.getText().toString())+1);
                orderCountText.setText(count);
                /*System.out.println("널 확인1 : "+totalPrice.getText().toString());
                System.out.println("널 확인2 : "+price.getText().toString());
                totalPrice.setText(String.valueOf(Integer.parseInt(totalPrice.getText().toString())+(Integer.parseInt(count)*Integer.parseInt(price.getText().toString()))));*/
                listViewItem.setOrderCount(Integer.parseInt(orderCountText.getText().toString()));
            }
        });

        orderMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(orderCountText.getText().toString()) > 0){
                    String count = String.valueOf(Integer.parseInt(orderCountText.getText().toString())-1);
                    orderCountText.setText(count);
                    listViewItem.setOrderCount(Integer.parseInt(orderCountText.getText().toString()));
                }
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(int no, String name, String price, int orderCount) {
        OrderItem item = new OrderItem();

        item.setNo(no);
        item.setName(name);
        item.setPrice(price);
        item.setOrderCount(orderCount);

        listViewItemList.add(item);
    }

    class ViewHolder{
        TextView name;
        TextView price;
        ImageButton orderMinusBtn, orderPlusBtn;
        Button orderBtn;
        TextView orderCountText;
    }
}

