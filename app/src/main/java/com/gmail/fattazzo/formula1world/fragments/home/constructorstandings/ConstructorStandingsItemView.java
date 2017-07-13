package com.gmail.fattazzo.formula1world.fragments.home.constructorstandings;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.fattazzo.formula1world.R;
import com.gmail.fattazzo.formula1world.domain.F1ConstructorStandings;
import com.gmail.fattazzo.formula1world.service.DataService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang3.ObjectUtils;

@EViewGroup(R.layout.standings_item_list)
public class ConstructorStandingsItemView extends LinearLayout {

    @Bean
    DataService dataService;

    @ViewById(R.id.standings_item_points)
    TextView pointsView;

    @ViewById(R.id.standings_item_name)
    TextView nameView;

    @ViewById(R.id.standings_item_color)
    ImageView teamColorView;

    public ConstructorStandingsItemView(Context context) {
        super(context);
    }

    public void bind(F1ConstructorStandings standings) {
        Float points = ObjectUtils.defaultIfNull(standings.points, 0f);
        boolean hasDecimals = points % 1 != 0;
        if (hasDecimals) {
            pointsView.setText(String.valueOf(points));
        } else {
            pointsView.setText(String.valueOf(points.intValue()));
        }

        if (standings.constructor != null) {
            nameView.setText(standings.constructor.name);
        }

        int color = dataService.loadContructorColor(standings.constructor);
        teamColorView.setColorFilter(color);
    }
}