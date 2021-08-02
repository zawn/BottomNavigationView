package com.saicmotor.sc.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saicmotor.sc.myapplication.databinding.ActivityMainBinding;
import com.saicmotor.sc.myapplication.ui.FragmentTabNavigator;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        FragmentTabNavigator navigator = new FragmentTabNavigator(
                this, navHostFragment.getChildFragmentManager(), R.id.nav_host_fragment_activity_main);
        navController.getNavigatorProvider().addNavigator(navigator);
        Bundle startDestinationArgs = new Bundle();
        startDestinationArgs.putBoolean(FragmentTabNavigator.FLAG_FRAGMENT_SINGLETON, true);
        navController.setGraph(R.navigation.mobile_navigation, startDestinationArgs);

        BottomNavigationView bottomNavigationView = binding.navView;
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        return onNavDestinationSelected(item, navController);
                    }
                });
        final WeakReference<BottomNavigationView> weakReference =
                new WeakReference<>(bottomNavigationView);
        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                                                     @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        BottomNavigationView view = weakReference.get();
                        if (view == null) {
                            navController.removeOnDestinationChangedListener(this);
                            return;
                        }
                        Menu menu = view.getMenu();
                        for (int h = 0, size = menu.size(); h < size; h++) {
                            MenuItem item = menu.getItem(h);
                            if (matchDestination(destination, item.getItemId())) {
                                item.setChecked(true);
                            }
                        }
                    }
                });
    }

    public boolean onNavDestinationSelected(@NonNull MenuItem item,
                                            @NonNull NavController navController) {
        NavOptions options = new NavOptions.Builder().build();
        try {
            //TODO provide proper API instead of using Exceptions as Control-Flow.
            Bundle startDestinationArgs = new Bundle();
            startDestinationArgs.putBoolean(FragmentTabNavigator.FLAG_FRAGMENT_SINGLETON, true);
            navController.navigate(item.getItemId(), startDestinationArgs, options);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Determines whether the given <code>destId</code> matches the NavDestination. This handles
     * both the default case (the destination's id matches the given id) and the nested case where
     * the given id is a parent/grandparent/etc of the destination.
     */
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static boolean matchDestination(@NonNull NavDestination destination,
                                    @IdRes int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }
}