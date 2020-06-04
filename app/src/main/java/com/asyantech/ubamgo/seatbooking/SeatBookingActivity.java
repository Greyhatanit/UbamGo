package com.asyantech.ubamgo.seatbooking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeatBookingActivity extends AppCompatActivity{
    TextView dateformat;

    Button chooseSeat;

    //Spinner sourceOfTravel, destinationOfTravel;
    EditText sourceOfTravel, destinationOfTravel;
    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("PlacesToTravel");
    List<String> categories;
    Calendar calendar;
    int year, month, day;
    //Adapter
    ArrayAdapter dataAdapter;
    //Swap values of spinner
    ImageView swapValuesofSpinnerImage;
    FirebaseFirestore firestore;

    //Choose Time
    TextView chooseTime;
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
        /*
        sourceOfTravel = (Spinner) findViewById(R.id.source_of_travel);
        sourceOfTravel.setOnItemSelectedListener(this);

        destinationOfTravel = (Spinner) findViewById(R.id.destination_of_travel);
        destinationOfTravel.setOnItemSelectedListener(this);

        categories = new ArrayList<String>();
        categories.add("Tulsipur");
        getDataFromDatabaseSetupSpinner();
         */

        sourceOfTravel = (EditText) findViewById(R.id.source_of_travel);
        destinationOfTravel = (EditText) findViewById(R.id.destination_of_travel);

        //Swap Values of Edit Text
        swapValuesofSpinnerImage = (ImageView) findViewById(R.id.placeswap);
        swapValuesofSpinnerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sourceofTravelText = sourceOfTravel.getText().toString();

                String destinationofTravelText = destinationOfTravel.getText().toString();
                //Swapping the values of source and destination

                sourceofTravelText = sourceofTravelText + destinationofTravelText;
                destinationofTravelText = sourceofTravelText.substring(0, sourceofTravelText.length() - destinationofTravelText.length());
                sourceofTravelText = sourceofTravelText.substring(destinationofTravelText.length());

                //setting the values to both spinners
                sourceOfTravel.setText(sourceofTravelText);
                destinationOfTravel.setText(destinationofTravelText);
            }
        });
    }

    /*
    void getDataFromDatabaseSetupSpinner(){
        notebookRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document: task.getResult()){
                            String itemName = document.getString("place_name");
                            categories.add(itemName);
                        }
                        dataAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sourceOfTravel.setAdapter(dataAdapter);
                        destinationOfTravel.setAdapter(dataAdapter);
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
     */

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
