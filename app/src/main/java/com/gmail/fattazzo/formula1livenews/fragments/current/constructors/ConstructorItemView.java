package com.gmail.fattazzo.formula1livenews.fragments.current.constructors;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.fattazzo.formula1livenews.R;
import com.gmail.fattazzo.formula1livenews.ergast.objects.Constructor;
import com.gmail.fattazzo.formula1livenews.utils.CountryNationality;
import com.gmail.fattazzo.formula1livenews.utils.ImageUtils;
import com.gmail.fattazzo.formula1livenews.utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.constructors_item_list)
public class ConstructorItemView extends LinearLayout {

    @Bean
    Utils utils;

    @Bean
    ImageUtils imageUtils;

    @ViewById(R.id.constructor_item_name)
    TextView nameView;

    @ViewById(R.id.constructor_item_flag)
    ImageView flagImageView;

    public ConstructorItemView(Context context) {
        super(context);
    }

    public void bind(Constructor constructor) {
        nameView.setText(constructor.getName());

        CountryNationality countryNationality = utils.getCountryNationality(constructor.getNationality());
        if(countryNationality != null) {
            flagImageView.setImageBitmap(imageUtils.getFlagForCountryCode(countryNationality.getAlpha2Code()));
        }
    }
}