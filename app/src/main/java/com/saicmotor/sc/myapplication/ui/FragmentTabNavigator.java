package com.saicmotor.sc.myapplication.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 该实现仅适用于和{@link BottomNavigationView}配合使用的情况下，复用之前实例，类似与{@link android.app.Activity}
 * 的{@link android.content.Intent#FLAG_ACTIVITY_SINGLE_TOP}与{@link android.content.Intent#FLAG_ACTIVITY_CLEAR_TOP}
 * 组合使用，或者Activity的{@code  android:launchMode="singleInstance"}效果
 * 注意在在使用的时候请在对应的fragment的启动{@link Bundle}中加入{@link #FLAG_FRAGMENT_SINGLETON}并设置为true，对于没有添加
 * 该设置的fragment，处于行为保持与{@link FragmentNavigator}一致。
 * <p>
 * 注意该实现没有依赖{@link FragmentManager#popBackStack()},无法在多页面中实现返回效果，
 * 其他情况请使用{@link FragmentNavigator}，它依赖{@link FragmentManager}的返回堆栈，实现了实例复用。
 *
 * 注：copy from {@link FragmentNavigator}
 *
 * @
 */
@Navigator.Name("fragment_tab")
public class FragmentTabNavigator extends Navigator<FragmentNavigator.Destination> {
    private static final String TAG = "FragmentNavigator";
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment-tab:navigator:backStackIds";

    public static final String FLAG_FRAGMENT_SINGLETON = "FLAG_FRAGMENT_SINGLETON";

    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final int mContainerId;
    private ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private LinkedHashMap<String, Fragment> mSingletonFragment = new LinkedHashMap<>();

    public FragmentTabNavigator(@NonNull Context context, @NonNull FragmentManager manager,
                                int containerId) {
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method must call
     * {@link FragmentTransaction#setPrimaryNavigationFragment(Fragment)}
     * if the pop succeeded so that the newly visible Fragment can be retrieved with
     * {@link FragmentManager#getPrimaryNavigationFragment()}.
     * <p>
     * Note that the default implementation pops the Fragment
     * asynchronously, so the newly visible Fragment from the back stack
     * is not instantly available after this call completes.
     */
    @Override
    public boolean popBackStack() {
        if (mBackStack.isEmpty()) {
            return false;
        }
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already"
                    + " saved its state");
            return false;
        }
        mFragmentManager.popBackStack(
                generateBackStackName(mBackStack.size(), mBackStack.peekLast()),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mBackStack.removeLast();
        return true;
    }

    @NonNull
    @Override
    public FragmentNavigator.Destination createDestination() {
        return new FragmentNavigator.Destination(this);
    }

    /**
     * Instantiates the Fragment via the FragmentManager's
     * {@link androidx.fragment.app.FragmentFactory}.
     * <p>
     * Note that this method is <strong>not</strong> responsible for calling
     * {@link Fragment#setArguments(Bundle)} on the returned Fragment instance.
     *
     * @param context         Context providing the correct {@link ClassLoader}
     * @param fragmentManager FragmentManager the Fragment will be added to
     * @param className       The Fragment to instantiate
     * @param args            The Fragment's arguments, if any
     * @return A new fragment instance.
     * @deprecated Set a custom {@link androidx.fragment.app.FragmentFactory} via
     * {@link FragmentManager#setFragmentFactory(FragmentFactory)} to control
     * instantiation of Fragments.
     */
    @SuppressWarnings("DeprecatedIsStillUsed") // needed to maintain forward compatibility
    @Deprecated
    @NonNull
    public Fragment instantiateFragment(@NonNull Context context,
                                        @NonNull FragmentManager fragmentManager,
                                        @NonNull String className, @SuppressWarnings("unused") @Nullable Bundle args) {
        return fragmentManager.getFragmentFactory().instantiate(
                context.getClassLoader(), className);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method should always call
     * {@link FragmentTransaction#setPrimaryNavigationFragment(Fragment)}
     * so that the Fragment associated with the new destination can be retrieved with
     * {@link FragmentManager#getPrimaryNavigationFragment()}.
     * <p>
     * Note that the default implementation commits the new Fragment
     * asynchronously, so the new Fragment is not instantly available
     * after this call completes.
     */
    @SuppressWarnings("deprecation") /* Using instantiateFragment for forward compatibility */
    @Nullable
    @Override
    public NavDestination navigate(@NonNull FragmentNavigator.Destination destination, @Nullable Bundle args,
                                   @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = mContext.getPackageName() + className;
        }
        final Fragment frag;

        boolean isSingleton = false;
        if (args != null) {
            isSingleton = args.getBoolean(FLAG_FRAGMENT_SINGLETON, false);
        }
        int destinationId = destination.getId();
        String tag = Integer.toHexString(destinationId);

        if (isSingleton && mSingletonFragment.containsKey(tag)) {
            frag = mSingletonFragment.get(tag);
        } else {
            frag = instantiateFragment(mContext, mFragmentManager,
                    className, args);
        }
        frag.setArguments(args);
        final FragmentTransaction ft = mFragmentManager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

        Fragment primaryNavigationFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (primaryNavigationFragment != null)
            if (mSingletonFragment.containsValue(primaryNavigationFragment)) {
                ft.detach(primaryNavigationFragment);
            } else {
                ft.remove(primaryNavigationFragment);
            }
        if (isSingleton && mSingletonFragment.containsValue(frag)) {
            ft.attach(frag);
        } else {
            ft.add(mContainerId, frag, tag);
        }
//        ft.replace(mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);

        final @IdRes int destId = destination.getId();
        final boolean initialNavigation = mBackStack.isEmpty();
        // TODO Build first class singleTop behavior for fragments
        final boolean isSingleTopReplacement = navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId;

        boolean isAdded;
        if (initialNavigation) {
            isAdded = true;
        } else if (!isSingleton) {
            if (isSingleTopReplacement) {
                // Single Top means we only want one instance on the back stack
                if (mBackStack.size() > 1) {
                    // If the Fragment to be replaced is on the FragmentManager's
                    // back stack, a simple replace() isn't enough so we
                    // remove it from the back stack and put our replacement
                    // on the back stack in its place
                    mFragmentManager.popBackStack(
                            generateBackStackName(mBackStack.size(), mBackStack.peekLast()),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    ft.addToBackStack(generateBackStackName(mBackStack.size(), destId));
                }
                isAdded = false;
            } else {
                ft.addToBackStack(generateBackStackName(mBackStack.size() + 1, destId));
                isAdded = true;
            }
        } else {
            isAdded = false;
        }

        if (navigatorExtras instanceof FragmentNavigator.Extras) {
            FragmentNavigator.Extras extras = (FragmentNavigator.Extras) navigatorExtras;
            for (Map.Entry<View, String> sharedElement : extras.getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();

        if (isSingleton) {
            mSingletonFragment.put(tag, frag);
        }
        // The commit succeeded, update our view of the world
        if (isAdded) {
            mBackStack.add(destId);
            return destination;
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[mBackStack.size()];
        int index = 0;
        for (Integer id : mBackStack) {
            backStack[index++] = id;
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        Bundle fragmentState = new Bundle();
        for (Map.Entry<String, Fragment> fragmentEntry : mSingletonFragment.entrySet()) {
            Fragment.SavedState savedState = mFragmentManager.saveFragmentInstanceState(fragmentEntry.getValue());
            fragmentState.putParcelable(fragmentEntry.getKey(), savedState);
        }
        b.putBundle("key_singleton_fragment", fragmentState);
        return b;
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        if (savedState != null) {
            int[] backStack = savedState.getIntArray(KEY_BACK_STACK_IDS);
            if (backStack != null) {
                mBackStack.clear();
                for (int destId : backStack) {
                    mBackStack.add(destId);
                }
            }
            Bundle savedStateBundle = savedState.getBundle("key_singleton_fragment");
            Set<String> strings = savedStateBundle.keySet();
            for (String tag : strings) {
                Log.d(TAG, "onRestoreState() called with: tag = [" + tag + "]");
                Fragment fragment = mFragmentManager.findFragmentByTag(tag);
                mSingletonFragment.put(tag,fragment);
            }
        }
    }

    @NonNull
    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }
}
