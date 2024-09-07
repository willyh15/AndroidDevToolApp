package com.example.androiddevtoolapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the navigation component with the bottom navigation view
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Initialize BottomNavigationView and connect it with NavController
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Listener for fragment changes to dynamically update toolbar title
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.SSHFragment:
                    setTitle("SSH Manager");
                    break;
                case R.id.GitHubRepoFragment:
                    setTitle("GitHub Repositories");
                    break;
                case R.id.GitRepoFragment:
                    setTitle("Local Git Repositories");
                    break;
                case R.id.FileManagementFragment:
                    setTitle("File Management");
                    break;
                case R.id.ScheduleTaskFragment:
                    setTitle("Task Scheduler");
                    break;
                default:
                    setTitle("Android Dev Tool App");
                    break;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp();
    }

    // Handle back press with custom fragment behavior
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (currentFragment instanceof SSHFragment) {
            // Handle specific SSHFragment back press logic
            // Example: Show a dialog or perform cleanup
        } else if (currentFragment instanceof GitHubRepoFragment) {
            // Handle GitHubRepoFragment specific logic
        } else if (currentFragment instanceof FileManagementFragment) {
            // Handle FileManagementFragment specific back behavior
        } else {
            // Implement double-tap to exit functionality
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            // Reset the back press flag after 2 seconds
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    // Additional helper for fragment transitions (Optional UI Enhancements)
    private void navigateToFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
