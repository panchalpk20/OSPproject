package com.anngrynerds.ospproject.CheckOut;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.pojo.Order;
import com.anngrynerds.ospproject.pojo.OrderItem;
import com.google.gson.Gson;

import java.text.MessageFormat;

public class CheckOutActivity extends AppCompatActivity {

    public static String EXTRA_OrderObject = "OrderObjectForCheckOut";
    Order order;

    TableLayout tableLayout;
    Context context;
    int totalCost;
    TextView tv_totalCost;
    Button btn_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        context = CheckOutActivity.this;

        tableLayout = findViewById(R.id.checkout_tableLayout);
        tv_totalCost = findViewById(R.id.checkout_tv_totalcost);
        btn_pay = findViewById(R.id.checkout_btn_pay);
        String json = getIntent().getStringExtra(EXTRA_OrderObject);
        order = new Gson().fromJson(json, Order.class);

        totalCost = 0;
        for(OrderItem item : order.getList()){

            TableRow tableRow = new TableRow(context);
            TextView name = new TextView(context);
            name.setText(item.getName());

            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            name.setTextColor(getResources().getColor(R.color.textColor));
            name.setGravity(Gravity.START);

            TextView qty = new TextView(context);
            qty.setText(item.getQty());
            qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            qty.setTextColor(getResources().getColor(R.color.textColor));
            qty.setGravity(Gravity.CENTER);


            TextView cost = new TextView(context);
            cost.setText(item.getCost());
            cost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            cost.setTextColor(getResources().getColor(R.color.textColor));
            cost.setGravity(Gravity.CENTER);


            totalCost = totalCost + (Integer.parseInt(item.getCost())*Integer.parseInt(item.getQty()));

            tableRow.addView(name);
            tableRow.addView(qty);
            tableRow.addView(cost);

            tableLayout.addView(tableRow);
        }

        tv_totalCost.setText(String.valueOf(totalCost));
        btn_pay.setText(MessageFormat.format("Pay{0}", totalCost));

    }
}