package com.menard.go4lunch.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;
import com.menard.go4lunch.R;
import com.menard.go4lunch.controller.activity.LunchActivity;
import com.menard.go4lunch.model.PlaceAutocomplete;
import com.menard.go4lunch.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.PredictionHolder> implements Filterable {

    private Context mContext;
    private ArrayList<PlaceAutocomplete> mList = new ArrayList<>();
    private PlacesClient mPlacesClient;
    private LatLng bounds;

    public AutocompleteAdapter(Context context, PlacesClient placesClient, LatLng latLng) {
        mContext = context;
        mPlacesClient = placesClient;
        bounds = latLng;
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.autocomplete_item, parent, false);
        return new PredictionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder holder, int position) {
        holder.name.setText(mList.get(position).getPlaceName());
        holder.address.setText(mList.get(position).getPlaceAddress());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    mList = getResult(constraint);
                    if (mList != null) {
                        results.values = mList;
                        results.count = mList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
            }
        };
    }

    private ArrayList<PlaceAutocomplete> getResult(CharSequence query){
        ArrayList<PlaceAutocomplete> list = new ArrayList<>();
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        LatLng north = SphericalUtil.computeOffset(bounds, 7000, 225.0);
        LatLng south = SphericalUtil.computeOffset(bounds, 7000, 45.0);
        LatLngBounds latLngBounds = LatLngBounds.builder().include(north).include(south).build();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setLocationRestriction(RectangularBounds.newInstance(latLngBounds))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(query.toString().toLowerCase())
                .build();

        Task<FindAutocompletePredictionsResponse> responseTask = mPlacesClient.findAutocompletePredictions(request);

        try{
            Tasks.await(responseTask, 60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        if(responseTask.isSuccessful()){
            FindAutocompletePredictionsResponse response = responseTask.getResult();
            if(response != null){
                for(AutocompletePrediction prediction : response.getAutocompletePredictions()){
                    List<Place.Type> types = prediction.getPlaceTypes();
                    for(Place.Type type: types) {
                        if(type == Place.Type.RESTAURANT)
                        list.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(new StyleSpan(Typeface.BOLD)).toString(), prediction.getSecondaryText(new StyleSpan(Typeface.NORMAL)).toString()));
                    }
                }
            }
            return list;
        }else {
            return list;
        }
    }


    class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView address;

        PredictionHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.autocomplete_item_name);
            address = itemView.findViewById(R.id.autocomplete_item_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PlaceAutocomplete item = mList.get(getAdapterPosition());
            String id = item.getPlaceId();

            Intent intent = new Intent(mContext, LunchActivity.class);
            intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, id);
            mContext.startActivity(intent);
        }
    }


}
