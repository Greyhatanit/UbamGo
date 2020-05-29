package com.asyantech.ubamgo.seatbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SeatBookingActivity extends AppCompatActivity{
    EditText dateformat;

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

        dateformat = findViewById(R.id.choose_journey_date);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        dateformat.setHint(currentDate);
        dateformat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SeatBookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        dateformat.setText(mDayOfMonth+"/"+(mMonth+1)+"/"+mYear);
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

        //Swap Values of Spinner
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
}
