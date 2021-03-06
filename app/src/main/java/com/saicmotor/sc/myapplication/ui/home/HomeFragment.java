package com.saicmotor.sc.myapplication.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.PieEntry;
import com.saicmotor.sc.myapplication.R;
import com.saicmotor.sc.myapplication.databinding.FragmentHomeBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final boolean DEBUG = true;


    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
    private String hexString;
    private String format;

    public HomeFragment() {
        Log.d(TAG, "HomeFragment() called");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.text;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s + " " + Integer.toHexString(System.identityHashCode(HomeFragment.this)));
            }
        });

        if (savedInstanceState != null) {
            hexString = savedInstanceState.getString("hexString");
            format = savedInstanceState.getString("format");
        }

        if (hexString == null) {
            hexString = Integer.toHexString(System.identityHashCode(this));
            format = sdf.format(new Date());
        }

        binding.state.setText("Create at " + format + "\n state " + hexString);

//        "speed0": 0,
//                "speed1_30": 660,
//                "speed31_60": 1200,
//                "speed61_80": 1740,
//                "speed81_100": 2010,
//                "speed100": 720,


        TripData tripDetail = new TripData();
        tripDetail.setSpeed0(300);
        tripDetail.setSpeed130(660);
        tripDetail.setSpeed3160(1200);
        tripDetail.setSpeed6180(1740);
        tripDetail.setSpeed81100(2010);
        tripDetail.setSpeed100(900);

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(new Double(tripDetail.getSpeed0()).floatValue(), "Idle speed"));
        entries.add(new PieEntry(new Double(tripDetail.getSpeed130()).floatValue(), "1-30km/h"));
        entries.add(new PieEntry(new Double(tripDetail.getSpeed3160()).floatValue(), "31-60km/h"));
        entries.add(new PieEntry(new Double(tripDetail.getSpeed6180()).floatValue(), "61-80km/h"));
        entries.add(new PieEntry(new Double(tripDetail.getSpeed81100()).floatValue(), "81-100km/h"));
        entries.add(new PieEntry(new Double(tripDetail.getSpeed100()).floatValue(), ">100km/h"));
        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed0));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed1));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed2));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed3));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed4));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed5));

        binding.waveProgressView.setData(entries,colors);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onHiddenChanged() called with: hidden = [" + hidden + "]");
    }

    @Override
    public void onAttachFragment(@NonNull @NotNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onAttachFragment() called with: childFragment = [" + childFragment + "]");
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onAttach() called with: context = [" + context + "]");
    }

    @Override
    public void onAttach(@NonNull @NotNull Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onAttach() called with: activity = [" + activity + "]");
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onViewCreated() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onViewStateRestored() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onResume() called");
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("hexString", hexString);
        outState.putString("format", format);

        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onSaveInstanceState() called with: outState = [" + outState + "]");
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onConfigurationChanged() called with: newConfig = [" + newConfig + "]");
    }

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onPrimaryNavigationFragmentChanged() called with: isPrimaryNavigationFragment = [" + isPrimaryNavigationFragment + "]");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onStop() called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onLowMemory() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onDestroy() called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, Integer.toHexString(System.identityHashCode(this)) + " " + "onDetach() called");
    }
}