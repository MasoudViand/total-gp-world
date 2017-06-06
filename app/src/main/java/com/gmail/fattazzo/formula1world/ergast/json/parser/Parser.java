package com.gmail.fattazzo.formula1world.ergast.json.parser;

import android.support.annotation.NonNull;

import com.gmail.fattazzo.formula1world.ergast.json.objects.ConstructorStandings;
import com.gmail.fattazzo.formula1world.ergast.json.objects.DriverStandings;
import com.gmail.fattazzo.formula1world.ergast.json.objects.Qualification;
import com.gmail.fattazzo.formula1world.ergast.json.objects.RaceResult;
import com.google.gson.*;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Parser<T> {
    private String json;
    private String[] jsonObjects;
    private Class<T> type;

    public Parser(String json, String[] jsonObjects, Class<T> type) {
        this.json = json;
        this.jsonObjects = jsonObjects;
        this.type = type;
    }

    @NonNull
    public List<T> parse() {
        List<T> entities = new ArrayList<>();

        if(StringUtils.isNotBlank(json)) {
            fixJson();
            JsonArray jarray = getJsonArray();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setExclusionStrategies(new FieldExclusionStrategy()).create();
            for (int i = 0; i < jarray.size(); i++) {
                entities.add(gson.fromJson(jarray.get(i).getAsJsonObject(), type));
            }
        }

        return entities;
    }

    private JsonArray getJsonArray() {
        JsonElement jelement = new JsonParser().parse(json);
        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("MRData");

        if (type == RaceResult.class || type == Qualification.class
                || type == DriverStandings.class || type == ConstructorStandings.class) {
            for (int i = 0; i < jsonObjects.length - 2; i++) {
                jobject = jobject.getAsJsonObject(jsonObjects[i]);
            }
            jobject = jobject.getAsJsonArray(jsonObjects[jsonObjects.length - 2]).get(0).getAsJsonObject();
        } else {
            for (int i = 0; i < jsonObjects.length - 1; i++) {
                jobject = jobject.getAsJsonObject(jsonObjects[i]);
            }
        }
        return jobject.getAsJsonArray(jsonObjects[jsonObjects.length - 1]);
    }

    private void fixJson() {
        json = json.
                replace("\"Location\"", "\"location\"").
                replace("\"Circuit\"", "\"circuit\"").
                replace("\"Constructor\"", "\"constructor\"").
                replace("\"Driver\"", "\"driver\"").
                replace("\"Time\"", "\"time\"").
                replace("\"AverageSpeed\"", "\"averageSpeed\"").
                replace("\"FastestLap\"", "\"fastestLap\"").
                replace("\"Q1\"", "\"q1\"").
                replace("\"Q2\"", "\"q2\"").
                replace("\"Q3\"", "\"q3\"").
                replace("\"Constructors\"", "\"constructors\"").
                replace("\"Laps\"", "\"laps\"").
                replace("\"Timings\"", "\"timings\"").
                replace("\"PitStops\"", "\"pitStops\"").
                replace("\"Results\"", "\"results\"");
    }
}
