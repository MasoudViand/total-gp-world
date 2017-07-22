package com.gmail.fattazzo.formula1world.ergast.imagedb.service;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.gmail.fattazzo.formula1world.domain.F1Constructor;
import com.gmail.fattazzo.formula1world.domain.F1ConstructorStandings;
import com.gmail.fattazzo.formula1world.domain.F1Driver;
import com.gmail.fattazzo.formula1world.domain.F1DriverStandings;
import com.gmail.fattazzo.formula1world.domain.F1LapTime;
import com.gmail.fattazzo.formula1world.domain.F1PitStop;
import com.gmail.fattazzo.formula1world.domain.F1Qualification;
import com.gmail.fattazzo.formula1world.domain.F1Race;
import com.gmail.fattazzo.formula1world.domain.F1Result;
import com.gmail.fattazzo.formula1world.domain.F1Season;
import com.gmail.fattazzo.formula1world.ergast.Ergast;
import com.gmail.fattazzo.formula1world.ergast.IDataService;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Constructor;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.ConstructorColors;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.ConstructorStandings;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Driver;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.DriverConstructor;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.DriverStandings;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.LapTime;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.PitStop;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Qualification;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Race;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Result;
import com.gmail.fattazzo.formula1world.ergast.imagedb.objects.Season;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fattazzo
 *         <p/>
 *         date: 03/06/17
 */
@EBean(scope = EBean.Scope.Singleton)
public class LocalDBDataService implements IDataService {

    private static final String TAG = LocalDBDataService.class.getSimpleName();

    @Bean
    Ergast ergast;

    /**
     * Load the season.
     *
     * @param year year
     * @return season loaded
     */
    public F1Season loadSeason(int year) {
        F1Season f1Season = null;

        try {
            Season season = new Select().from(Season.class).where("Id = ?", year).executeSingle();
            if (season != null) {
                f1Season = season.toF1Season();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            f1Season = null;
        }

        return f1Season;
    }

    @Override
    public void updateSeason(F1Season season) {
        Season dbSeason = new Select().from(Season.class).where("Id = ?", season.year).executeSingle();
        if (dbSeason != null) {
            dbSeason.url = season.url;
            dbSeason.description = season.description;
            dbSeason.save();
        }
    }

    @NonNull
    @Override
    public List<F1Driver> loadDrivers() {
        List<F1Driver> drivers = new ArrayList<>();

        try {
            List<Driver> dbDrivers = new Select("drivers.*").distinct().from(Driver.class)
                    .innerJoin(DriverStandings.class).on("drivers.Id = driverStandings.driverId")
                    .innerJoin(Race.class).on("races.Id = driverStandings.raceId")
                    .where("races.year = ?", ergast.getSeason())
                    .orderBy("drivers.surname").execute();

            for (Driver dbDriver : dbDrivers) {
                drivers.add(dbDriver.toF1Driver());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            drivers = new ArrayList<>();
        }

        return drivers;
    }

    @NonNull
    @Override
    public List<F1Constructor> loadConstructors() {
        List<F1Constructor> constructors = new ArrayList<>();

        try {
            List<Constructor> dbConstructors = new Select("constructors.*").distinct().from(Constructor.class)
                    .innerJoin(ConstructorStandings.class).on("constructors.Id = constructorStandings.constructorId")
                    .innerJoin(Race.class).on("races.Id = constructorStandings.raceId")
                    .where("races.year = ?", ergast.getSeason())
                    .orderBy("constructors.name").execute();

            for (Constructor dbConstructor : dbConstructors) {
                constructors.add(dbConstructor.toF1Constructor());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            constructors = new ArrayList<>();
        }

        return constructors;
    }

    @NonNull
    @Override
    public List<F1Result> loadDriverRacesResult(F1Driver driver) {
        List<F1Result> results = new ArrayList<>();
        try {
            List<Result> dbResults = new Select("res.*").from(Result.class).as("res")
                    .innerJoin(Race.class).as("rac").on("rac.Id = res.raceId")
                    .innerJoin(Driver.class).as("dr").on("dr.Id = res.driverId")
                    .where("rac.year = ?", ergast.getSeason())
                    .where("dr.driverRef = ?", driver.driverRef)
                    .execute();
            for (Result result : dbResults) {
                results.add(result.toF1Result());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            results = new ArrayList<>();
        }
        return results;
    }

    @NonNull
    @Override
    public List<F1Result> loadConstructorRacesResult(F1Constructor constructor) {
        List<F1Result> results = new ArrayList<>();
        try {
            List<Result> dbResults = new Select("res.*").from(Result.class).as("res")
                    .innerJoin(Race.class).as("rac").on("rac.Id = res.raceId")
                    .innerJoin(Constructor.class).as("cs").on("cs.Id = res.constructorId")
                    .where("rac.year = ?", ergast.getSeason())
                    .where("cs.constructorRef = ?", constructor.constructorRef)
                    .execute();
            for (Result result : dbResults) {
                results.add(result.toF1Result());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            results = new ArrayList<>();
        }
        return results;
    }

    @NonNull
    @Override
    public List<F1DriverStandings> loadDriverStandings() {
        List<F1DriverStandings> f1DriverStandings = new ArrayList<>();

        try {
            List<DriverStandings> dbDriverStandings = new Select("drs.*").distinct()
                    .from(DriverStandings.class).as("drs")
                    .innerJoin(Race.class).on("races.Id = drs.raceId")
                    .where("races.year = ?", ergast.getSeason())
                    .orderBy("drs.points desc").groupBy("drs.driverId").execute();

            for (com.gmail.fattazzo.formula1world.ergast.imagedb.objects.DriverStandings dbDriverStanding : dbDriverStandings) {
                f1DriverStandings.add(dbDriverStanding.toF1DriverStandings(ergast.getSeason()));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            f1DriverStandings = new ArrayList<>();
        }

        return f1DriverStandings;
    }

    @Nullable
    @Override
    public F1DriverStandings loadDriverLeader() {
        F1DriverStandings f1DriverStandings;
        try {
            DriverStandings dbDriverStandings = new Select("drs.*").distinct()
                    .from(DriverStandings.class).as("drs")
                    .innerJoin(Race.class).on("races.Id = drs.raceId")
                    .where("races.year = ?", ergast.getSeason())
                    .orderBy("drs.points desc").groupBy("drs.driverId").limit(1).executeSingle();

            f1DriverStandings = dbDriverStandings.toF1DriverStandings(ergast.getSeason());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            f1DriverStandings = null;
        }
        return f1DriverStandings;
    }

    @NonNull
    @Override
    public List<F1ConstructorStandings> loadConstructorStandings() {
        List<F1ConstructorStandings> f1ConstructorStandings = new ArrayList<>();

        try {
            List<ConstructorStandings> dbConstructorStandings = new Select("cs.*").distinct()
                    .from(ConstructorStandings.class).as("cs")
                    .innerJoin(Race.class).on("races.Id = cs.raceId")
                    .where("races.year = ?", ergast.getSeason())
                    .orderBy("cs.points desc").groupBy("cs.constructorId").execute();

            for (ConstructorStandings dbConstructorStanding : dbConstructorStandings) {
                f1ConstructorStandings.add(dbConstructorStanding.toF1ConstructorStandings());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            f1ConstructorStandings = new ArrayList<>();
        }

        return f1ConstructorStandings;
    }

    @NonNull
    @Override
    public List<F1Race> loadRaces() {
        List<F1Race> f1Races = new ArrayList<>();

        try {
            List<Race> dbRaces = new Select("race.*")
                    .from(Race.class).as("race")
                    .where("race.year = ?", ergast.getSeason())
                    .orderBy("race.round").execute();

            for (Race race : dbRaces) {
                f1Races.add(race.toF1Race());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            f1Races = new ArrayList<>();
        }

        return f1Races;
    }

    @NonNull
    @Override
    public List<F1Result> loadRaceResult(F1Race race) {
        List<F1Result> results = new ArrayList<>();
        try {
            List<Result> dbResults = new Select("res.*").from(Result.class).as("res")
                    .innerJoin(Race.class).as("rac").on("rac.Id = res.raceId")
                    .where("rac.round = ?", race.round)
                    .where("rac.year = ?", ergast.getSeason())
                    .execute();
            for (Result result : dbResults) {
                results.add(result.toF1Result());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            results = new ArrayList<>();
        }
        return results;
    }

    @NonNull
    @Override
    public List<F1Qualification> loadQualification(F1Race race) {
        List<F1Qualification> results = new ArrayList<>();
        try {
            List<Qualification> dbResults = new Select("qual.*").from(Qualification.class).as("qual")
                    .innerJoin(Race.class).as("rac").on("rac.Id = qual.raceId")
                    .where("rac.round = ?", race.round)
                    .where("rac.year = ?", ergast.getSeason())
                    .execute();
            for (Qualification result : dbResults) {
                results.add(result.toF1Qualification());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            results = new ArrayList<>();
        }
        return results;
    }

    @NonNull
    @Override
    public List<F1PitStop> loadPitStops(F1Race race) {
        List<F1PitStop> results = new ArrayList<>();

        try {
            List<PitStop> dbResult = new Select("pits.*").from(PitStop.class).as("pits")
                    .innerJoin(Race.class).as("rac").on("rac.Id = pits.raceId")
                    .leftJoin(Driver.class).as("dr").on("dr.id = pits.driverId")
                    .where("rac.round = ?", race.round)
                    .where("rac.year = ?", ergast.getSeason())
                    .execute();
            for (PitStop result : dbResult) {
                results.add(result.f1PitStop());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            results = new ArrayList<>();
        }

        return results;
    }

    @Override
    public List<F1LapTime> loadLaps(@NonNull F1Race race, @NonNull F1Driver driver) {
        List<F1LapTime> results = new ArrayList<>();

        try {
            List<LapTime> dbResults = new Select("laps.*").from(LapTime.class).as("laps")
                    .innerJoin(Race.class).as("rac").on("rac.Id = laps.raceId")
                    .innerJoin(Driver.class).as("dr").on("dr.id = laps.driverId")
                    .where("dr.driverRef = ?", driver.driverRef)
                    .where("rac.round = ?", race.round)
                    .where("rac.year = ?", ergast.getSeason())
                    .execute();
            for (LapTime lapTime : dbResults) {
                F1LapTime f1LapTime = lapTime.toF1LapTime();
                results.add(f1LapTime);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            results = new ArrayList<>();
        }

        return results;
    }

    public Integer loadContructorColor(F1Constructor constructor) {
        Integer color = null;

        try {
            ConstructorColors constrColor = new Select("cc.*").from(ConstructorColors.class).as("cc")
                    .innerJoin(Constructor.class).as("cs").on("cs.Id = cc.constructorId")
                    .where("cc.year = ?", ergast.getSeason())
                    .where("cs.constructorRef = ?", constructor.constructorRef)
                    .executeSingle();
            if (constrColor != null) {
                color = Color.parseColor(constrColor.hex);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            color = null;
        }

        return color;
    }

    public Integer loadDriverColor(F1Driver driver) {
        Integer color = null;

        try {
            ConstructorColors constrColor = new Select("cc.*").from(ConstructorColors.class).as("cc")
                    .innerJoin(Driver.class).as("dr").on("dr.Id = cc.driverId")
                    .where("cc.year = ?", ergast.getSeason())
                    .where("dr.driverRef = ?", driver.driverRef)
                    .executeSingle();
            if (constrColor != null) {
                color = Color.parseColor(constrColor.hex);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            color = null;
        }

        return color;
    }

    public F1Constructor loadConstructor(F1Driver driver) {
        F1Constructor constructor = null;

        try {
            Constructor dbConstructor = new Select("constr.*").distinct().from(Constructor.class).as("constr")
                    .innerJoin(DriverConstructor.class).as("dc").on("constr.Id = dc.constructorId")
                    .innerJoin(Driver.class).as("driver").on("driver.Id = dc.driverId")
                    .where("dc.year = ?",ergast.getSeason())
                    .where("driver.driverRef = ?",driver.driverRef)
                    .executeSingle();
            if(dbConstructor != null) {
                constructor = dbConstructor.toF1Constructor();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            constructor = null;
        }

        return constructor;
    }

    public boolean hasLocalLapsData(F1Race race) {
        boolean localData = false;

        String sql = "SELECT COUNT(lapTimes.Id) as lapsData FROM lapTimes " +
                "inner join races on lapTimes.raceId = races.Id " +
                "where races.year = " + race.year +
                " and races.round = " + race.round;

        Cursor c = null;
        try {
            c = ActiveAndroid.getDatabase().rawQuery(sql, null);
            c.moveToFirst();
            int total = c.getInt(c.getColumnIndex("lapsData"));
            localData = total > 0;
        } catch (Exception e) {
            localData = false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return localData;
    }
}
