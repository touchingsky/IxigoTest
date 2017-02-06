package com.gaurav.ixigotest.activity;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.gaurav.ixigotest.adapter.FlightAdapter;
import com.gaurav.ixigotest.R;
import com.gaurav.ixigotest.database.FlightDataBase;
import com.gaurav.ixigotest.database.FlightProvider;
import com.gaurav.ixigotest.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Drawable drawableUpArrow, drawableDownArrow;
    private Map<String, String> airlines, airports;
    private SparseArray<String> providers;
    private String selection = FlightDataBase.COLUMN_PRICE;
    private FlightAdapter adapter;
    private View sortedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        drawableUpArrow = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_up_arrow);
        drawableDownArrow = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_down_arrow);

        sortedView = binding.priceSort;
        setCompoundDrawablesWithIntrinsicBounds(binding.priceSort, drawableUpArrow);

        String jsonString = loadJson();
        if (jsonString == null) {
            finish();
            return;
        }

        adapter = new FlightAdapter(this, null, airlines, airports, providers);
        binding.flighList.setAdapter(adapter);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first", true)) {
            new JsonLoader().execute(jsonString);
        } else {
            getSupportLoaderManager().initLoader(1, null, this);
        }
    }

    public void onSort(View view) {
        view.setEnabled(false);
        int tag = Integer.parseInt((String) view.getTag());
        boolean ascending = tag == 1 && sortedView == view;
        switch (view.getId()) {
            case R.id.price_sort:
                if (sortedView.getId() != R.id.price_sort) {
                    setCompoundDrawablesWithIntrinsicBounds((TextView) sortedView, null);
                }
                selection = FlightDataBase.COLUMN_PRICE + (ascending? " desc": "");
                break;

            case R.id.departure_sort:
                if (sortedView.getId() != R.id.departure_sort) {
                    setCompoundDrawablesWithIntrinsicBounds((TextView) sortedView, null);
                }
                selection = FlightDataBase.COLUMN_DEPARTURE + (ascending? " desc": "");
                break;

            case R.id.arrival_sort:
                if (sortedView.getId() != R.id.arrival_sort) {
                    setCompoundDrawablesWithIntrinsicBounds((TextView) sortedView, null);
                }
                selection = FlightDataBase.COLUMN_ARRIVAL + (ascending? " desc": "");
                break;
        }
        sortedView = view;
        setCompoundDrawablesWithIntrinsicBounds((TextView) sortedView, ascending? drawableDownArrow: drawableUpArrow);
        view.setTag(ascending? "0": "1");
        getSupportLoaderManager().restartLoader(1, null, this);
        view.setEnabled(true);
    }

    private void setCompoundDrawablesWithIntrinsicBounds(TextView textView, Drawable drawable) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    private String loadJson() {
        String jsonString;
        try {
            AssetManager manager = getAssets();
            InputStream file = manager.open("data.json");
            byte[] formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
            jsonString = new String(formArray);

            if (TextUtils.isEmpty(jsonString)) {
                return null;
            }

            JSONObject parentObject = new JSONObject(jsonString);
            JSONObject appendix = parentObject.getJSONObject("appendix");
            JSONObject airlinesJson = appendix.getJSONObject("airlines");
            Iterator<String> airlinesIterator = airlinesJson.keys();
            airlines = new HashMap<>();
            do {
                String key = airlinesIterator.next();
                airlines.put(key, airlinesJson.getString(key).replace(" ", "\n"));

            } while (airlinesIterator.hasNext());

            JSONObject airportsJson = appendix.getJSONObject("airports");
            Iterator<String> airportIterator = airportsJson.keys();
            airports = new HashMap<>();
            do {
                String key = airportIterator.next();
                airports.put(key, airportsJson.getString(key));

            } while (airportIterator.hasNext());

            JSONObject providersJson = appendix.getJSONObject("providers");
            Iterator<String> providersIterator = providersJson.keys();
            providers = new SparseArray<>();
            do {
                String key = providersIterator.next();
                providers.put(Integer.parseInt(key), providersJson.getString(key));

            } while (providersIterator.hasNext());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putInt("position", adapter.getExpandedPosition());
        } catch (NullPointerException ignored) {}
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            adapter.setExpandedPosition(savedInstanceState.getInt("position"));
        } catch (NullPointerException ignored) {}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FlightProvider.CONTENT_URI,
                new String[]{"_id", FlightDataBase.JSON_FORMAT, FlightDataBase.COLUMN_PRICE},
                null, null, selection);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.resetView();
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private class JsonLoader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String jsonString = params[0];

            try {
                JSONObject parentObject = new JSONObject(jsonString);
                JSONArray flightArray = parentObject.getJSONArray("flights");
                for (int index=0; index<flightArray.length(); index++) {
                    JSONObject flightObject = flightArray.getJSONObject(index);
                    long minFare = 0;
                    JSONArray fareArray = flightObject.getJSONArray("fares");
                    for (int position=0; position<fareArray.length(); position++) {
                        JSONObject fareObject = fareArray.getJSONObject(position);
                        long fare = fareObject.getInt("fare");
                        if (minFare < fare) {
                            minFare = fare;
                        }
                    }

                    ContentValues values = new ContentValues();
                    values.put(FlightDataBase.COLUMN_PRICE, minFare);
                    values.put(FlightDataBase.COLUMN_DEPARTURE, flightObject.getLong("departureTime"));
                    values.put(FlightDataBase.COLUMN_ARRIVAL, flightObject.getLong("arrivalTime"));
                    values.put(FlightDataBase.JSON_FORMAT, flightObject.toString());

                    getContentResolver().insert(FlightProvider.CONTENT_URI, values);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("first", false).apply();

            getSupportLoaderManager().initLoader(1, null, MainActivity.this);
        }
    }
}
