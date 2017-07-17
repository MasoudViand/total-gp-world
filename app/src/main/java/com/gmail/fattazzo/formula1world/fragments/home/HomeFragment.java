package com.gmail.fattazzo.formula1world.fragments.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.dspot.declex.api.eventbus.Event;
import com.gmail.fattazzo.formula1world.R;
import com.gmail.fattazzo.formula1world.activity.dbimport.DBImportActivity_;
import com.gmail.fattazzo.formula1world.activity.settings.SettingsActivity_;
import com.gmail.fattazzo.formula1world.ergast.imagedb.importer.ErgastDBImporter;
import com.gmail.fattazzo.formula1world.fragments.home.circuit.CurrentCircuitTask;
import com.gmail.fattazzo.formula1world.fragments.home.constructorstandings.CurrentConstructorStandingsTask;
import com.gmail.fattazzo.formula1world.fragments.home.driverstandings.CurrentDriverStandingsTask;
import com.gmail.fattazzo.formula1world.service.DataService;
import com.gmail.fattazzo.formula1world.settings.ApplicationPreferenceManager;
import com.gmail.fattazzo.formula1world.utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;

import static com.gmail.fattazzo.formula1world.activity.home.HomeActivity.PREF_ACTIVITY_RESULT;

/**
 * @author fattazzo
 *         <p/>
 *         date: 15/04/17
 */
@OptionsMenu(R.menu.home)
@EFragment(R.layout.content_home)
public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @Bean
    Utils utils;

    @Bean
    ErgastDBImporter dbImporter;

    @Bean
    ApplicationPreferenceManager preferenceManager;

    @Bean
    CurrentCircuitTask currentCircuitTask;

    @Bean
    CurrentDriverStandingsTask currentDriverStandingsTask;

    @Bean
    CurrentConstructorStandingsTask currentConstructorStandingsTask;

    @Bean
    DataService dataService;

    @Override
    public void onResume() {
        super.onResume();
        if(!dataService.importDBIfRequired()) {
            currentCircuitTask.loadCurrentSchedule();
            currentDriverStandingsTask.loadCurrentStandings();
            currentConstructorStandingsTask.loadCurrentStandings();
        }
    }

    @Click
    void fab() {
        dataService.clearColorsCache();

        currentCircuitTask.loadCurrentSchedule(true);
        currentDriverStandingsTask.loadCurrentStandings(true);
        currentConstructorStandingsTask.loadCurrentStandings(true);
    }

    @Event
    void onBackPressedEvent() {
        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                SettingsActivity_.intent(getContext()).startForResult(PREF_ACTIVITY_RESULT);
                return true;
            case R.id.action_import_dbimage:
                Intent i = new Intent(getContext(), DBImportActivity_.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);

                dbImporter.importDBImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
