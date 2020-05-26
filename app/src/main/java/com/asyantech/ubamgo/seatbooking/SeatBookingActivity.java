package com.asyantech.ubamgo.seatbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SeatBookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText dateformat;
    int year, month, day;

    Button chooseSeat;
    Spinner sourceOfTravel;
    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("PlacesToTravel");
    List<String> categories;

    TextView source_of_travel_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);

        chooseSeat = findViewById(R.id.choose_seat);
        chooseSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseSeatActivity.class);
                startActivity(intent);
            }
        });


        dateformat = findViewById(R.id.choose_journey_date);
        final Calendar calendar = Calendar.getInstance();
        dateformat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SeatBookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateformat.setText(SimpleDateFormat.getDateInstance().format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        //Setting up spinner for Source of Travel
        //source_of_travel_tv = (TextView) findViewById(R.id.source_of_travel_tv);
        sourceOfTravel = (Spinner) findViewById(R.id.source_of_travel);
        sourceOfTravel.setOnItemSelectedListener(this);

        categories = new ArrayList<String>();
        getDataFromDatabaseSetupSpinner(new FirebaseCallback() {
            @Override
            public void onCallBack(List<String> list) {
                Toast.makeText(SeatBookingActivity.this, "List data "+list.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sourceOfTravel.setAdapter(dataAdapter);
        //sourceOfTravel.setPrompt(getString(R.string.select_source_place));
        //sourceOfTravel.setOnItemSelectedListener(this);
    }

    void getDataFromDatabaseSetupSpinner(final FirebaseCallback firebaseCallback){
        notebookRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document: task.getResult()){
                            String itemName = document.getString("place_name");
                            categories.add(itemName);
                        }
                        firebaseCallback.onCallBack(categories);
                    }else{
                        Toast.makeText(SeatBookingActivity.this, "Error getting documents: ", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SeatBookingActivity.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private interface FirebaseCallback{
        void onCallBack(List<String> list);
    }
}
