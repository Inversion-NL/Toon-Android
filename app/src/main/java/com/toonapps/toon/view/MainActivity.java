package com.toonapps.toon.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.toonapps.toon.BuildConfig;
import com.toonapps.toon.R;
import com.toonapps.toon.helper.AppSettings;
import com.toonapps.toon.view.fragments.ControlsFragment;
import com.toonapps.toon.view.fragments.OnFragmentInteractionListener;
import com.toonapps.toon.view.fragments.UsageGraphFragment;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private NavController navController;
    private final int REQUEST_CODE_INTRO = 100;
    private final String REQUEST_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppSettings.getInstance().initialize(this.getApplicationContext());

        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        if (AppSettings.getInstance().isFirstStart()) {
            Intent intent = new Intent(this, ConnectionWizardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        } else {
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.navigationView);
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);

            String version = String.format(getString(R.string.drawer_app_version), BuildConfig.VERSION_NAME);
            Menu nav = navigationView.getMenu();
            MenuItem menuInfo = nav.findItem(R.id.menu_info);
            menuInfo.setTitle(version);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_refresh:
                updateDataInFragment();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                AppSettings.getInstance().setFirstStart(false);
                updateDataInFragment();
            } else {
                // Cancelled the intro. You can then e.g. finish this activity too.
                finish();
            }
        }
    }

    private void updateDataInFragment(){
        try {
            @SuppressWarnings("ConstantConditions")
            Fragment current =
                    getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment)
                            .getChildFragmentManager()
                            .getFragments()
                            .get(0);
            if (current instanceof ControlsFragment)
                ((ControlsFragment) current).updateData(false);
            if (current instanceof UsageGraphFragment)
                ((UsageGraphFragment) current).updateGraphData();
        } catch (Exception ignore){}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        Bundle bundle = new Bundle();

        switch (menuItem.getItemId()) {
            case R.id.menu_controls:
                startFragment(R.id.fragment_controls, bundle);
                break;

            case R.id.menu_elecUsage:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.ELEC);
                startFragment(R.id.fragment_usage_elec, bundle);
                break;

            case R.id.menu_gasUsage:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.GAS);
                startFragment(R.id.fragment_usage_gas, bundle);
                break;

            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.menu_openSource:
                startActivity(new Intent(this, OssLicensesMenuActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(int action) {
        Bundle bundle = new Bundle();

        switch (action) {
            case ACTION.START.ELEC_USAGE:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.ELEC);
                startFragment(R.id.fragment_usage_elec, bundle);
                break;
            case ACTION.START.GAS_USAGE:
                bundle.putInt(REQUEST_TYPE, UsageGraphFragment.TYPE.GAS);
                startFragment(R.id.fragment_usage_gas, bundle);
                break;
        }
    }

    private void startFragment(int fragmentId, Bundle bundle) {
        NavOptions.Builder navBuilder = new NavOptions.Builder();
        NavOptions navOptions;
        navOptions = navBuilder.setPopUpTo(fragmentId, true).build();
        // Clear the back stack

        navController.navigate(fragmentId, bundle, navOptions);
    }
}