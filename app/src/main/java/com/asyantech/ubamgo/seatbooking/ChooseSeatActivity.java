package com.asyantech.ubamgo.seatbooking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.seatbookingUtils.AbstractItem;
import com.asyantech.ubamgo.seatbookingUtils.AirplaneAdapter;
import com.asyantech.ubamgo.seatbookingUtils.CenterItem;
import com.asyantech.ubamgo.seatbookingUtils.EdgeItem;
import com.asyantech.ubamgo.seatbookingUtils.EmptyItem;
import com.asyantech.ubamgo.seatbookingUtils.OnSeatSelected;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChooseSeatActivity extends AppCompatActivity implements OnSeatSelected {

    private static final int COLUMNS = 5;
    private TextView txtSeatSelected;
    private int no_of_selected_seat;

    String document_id, bus_id,depature_date, depature_time, travel_source, travel_destination, travel_cost, travel_rating;
    List<AbstractItem> items;
    ArrayList<AbstractItem> schedule_list = new ArrayList<AbstractItem>();
    AirplaneAdapter adapter;

    //Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat);

        //Get data from ListSchedulesActivity
        Intent in = getIntent();
        document_id = in.getStringExtra("document_id");
        bus_id = in.getStringExtra("bus_id");
        depature_date = in.getStringExtra("depature_date");
        depature_time = in.getStringExtra("depature_time");
        travel_source = in.getStringExtra("travel_source");
        travel_destination = in.getStringExtra("travel_destination");
        travel_cost = in.getStringExtra("travel_cost");
        travel_rating = in.getStringExtra("travel_rating");

        Toast.makeText(this,
                "Document ID: "+document_id+
                "\nBus ID: "+bus_id+
                "\nDepature Date: "+depature_date
                +"\nTime: "+depature_time+
                "\nTravel Source: "+travel_source+
                "\nTravel Destination: "+travel_destination+
                "\nTravel Cost: "+travel_cost+
                "\nTravel Rating: "+travel_rating, Toast.LENGTH_SHORT).show();

        txtSeatSelected = (TextView)findViewById(R.id.txt_seat_selected);

        items = new ArrayList<>();
        for (int i=0; i<37; i++) {
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

        adapter = new AirplaneAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSeatSelected(View view, int position, final List<Integer> selected_seats, int count) {
        no_of_selected_seat = count;
        txtSeatSelected.setText("Book "+count+" seats");
        Map<Integer, Integer> map = selected_seats.stream().distinct().collect(Collectors.toMap(Integer::intValue, Integer::intValue));
        txtSeatSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cost_per_seat = 1000;
                int total_cost_selected_seat = cost_per_seat * no_of_selected_seat;
                Toast.makeText(ChooseSeatActivity.this, "List items are:  "+map, Toast.LENGTH_SHORT).show();
                /*
                db.collection("BusDetails")
                        .document(bus_id)
                        .set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChooseSeatActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChooseSeatActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                            }
                        });
                     */
            }
        });
    }

    public void prepareSelection(View view, int position){

    }
}
