package com.asyantech.ubamgo.seatbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.asyantech.ubamgo.DashboardActivity;
import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.login.SignUpActivity;
import com.asyantech.ubamgo.model.Booking;
import com.asyantech.ubamgo.seatbookingUtils.AbstractItem;
import com.asyantech.ubamgo.seatbookingUtils.AirplaneAdapter;
import com.asyantech.ubamgo.seatbookingUtils.CenterItem;
import com.asyantech.ubamgo.seatbookingUtils.EdgeItem;
import com.asyantech.ubamgo.seatbookingUtils.EmptyItem;
import com.asyantech.ubamgo.seatbookingUtils.OnSeatSelected;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChooseSeatActivity extends AppCompatActivity implements OnSeatSelected {

    private static final int COLUMNS = 5;
    private TextView txtSeatSelected;

    String document_id, bus_id,depature_date, depature_time, travel_source, travel_destination,travel_cost,
            travel_rating;
    List<AbstractItem> items;
    AirplaneAdapter adapter;

    //Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookings = db.collection("Booking");
    FirebaseAuth firebaseAuth;

    List<String> reserved_seat = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat);

        firebaseAuth = FirebaseAuth.getInstance();

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
                        "travel_cost"+ travel_cost
                , Toast.LENGTH_SHORT).show();

        txtSeatSelected = (TextView)findViewById(R.id.txt_seat_selected);
        //Get reserved seats
        //FirebaseFirestore.getInstance().collection("")
        final DocumentReference documentReference = db.collection("Booking").document(bus_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                reserved_seat = (List<String>) snapshot.get("selected_seats");
                Toast.makeText(ChooseSeatActivity.this, "Reserved Seats are: "+ reserved_seat.toString(), Toast.LENGTH_SHORT).show();
            }
        });

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

    @Override
    public void onSeatSelected(View view, int position, final List<Integer> selected_seats, int count) {
        txtSeatSelected.setText("Book "+count+" seats");
        txtSeatSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int total_liable_payment = count * Integer.parseInt(travel_cost);
                Intent intent = new Intent(getApplicationContext(), PaymentOfBookedSeatActivity.class);
                intent.putExtra("schedule_id",document_id);
                intent.putExtra("bus_id", bus_id );
                intent.putExtra("depature_date", depature_date);
                intent.putExtra("depature_time", depature_time);
                intent.putExtra("travel_source", travel_source);
                intent.putExtra("travel_destination", travel_destination);
                intent.putExtra("travel_rating", travel_rating);
                intent.putExtra("total_liable_payment", total_liable_payment);
                intent.putExtra("seat_count", count);
                intent.putIntegerArrayListExtra("booked_seat", (ArrayList<Integer>) selected_seats);
                startActivity(intent);
            }
        });
    }
}
