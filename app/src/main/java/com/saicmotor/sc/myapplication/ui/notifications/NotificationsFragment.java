package com.saicmotor.sc.myapplication.ui.notifications;

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

import com.saicmotor.sc.myapplication.databinding.FragmentNotificationsBinding;

import org.jetbrains.annotations.NotNull;

public class NotificationsFragment extends Fragment {
    private static final String TAG = "NotificationsFragment";
    private static final boolean DEBUG = true;


    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
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
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onHiddenChanged() called with: hidden = [" + hidden + "]");
    }

    @Override
    public void onAttachFragment(@NonNull @NotNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onAttachFragment() called with: childFragment = [" + childFragment + "]");
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onAttach() called with: context = [" + context + "]");
    }

    @Override
    public void onAttach(@NonNull @NotNull Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onAttach() called with: activity = [" + activity + "]");
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onViewCreated() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onViewStateRestored() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onResume() called");
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onSaveInstanceState() called with: outState = [" + outState + "]");
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onConfigurationChanged() called with: newConfig = [" + newConfig + "]");
    }

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onPrimaryNavigationFragmentChanged() called with: isPrimaryNavigationFragment = [" + isPrimaryNavigationFragment + "]");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onStop() called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onLowMemory() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onDestroy() called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, Integer.toHexString(System.identityHashCode(this)) +" "+ "onDetach() called");
    }
}