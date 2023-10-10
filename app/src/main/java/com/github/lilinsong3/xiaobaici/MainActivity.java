package com.github.lilinsong3.xiaobaici;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.lilinsong3.xiaobaici.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment == null) {
            super.finish();
        } else {
            NavController navController = navHostFragment.getNavController();
            navController.addOnDestinationChangedListener((@NonNull NavController controller,
                                                           @NonNull NavDestination destination,
                                                           @Nullable Bundle arguments) -> listenOnDestinationChanged(arguments));
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.myFragment).build();
            NavigationUI.setupWithNavController(binding.toolbarTop, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNav, navController);

            binding.globalLoading.circularLoading.setVisibilityAfterHide(View.GONE);

            mainActivityViewModel.distinctShowBottomView.observe(this, this::showBottomNavObserver);
            mainActivityViewModel.distinctShowTopToolbar.observe(this, this::showTopToolbarObserver);

            mainActivityViewModel.getAppBarTitle().observe(this, title -> {
                if (binding.toolbarTop.getVisibility() == View.VISIBLE && title != null && !title.isEmpty()) {
                    binding.toolbarTop.setTitle(title);
                }
            });

            mainActivityViewModel.getGlobalLoading().observe(this, showLoading -> {
                if (showLoading) {
                    binding.globalLoading.circularLoading.show();
                    return;
                }
                binding.globalLoading.circularLoading.hide();
            });
        }
    }

    private void listenOnDestinationChanged(@Nullable Bundle arguments) {
        setShowTopToolbarData(arguments);
        setShowBottomViewData(arguments);
    }

    private void setShowTopToolbarData(@Nullable Bundle arguments) {
        boolean showTopToolbar = false;
        if (arguments != null) {
            showTopToolbar = arguments.getBoolean("showDefaultToolbar", true);
        }
        mainActivityViewModel.makeTopToolbarShow(showTopToolbar);
    }

    private void setShowBottomViewData(@Nullable Bundle arguments) {
        boolean showBottomNav = false;
        if (arguments != null) {
            showBottomNav = arguments.getBoolean("showBottomNav", false);
        }
        mainActivityViewModel.makeBottomViewShow(showBottomNav);
    }

    private void showBottomNavObserver(@NonNull Boolean showView) {
        showView(binding.bottomNav, showView);
    }

    private void showTopToolbarObserver(@NonNull Boolean showView) {
        showView(binding.appbarLayoutTop, showView);
    }

    private void showView(@NonNull View view, @NonNull Boolean showView) {
        if (showView) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

}