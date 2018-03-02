package android.app.setak;

import java.io.Serializable;

/**
 * Created by Administrator on 2018-02-18.
 */

public class OrderItem implements Serializable {
    private int no;
    private String name;
    private String price;
    private int orderCount;

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", orderCount=" + orderCount +
                '}';
    }
}
