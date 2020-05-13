package com.asyantech.ubamgo.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.asyantech.ubamgo.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    //Sliders
    List<SlideModel> slideModels;
    ImageSlider imageSlider;
    static Context context;
    SwipeRefreshLayout swipeRefreshLayout;
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
                        Toast.makeText(getActivity(), "Could not fetch images", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to Load " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(), "Network connection is not available!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "Could not fetch images", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to Load " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "Network connection is not available!", Toast.LENGTH_SHORT).show();
                    slideModels = new ArrayList<>();
                    slideModels.add(new SlideModel(R.drawable.ubambackground1, false));
                    slideModels.add(new SlideModel(R.drawable.ubambackground2, false));
                    slideModels.add(new SlideModel(R.drawable.ubambackground3, false));
                    imageSlider.setImageList(slideModels, true);
                }
                swipeRefreshLayout.setRefreshing(false);
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
                        Toast.makeText(getContext(), "Wifi Enabled", Toast.LENGTH_SHORT).show();
                        have_WIFI = true;
                    }
                }
                if(info.getType() == ConnectivityManager.TYPE_MOBILE){
                    if(info.isConnected()){
                        Toast.makeText(getContext(), "Data Network Enabled", Toast.LENGTH_SHORT).show();
                        have_MobileData = true;
                    }
                }
            }else{
                Toast.makeText(getContext(), "WiFi or Data Network not Enabled", Toast.LENGTH_SHORT).show();
            }
        }
        return have_MobileData || have_WIFI;
    }

}
