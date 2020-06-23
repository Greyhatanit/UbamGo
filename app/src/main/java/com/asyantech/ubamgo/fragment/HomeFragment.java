package com.asyantech.ubamgo.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.seatbooking.ListSchedulesActivity;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    //Sliders
    List<SlideModel> slideModels;
    ImageSlider imageSlider;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView dateformat;

    Spinner sourceOfTravel, destinationOfTravel;
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
    TextView chooseTime, date_tomorrow;

    //Selected Strings
    int position_source, position_destination;
    String selected_source, selected_destination;

    //Search Bus Button
    Button searchBus;
    String datePicked;

    //Tag
    private final static String TAG_HOME = "TAG_HOME";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageSlider= view.findViewById(R.id.slider);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        if(haveNetwork()) {//If internet is connected then
            //Getting the values of slider from firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference images = db.collection("Sliders").document("images");
            images.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();

                        slideModels = new ArrayList<>();
                        String img1 = (String) doc.get("image1");
                        String img2 = (String) doc.get("image2");
                        String img3 = (String) doc.get("image3");
                        String img4 = (String) doc.get("image4");
                        String img5 = (String) doc.get("image5");

                        slideModels.add(new SlideModel(img1, false));
                        slideModels.add(new SlideModel(img2, false));
                        slideModels.add(new SlideModel(img3, false));
                        slideModels.add(new SlideModel(img4, false));
                        slideModels.add(new SlideModel(img5, false));
                        imageSlider.setImageList(slideModels, true);

                    } else {
                        Toast.makeText(getActivity(), R.string.failed_to_fetch_images, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), R.string.failed_to_load + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(), R.string.network_not_available, Toast.LENGTH_SHORT).show();
            slideModels = new ArrayList<>();
            slideModels.add(new SlideModel(R.drawable.ubambackground1, false));
            slideModels.add(new SlideModel(R.drawable.ubambackground2, false));
            slideModels.add(new SlideModel(R.drawable.ubambackground3, false));
            imageSlider.setImageList(slideModels, true);
        }

        //OnRefresh check if the INTERNET CONNECTIVITY IS TURNED ON AND FETCH THE DATA FROM FIREBASE
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(haveNetwork()) {//If internet is connected then
                    //Getting the values of slider from firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference images = db.collection("Sliders").document("images");
                    images.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();

                                slideModels = new ArrayList<>();
                                String img1 = (String) doc.get("image1");
                                String img2 = (String) doc.get("image2");
                                String img3 = (String) doc.get("image3");
                                String img4 = (String) doc.get("image4");
                                String img5 = (String) doc.get("image5");

                                slideModels.add(new SlideModel(img1, false));
                                slideModels.add(new SlideModel(img2, false));
                                slideModels.add(new SlideModel(img3, false));
                                slideModels.add(new SlideModel(img4, false));
                                slideModels.add(new SlideModel(img5, false));
                                imageSlider.setImageList(slideModels, true);

                            } else {
                                Toast.makeText(getActivity(), R.string.failed_to_fetch_images, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), R.string.failed_to_load + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), R.string.network_not_available, Toast.LENGTH_SHORT).show();
                    slideModels = new ArrayList<>();
                    slideModels.add(new SlideModel(R.drawable.ubambackground1, false));
                    slideModels.add(new SlideModel(R.drawable.ubambackground2, false));
                    slideModels.add(new SlideModel(R.drawable.ubambackground3, false));
                    imageSlider.setImageList(slideModels, true);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        firestore = FirebaseFirestore.getInstance();
        chooseTime = (TextView) view.findViewById(R.id.choose_journey_time);

        dateformat = view.findViewById(R.id.choose_journey_date);

        calendar = Calendar.getInstance();
        final String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateformat.setText(currentDate);

        dateformat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        Calendar convertedDate = Calendar.getInstance();
                        convertedDate.set(mYear, mMonth, mDayOfMonth);
                        datePicked = DateFormat.getDateInstance().format(convertedDate.getTime());
                        dateformat.setText(datePicked);
                        //getBusNoAndDepatureTime(datePicked);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        //Set Date for Tomorrow
        date_tomorrow = (TextView) view.findViewById(R.id.date_tomorrow);
        date_tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add one day to the date/calendar
                Calendar calendartom= Calendar.getInstance();
                calendartom.add(Calendar.DAY_OF_YEAR, 1);
                String tomorrow_date = DateFormat.getDateInstance().format(calendartom.getTime());
                dateformat.setText(tomorrow_date);
                date_tomorrow.setClickable(false);
            }
        });


        //Setting up spinner for Source of Travel
        sourceOfTravel = (Spinner) view.findViewById(R.id.source_of_travel);
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

        destinationOfTravel = (Spinner) view.findViewById(R.id.destination_of_travel);
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

        //Swap Values of Edit Text
        swapValuesofSpinnerImage = (ImageView) view.findViewById(R.id.placeswap);
        swapValuesofSpinnerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceOfTravel.setSelection(position_destination);
                destinationOfTravel.setSelection(position_source);
            }
        });

        //Search Bus
        searchBus = view.findViewById(R.id.search_bus);
        searchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ListSchedulesActivity.class);
                intent.putExtra("selected_source", selected_source);
                intent.putExtra("selected_destination", selected_destination);
                intent.putExtra("selected_date", (String) dateformat.getText());
                startActivity(intent);

                /*
                ListSchedulesFragment listSchedulesFragment = new ListSchedulesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selected_source", selected_source);
                bundle.putString("selected_destination", selected_destination);
                bundle.putString("selected_date", (String) dateformat.getText());
                listSchedulesFragment.setArguments(bundle);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, listSchedulesFragment);
                fr.addToBackStack(TAG_HOME);
                fr.commit();
                 */
            }
        });
        return view;
    }

    //Check if internet connection is turned on
    public boolean haveNetwork(){
        boolean have_WIFI = false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] activeNetwork = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info: activeNetwork){
            if(null != activeNetwork){
                if(info.getType()==ConnectivityManager.TYPE_WIFI){
                    if(info.isConnected()){
                        have_WIFI = true;
                    }
                }
                if(info.getType() == ConnectivityManager.TYPE_MOBILE){
                    if(info.isConnected()){
                        have_MobileData = true;
                    }
                }
            }else{
                Toast.makeText(getContext(), R.string.no_network_available, Toast.LENGTH_SHORT).show();
            }
        }
        return have_MobileData || have_WIFI;
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
                        dataAdapterForSource = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sourcesList);
                        dataAdapterForDestination = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, destinationList);

                        dataAdapterForSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dataAdapterForDestination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        sourceOfTravel.setAdapter(dataAdapterForSource);
                        destinationOfTravel.setAdapter(dataAdapterForDestination);
                    }else{
                        Toast.makeText(getContext(), "Error getting documents: ", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

}
