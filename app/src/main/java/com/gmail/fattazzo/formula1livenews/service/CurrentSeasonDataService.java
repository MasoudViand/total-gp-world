package com.gmail.fattazzo.formula1livenews.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gmail.fattazzo.formula1livenews.ergast.Ergast;
import com.gmail.fattazzo.formula1livenews.ergast.ErgastManager;
import com.gmail.fattazzo.formula1livenews.ergast.objects.Constructor;
import com.gmail.fattazzo.formula1livenews.ergast.objects.ConstructorStandings;
import com.gmail.fattazzo.formula1livenews.ergast.objects.Driver;
import com.gmail.fattazzo.formula1livenews.ergast.objects.DriverStandings;
import com.gmail.fattazzo.formula1livenews.ergast.objects.RaceResults;
import com.gmail.fattazzo.formula1livenews.ergast.objects.Schedule;
import com.gmail.fattazzo.formula1livenews.utils.Utils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author fattazzo
 *         <p/>
 *         date: 11/04/17
 */
@EBean(scope = EBean.Scope.Singleton)
public class CurrentSeasonDataService {

    @Bean
    ErgastManager ergastManager;

    @Bean
    Utils utils;

    @AfterInject
    void afterInjenction() {
        ergastManager.getErgast().setSeason(Ergast.CURRENT_SEASON);
    }

    /**
     * Load all driver of the current season.
     *
     * @return drivers
     */
    @NonNull
    public List<Driver> loadDrivers() {
        List<Driver> drivers;
        try {
            drivers = ergastManager.getDrivers();
        } catch (Exception e) {
            drivers = new ArrayList<>();
        }
        return drivers;
    }

    /**
     * Load all constructors of the current season.
     *
     * @return constructors
     */
    @NonNull
    public List<Constructor> loadConstructors() {
        List<Constructor> constructors;
        try {
            constructors = ergastManager.getConstructors();
        } catch (Exception e) {
            constructors = new ArrayList<>();
        }
        return constructors;
    }

    /**
     * @param driverId driver id
     * @return race results
     */
    @NonNull
    public List<RaceResults> loadDriverRacesResult(String driverId) {
        List<RaceResults> results;
        try {
            results = ergastManager.getDriverRacersResult(driverId);
        } catch (Exception e) {
            results = new ArrayList<>();
        }
        return results;
    }

    /**
     * @param constructorId constructor id
     * @return race results
     */
    @NonNull
    public List<RaceResults> loadConstructorRacesResult(String constructorId) {
        List<RaceResults> results;
        try {
            results = ergastManager.getConstructorRacersResult(constructorId);
        } catch (Exception e) {
            results = new ArrayList<>();
        }
        return results;
    }

    /**
     * Load current season race scheduled.
     *
     * @return current race
     */
    @Nullable
    public Schedule loadCurrentSchedule() {
        List<Schedule> schedules;
        try {
            schedules = ergastManager.getSchedules();
        } catch (Exception e) {
            schedules = new ArrayList<>();
        }

        Calendar currentDateEnd = Calendar.getInstance();
        currentDateEnd.add(Calendar.HOUR_OF_DAY, 2);
        String currentDate = DateFormatUtils.format(currentDateEnd, "yyyy-MM-dd'T'HH:mm:ss");

        for (Schedule schedule : schedules) {

            String scheudleDateUTC = schedule.getDate() + "T" + schedule.getTime();
            String scheduleDateLocal = utils.convertUTCDateToLocal(scheudleDateUTC, "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss");

            if (scheduleDateLocal.compareTo(currentDate) >= 0) {
                return schedule;
            }
        }

        return null;
    }

    /**
     * Load current season driver standings.
     *
     * @return current standings
     */
    @NonNull
    public List<DriverStandings> loadDriverStandings() {
        List<DriverStandings> driverStandingsList;
        try {
            driverStandingsList = ergastManager.getDriverStandings(Ergast.NO_ROUND);
        } catch (Exception e) {
            driverStandingsList = new ArrayList<>();
        }

        return driverStandingsList;
    }

    /**
     * Load the current leader.
     *
     * @return driver leader information
     */
    @Nullable
    public DriverStandings loadDriverLeader() {
        return ergastManager.getDriverLeader();
    }

    /**
     * Load current season constructor standings.
     *
     * @return current standings
     */
    @NonNull
    public List<ConstructorStandings> loadConstructorStandings() {
        List<ConstructorStandings> constructorStandingsList;
        try {
            constructorStandingsList = ergastManager.getConstructorStandings(Ergast.NO_ROUND);
        } catch (Exception e) {
            constructorStandingsList = new ArrayList<>();
        }

        return constructorStandingsList;
    }
}
