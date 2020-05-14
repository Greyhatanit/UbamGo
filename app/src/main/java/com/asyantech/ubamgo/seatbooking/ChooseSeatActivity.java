package com.asyantech.ubamgo.seatbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.seatbookingUtils.AbstractItem;
import com.asyantech.ubamgo.seatbookingUtils.AirplaneAdapter;
import com.asyantech.ubamgo.seatbookingUtils.CenterItem;
import com.asyantech.ubamgo.seatbookingUtils.EdgeItem;
import com.asyantech.ubamgo.seatbookingUtils.EmptyItem;
import com.asyantech.ubamgo.seatbookingUtils.OnSeatSelected;

import java.util.ArrayList;
import java.util.List;

public class ChooseSeatActivity extends AppCompatActivity implements OnSeatSelected {

    private static final int COLUMNS = 5;
    private TextView txtSeatSelected;
    private Button submitCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat);

        txtSeatSelected = (TextView)findViewById(R.id.txt_seat_selected);
        submitCloud = (Button) findViewById(R.id.submit_cloud);

        List<AbstractItem> items = new ArrayList<>();
        for (int i=0; i<30; i++) {
            if (i%COLUMNS==0 || i%COLUMNS==4) {
                items.add(new EdgeItem(String.valueOf(i)));
            } else if (i%COLUMNS==1 || i%COLUMNS==3) {
                items.add(new CenterItem(String.valueOf(i)));
            } else {
                items.add(new EmptyItem(String.valueOf(i)));
            }
        }

        GridLayoutManager manager = new GridLayoutManager(this, COLUMNS);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lst_items);
        recyclerView.setLayoutManager(manager);

        AirplaneAdapter adapter = new AirplaneAdapter(this, items);
        recyclerView.setAdapter(adapter);

        submitCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChooseSeatActivity.this, "Submit Cloud Button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSeatSelected(int count) {

        txtSeatSelected.setText("Book "+count+" seats");
    }
}
