package com.asyantech.ubamgo.seatbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SeatBookingActivity extends AppCompatActivity {
    TextView dateformat;
    Button chooseSeat;

    Spinner sourceOfTravel, destinationOfTravel;
    //EditText sourceOfTravel, destinationOfTravel;
    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference placesToTravel = db.collection("PlacesToTravel");
    List<String> sourcesList, destinationList;
    Calendar calendar;
    int year, month, day;
    //Adapter
    ArrayAdapter dataAdapterForSource, dataAdapterForDestination;
    //Swap values of spinner
    ImageView swapValuesofSpinnerImage;
    FirebaseFirestore firestore;

    //Choose Time
    TextView chooseTime;

    //Selected Strings
    int position_source, position_destination;
    String selected_source, selected_destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);
        getSupportActionBar().setElevation(0);

        chooseSeat = findViewById(R.id.choose_seat);
        chooseSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseSeatActivity.class);
                startActivity(intent);
            }
        });

        firestore = FirebaseFirestore.getInstance();
        chooseTime = (TextView) findViewById(R.id.choose_journey_time);

        dateformat = findViewById(R.id.choose_journey_date);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        final String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        //dateformat.setText(currentDate);
        dateformat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                final String date_pattern = "dd MMMM yyyy";

                DatePickerDialog datePickerDialog = new DatePickerDialog(SeatBookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        //Toast.makeText(SeatBookingActivity.this, "Year: "+mYear+ "Month: "+(mMonth+1)+"Day: "+mDayOfMonth, Toast.LENGTH_SHORT).show();
                        String currentDateString = mDayOfMonth+"/"+(month+1)+"/"+mYear;
                        Calendar convertedDate = Calendar.getInstance();
                        convertedDate.set(mYear, mMonth, mDayOfMonth);
                        String datePicked = DateFormat.getDateInstance().format(convertedDate.getTime());
                        dateformat.setText(datePicked);
                        getBusNoAndDepatureTime(datePicked);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        //Setting up spinner for Source of Travel

        sourceOfTravel = (Spinner) findViewById(R.id.source_of_travel);
        sourceOfTravel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                position_source = sourceOfTravel.getSelectedItemPosition();
                selected_source = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destinationOfTravel = (Spinner) findViewById(R.id.destination_of_travel);
        destinationOfTravel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                position_destination = destinationOfTravel.getSelectedItemPosition();
                selected_destination = parent.getItemAtPosition(position).toString();
                if(selected_source.equalsIgnoreCase(selected_destination)){
                    position_destination = position_destination +1;
                    destinationOfTravel.setSelection(position_destination);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sourcesList = new ArrayList<String>();
        destinationList = new ArrayList<String>();

        getDataFromDatabaseSetupSpinner();

        swapValuesofSpinnerImage = (ImageView) findViewById(R.id.placeswap);
        swapValuesofSpinnerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceOfTravel.setSelection(position_destination);
                destinationOfTravel.setSelection(position_source);
            }
        });
    }

    void getDataFromDatabaseSetupSpinner(){
        placesToTravel.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document: task.getResult()){
                            String itemName = document.getString("place_name");
                            sourcesList.add(itemName);
                            destinationList.add(itemName);
                        }
                        dataAdapterForSource = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, sourcesList);
                        dataAdapterForDestination = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, destinationList);

                        dataAdapterForSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dataAdapterForDestination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        sourceOfTravel.setAdapter(dataAdapterForSource);
                        destinationOfTravel.setAdapter(dataAdapterForDestination);
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

    void getBusNoAndDepatureTime(String choosen_date){

        final DocumentReference documentReference = firestore.collection("ScheduledDepature").document(choosen_date);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                
                String departure_time = documentSnapshot.getString("depature_time");
                chooseTime.setText(departure_time);
                //Toast.makeText(SeatBookingActivity.this, "Bus_no: "+bus_no+"Depature_time: "+departure_time, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
