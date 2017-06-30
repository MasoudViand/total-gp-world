package com.gmail.fattazzo.formula1world.fragments.current.constructors.detail.pages.progress;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gmail.fattazzo.formula1world.R;
import com.gmail.fattazzo.formula1world.domain.F1Constructor;
import com.gmail.fattazzo.formula1world.domain.F1Result;
import com.gmail.fattazzo.formula1world.fragments.current.drivers.detail.pages.progress.RaceResultsItemView;
import com.gmail.fattazzo.formula1world.fragments.current.drivers.detail.pages.progress.RaceResultsItemView_;
import com.gmail.fattazzo.formula1world.service.DataService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.List;

@EFragment(R.layout.fragment_driver_progress)
public class ProgressConstructorFragment extends Fragment {

    @FragmentArg
    F1Constructor constructor;

    @ViewById(R.id.race_result_progress_main_layout)
    LinearLayout mainLayout;

    @ViewById
    FloatingActionButton refreshFab;

    @ViewById
    ProgressBar progressBar;

    @Bean
    DataService dataService;

    private List<F1Result> raceResults;

    public static ProgressConstructorFragment newInstance(F1Constructor constructor) {
        ProgressConstructorFragment progressFragment = new ProgressConstructorFragment_();
        Bundle args = new Bundle();
        args.putSerializable("constructor", constructor);
        progressFragment.setArguments(args);
        return progressFragment;
    }

    @AfterViews
    void init() {
        mainLayout.removeAllViews();

        load();
    }

    @Click
    void refreshFab() {
        dataService.clearConstructorRaceResultsCache(constructor);
        load();
    }

    @UiThread
    void startLoad() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Background
    void load() {
        startLoad();
        raceResults = dataService.loadConstructorRacesResult(constructor);
        updateUI();
    }

    @UiThread
    void updateUI() {
        try {
            if (mainLayout != null) {
                mainLayout.removeAllViews();

                // 1 table for each driver
                MultiValuedMap<String, F1Result> resultsMap = new ArrayListValuedHashMap<>();

                for (F1Result result : raceResults) {
                    String driverName = result.driver != null ? result.driver.getFullName() : "";
                    resultsMap.put(driverName, result);
                }

                for (String driverName : resultsMap.keySet()) {

                    TableLayout tableLayout = (TableLayout) getActivity().getLayoutInflater().inflate(R.layout.race_result_progress_table_layout, null).findViewById(R.id.progress_driver_table_layout);

                    // Title: driver name
                    TextView driverTextView = new TextView(getContext());
                    driverTextView.setText(driverName);

                    if (Build.VERSION.SDK_INT < 23) {
                        driverTextView.setTextAppearance(getContext(), R.style.TextAppearance_Medium);
                    } else {
                        driverTextView.setTextAppearance(R.style.TextAppearance_Medium);
                    }

                    tableLayout.addView(driverTextView, 0, new TableLayout.LayoutParams());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) driverTextView.getLayoutParams();
                    mlp.setMargins(0, 50, 0, 0);
                    driverTextView.setTypeface(null, Typeface.BOLD);

                    int rowNumber = 1;
                    for (F1Result result : resultsMap.get(driverName)) {
                        RaceResultsItemView row = RaceResultsItemView_.build(getActivity(), result, rowNumber);
                        tableLayout.addView(row, new TableLayout.LayoutParams());
                        rowNumber++;
                    }

                    mainLayout.addView(tableLayout);
                }

                //tableLayout.getChildAt(0).setVisibility(View.INVISIBLE);
            }
        } finally {
            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}