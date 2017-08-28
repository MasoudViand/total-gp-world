package com.gmail.fattazzo.formula1world.fragments.stats;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gmail.fattazzo.formula1world.R;
import com.gmail.fattazzo.formula1world.ergast.imagedb.service.stats.StatsData;
import com.gmail.fattazzo.formula1world.service.StatisticsService;
import com.gmail.fattazzo.formula1world.utils.ThemeUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author fattazzo
 *         <p/>
 *         date: 24/08/17
 */
@EFragment(R.layout.fragment_stats_chart)
public abstract class AbstractStatsChartFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    @FragmentArg
    public int seasonStart;

    @FragmentArg
    public int seasonEnd;

    @Bean
    protected StatisticsService statisticsService;

    @Bean
    protected ThemeUtils themeUtils;

    @ViewById
    protected PieChart chart;

    @ViewById
    protected RelativeLayout chartLayout;

    @ViewById
    protected Switch percentageSwitch;

    @ViewById
    protected ListView dataListView;

    @AfterViews
    protected void initViews() {
        configureChart();

        if (percentageSwitch != null) {
            percentageSwitch.setOnCheckedChangeListener(this);
        }

        dataListView.setAdapter(new StatsDataListAdapter(getActivity(), new ArrayList<StatsData>(), getListValueFormat()));

        bindData();
    }

    private void configureChart() {
        if (chart != null) {
            chart.getDescription().setEnabled(false);

            SpannableString text = new SpannableString(getString(R.string.seasons) + "\n" + seasonStart + " - " + seasonEnd);
            chart.setCenterText(text);
            chart.setCenterTextColor(themeUtils.getThemeTextColor(getContext()));
            chart.setCenterTextSize(themeUtils.getThemeTextSize(getContext(),R.dimen.font_size_medium));

            chart.setDrawHoleEnabled(true);
            chart.setHoleColor(Color.TRANSPARENT);

            chart.setHoleRadius(58f);
            chart.setTransparentCircleRadius(58f);

            chart.setDrawCenterText(true);

            chart.setRotationEnabled(false);
            chart.setHighlightPerTapEnabled(true);

            chart.setMaxAngle(180f);
            chart.setRotationAngle(180f);
            chart.setCenterTextOffset(0, -40);

            chart.setEntryLabelColor(themeUtils.getThemeTextColor(getContext()));
            chart.setDrawEntryLabels(false);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setTextColor(themeUtils.getThemeTextColor(getContext()));
            l.setTextSize(themeUtils.getThemeTextSize(getContext(),R.dimen.font_size_small));
            l.setEnabled(true);

            chart.clear();
        }
    }

    private void setChartData(final List<StatsData> statsData) {
        ArrayList<PieEntry> values = new ArrayList<>();

        int entryNr = 0;
        for (StatsData stsdata : statsData) {
            values.add(new PieEntry(stsdata.getValue(), stsdata.getLabel()));
            entryNr++;

            if (entryNr == 5) {
                break;
            }
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(themeUtils.getThemeTextSize(getContext(), R.dimen.font_size_small));
        data.setValueTextColor(themeUtils.getThemeTextColor(getContext()));
        chart.setData(data);
        applyChartValueFormatter();

        chart.invalidate();
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void setListData(final List<StatsData> data) {
        DecimalFormat valueFormat = getListValueFormat();
        StatsDataListAdapter adapter = new StatsDataListAdapter(getActivity(), data, valueFormat);
        dataListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    void bindData() {
        List<StatsData> data = loadData();

        Collections.sort(data, new Comparator<StatsData>() {
            @Override
            public int compare(StatsData o1, StatsData o2) {
                return o1.getValue() < o2.getValue() ? 1 : o1.getValue() > o2.getValue() ? -1 : 0;
            }
        });

        if (chart != null) {
            setChartData(data);
        }
        setListData(data);
    }

    private void applyChartValueFormatter() {
        if (chart != null && chart.getData() != null) {
            chart.setUsePercentValues(percentageSwitch.isChecked());
            chart.getData().setValueFormatter(percentageSwitch.isChecked() ? new PercentFormatter() : getChartValueFormatter());
            chart.invalidate();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        applyChartValueFormatter();
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    protected abstract
    @NonNull
    List<StatsData> loadData();

    protected abstract
    @NonNull
    IValueFormatter getChartValueFormatter();

    protected abstract
    @NonNull
    DecimalFormat getListValueFormat();
}
