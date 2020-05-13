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

import java.util.ArrayList;
import java.util.List;

public class ChooseSeatActivity extends AppCompatActivity {

    private static final int COLUMNS = 5;
    private TextView txtSeatSelected;
    private Button submitCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat);

        txtSeatSelected = (TextView)findViewById(R.id.txt_seat_selected);
        submitCloud = (Button) findViewById(R.id.submit_cloud);
    }
}
