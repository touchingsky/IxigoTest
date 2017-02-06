package com.gaurav.ixigotest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaurav.ixigotest.R;
import com.gaurav.ixigotest.database.FlightDataBase;
import com.gaurav.ixigotest.model.FareModel;
import com.gaurav.ixigotest.model.FlightModel;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

/**
 * @author gaurav
 * @version 1.0
 * @since 4/2/17
 */
public class FlightAdapter extends CursorAdapter {

    final private DecimalFormat currencyFormatter = new DecimalFormat("##,##,###", DecimalFormatSymbols.getInstance(Locale.US));
    final private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    private final Map<String, String> airlines;
    private final Map<String, String> airport;
    private final SparseArray<String> providers;
    private int expandedPosition = -1;

    public int getExpandedPosition() {
        return expandedPosition;
    }

    public void setExpandedPosition(int expandedPosition) {
        this.expandedPosition = expandedPosition;
    }

    public FlightAdapter(Context context, Cursor cursor,
                         Map<String, String> airlines,
                         Map<String, String> airport,
                         SparseArray<String> providers) {
        super(context, cursor, false);
        this.airlines = airlines;
        this.airport = airport;
        this.providers = providers;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.flight_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int position = cursor.getPosition();
        FlightModel model = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(FlightDataBase.JSON_FORMAT)), FlightModel.class);
        ((TextView) view.findViewById(R.id.airline)).setText(airlines.get(model.getAirlineCode()));
        ((TextView) view.findViewById(R.id.travel_class)).setText(model.getClassType());

        ((TextView) view.findViewById(R.id.departure_place)).setText(airport.get(model.getDestinationCode()));
        ((TextView) view.findViewById(R.id.arrival_place)).setText(airport.get(model.getOriginCode()));

        ((TextView) view.findViewById(R.id.depature_time)).setText(timeFormatter.print(new DateTime(model.getDepartureTime())));
        ((TextView) view.findViewById(R.id.arrival_time)).setText(timeFormatter.print(new DateTime(model.getArrivalTime())));

        ((TextView) view.findViewById(R.id.price_sort)).setText(context.getString(R.string.inr,
                currencyFormatter.format(cursor.getLong(cursor.getColumnIndex(FlightDataBase.COLUMN_PRICE)))));

        final LinearLayout providerLayout = (LinearLayout) view.findViewById(R.id.providers_list);
        boolean isThisExpanded = expandedPosition == position;
        providerLayout.setVisibility(isThisExpanded? View.VISIBLE: View.GONE);
        providerLayout.removeAllViews();
        for (FareModel fareModel: model.getFares()) {
            View providerView = View.inflate(context, R.layout.flight_provider_item, null);
            ((TextView) providerView.findViewById(R.id.provider_name)).setText(providers.get(fareModel.getProviderId()));
            ((TextView) providerView.findViewById(R.id.provider_fare)).setText(context.getString(R.string.inr,
                    currencyFormatter.format(fareModel.getFare())));

            providerLayout.addView(providerView);
        }

        ImageView actionButton = (ImageView) view.findViewById(R.id.action_button);
        actionButton.setImageResource(isThisExpanded? R.drawable.ic_collapse: R.drawable.ic_expand);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedPosition = expandedPosition == position? -1: position;
                notifyDataSetChanged();
            }
        });
    }

    public void resetView() {
        expandedPosition = -1;
    }
}
