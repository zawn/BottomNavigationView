package com.saicmotor.sc.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saicmotor.sc.myapplication.databinding.ActivityMainBinding;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

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

    public static NavBackStackEntry getBackStackEntry(Deque<NavBackStackEntry> backStack, @IdRes int destinationId) {
        NavBackStackEntry lastFromBackStack = null;
        Iterator<NavBackStackEntry> iterator = backStack.descendingIterator();
        while (iterator.hasNext()) {
            NavBackStackEntry entry = iterator.next();
            NavDestination destination = entry.getDestination();
            if (destination.getId() == destinationId) {
                lastFromBackStack = entry;
                break;
            }
        }
        return lastFromBackStack;
    }

    public boolean onNavDestinationSelected(@NonNull MenuItem item,
                                            @NonNull NavController navController) {
        @SuppressLint("RestrictedApi")
        Deque<NavBackStackEntry> backStack = navController.getBackStack();
        int itemId = item.getItemId();
        NavBackStackEntry backStackEntry = getBackStackEntry(backStack, itemId);
        if (backStackEntry == null) {
            NavOptions.Builder builder = new NavOptions.Builder()
                    .setLaunchSingleTop(true);
            if (navController.getCurrentDestination().getParent().findNode(item.getItemId())
                    instanceof ActivityNavigator.Destination) {
                builder.setEnterAnim(R.anim.nav_default_enter_anim)
                        .setExitAnim(R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.anim.nav_default_pop_exit_anim);

            } else {
                builder.setEnterAnim(R.animator.nav_default_enter_anim)
                        .setExitAnim(R.animator.nav_default_exit_anim)
                        .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.animator.nav_default_pop_exit_anim);
            }
//        if ((item.getOrder() & Menu.CATEGORY_SECONDARY) == 0) {
//            builder.setPopUpTo(findStartDestination(navController.getGraph()).getId(), false);
//        }
            NavOptions options = builder.setLaunchSingleTop(true).build();
            try {
                //TODO provide proper API instead of using Exceptions as Control-Flow.
                navController.navigate(item.getItemId(), null, options);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            // TODO
            int id = backStackEntry.getDestination().getId();
            String idString = String.valueOf(id);

            NavBackStackEntry last = backStack.getLast();
            if (backStackEntry.equals(last)) {
                navController.popBackStack(item.getItemId(), false);
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment instanceof NavHostFragment) {
                        Log.d(TAG, fragment.getTag() + fragment.toString());
                        NavHostFragment hostFragment = (NavHostFragment) fragment;
                        NavigatorProvider navigatorProvider = navController.getNavigatorProvider();
                        Navigator<?> navigator = navigatorProvider.getNavigator(backStackEntry.getDestination().getNavigatorName());
                        if (navigator instanceof FragmentNavigator) {
                            FragmentNavigator fragmentNavigator = (FragmentNavigator) navigator;
                            ArrayDeque<Integer> fragmentBackstack = getFragmentBackstack(fragmentNavigator);
                            int size = fragmentBackstack.size();
                            Log.d(TAG, "onNavDestinationSelected() called with: item = [" + item + "], navController = [" + navController + "]");
                        }
                        FragmentManager childFragmentManager = hostFragment.getChildFragmentManager();
                        List<Fragment> fragments1 = childFragmentManager.getFragments();
                        Log.d(TAG, "onNavDestinationSelected() called with: item = [" + item + "], navController = [" + navController + "]");
                        int backStackEntryCount = childFragmentManager.getBackStackEntryCount();
                        for (int i = 0; i < backStackEntryCount; i++) {
                            FragmentManager.BackStackEntry backStackRecord = childFragmentManager.getBackStackEntryAt(i);
                            String name = backStackRecord.getName();
                            String s = "-" + backStackEntry.getDestination().getId();
                            if (name.contains(s)) {
                                childFragmentManager.popBackStack(name, 0);
                                Log.d(TAG, "onNavDestinationSelected() called with: item = [" + item + "], navController = [" + navController + "]");
                            } else {

                            }
                        }
                    }
                }
//                backStack.remove(backStackEntry);
//                backStack.add(backStackEntry);
//                navController.popBackStack(item.getItemId(), false);
            }
            return true;
        }
    }

    private ArrayDeque<Integer> getFragmentBackstack(FragmentNavigator fragmentNavigator) {
        try {
            Field mBackStack = FragmentNavigator.class.getDeclaredField("mBackStack");
            mBackStack.setAccessible(true);
            ArrayDeque<Integer> o = (ArrayDeque<Integer>) mBackStack.get(fragmentNavigator);
            return o;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
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

    /**
     * Finds the actual start destination of the graph, handling cases where the graph's starting
     * destination is itself a NavGraph.
     */
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static NavDestination findStartDestination(@NonNull NavGraph graph) {
        NavDestination startDestination = graph;
        while (startDestination instanceof NavGraph) {
            NavGraph parent = (NavGraph) startDestination;
            startDestination = parent.findNode(parent.getStartDestination());
        }
        return startDestination;
    }

}