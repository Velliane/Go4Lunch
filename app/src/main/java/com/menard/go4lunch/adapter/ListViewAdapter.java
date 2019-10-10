package com.menard.go4lunch.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.menard.go4lunch.BuildConfig;
import com.menard.go4lunch.R;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.controller.activity.LunchActivity;
import com.menard.go4lunch.model.User;
import com.menard.go4lunch.model.detailsrequest.DetailsRequest;
import com.menard.go4lunch.model.detailsrequest.ResultDetails;
import com.menard.go4lunch.utils.*;

import java.util.List;

import static com.menard.go4lunch.utils.PhotoUtilsKt.getProgressDrawableSpinner;
import static com.menard.go4lunch.utils.PhotoUtilsKt.loadRestaurantPhoto;
import static com.menard.go4lunch.utils.RestaurantUtilsKt.distanceToUser;
import static com.menard.go4lunch.utils.RestaurantUtilsKt.setRating;

/**
 * Adapter for the RecyclerView that's show the detailed list of nearby restaurants
 */

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder>{

    private final Context mContext;
    private final List<DetailsRequest> mList;
    public ListViewAdapter(List<DetailsRequest> list, Context context) {
        mContext = context;
        mList = list;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listview_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        DetailsRequest detailResult = mList.get(position);
        ResultDetails restaurant = detailResult.getResult();

        holder.nameRestaurant.setText(restaurant.getName());
        holder.styleAndAddress.setText(restaurant.getFormattedAddress());

        //-- Set opening hours --
        String opening;
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().getOpenNow()) {
               opening = "Open";
            } else {
                opening = "Close";
            }
        } else {
            opening = "No opening hours available";
        }
        holder.openingHours.setText(opening);

        //-- Set distance according to user --
        Location restaurantLocation = setLocation(restaurant.getGeometry().getLocation().getLat() , restaurant.getGeometry().getLocation().getLng());
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                Location userLocation = setLocation(Double.valueOf(currentUser.getUserLocationLatitude()), Double.valueOf(currentUser.getUserLocationLongitude()));
                holder.distance.setText(distanceToUser(restaurantLocation, userLocation));
            }
        });


        //-- Set restaurant's photo --
        if (restaurant.getPhotos() != null) {
            String reference = restaurant.getPhotos().get(0).getPhotoReference();
            String url = mContext.getString(R.string.photos_list_view, reference, BuildConfig.api_key_google);
            loadRestaurantPhoto(holder.photo, url, null, getProgressDrawableSpinner(mContext));
        } else {
            loadRestaurantPhoto(holder.photo, null, R.drawable.no_image_available_64, getProgressDrawableSpinner(mContext));
        }

        if(restaurant.getRating() != null) {
            int rating = setRating(Double.valueOf(restaurant.getRating()));
            setStarVisibility(rating, holder);
            holder.noRating.setVisibility(View.INVISIBLE);
        }else{
            holder.noRating.setVisibility(View.VISIBLE);
        }

    }


    /**
     * View Holder
     */
    class ListViewHolder extends RecyclerView.ViewHolder{

        private final TextView nameRestaurant;
        private final TextView styleAndAddress;
        private final TextView openingHours;
        private final TextView distance;
        private final ImageView photo;
        private final ImageView starOne;
        private final ImageView starTwo;
        private final ImageView starThree;
        private final TextView noRating;


        ListViewHolder(@NonNull View itemView) {
            super(itemView);

            nameRestaurant = itemView.findViewById(R.id.item_name_restaurant);
            styleAndAddress = itemView.findViewById(R.id.item_address);
            openingHours = itemView.findViewById(R.id.item_open_hours);
            distance = itemView.findViewById(R.id.item_distance);
            photo = itemView.findViewById(R.id.item_photo);
            starOne = itemView.findViewById(R.id.star_one);
            starThree = itemView.findViewById(R.id.star_three);
            starTwo  = itemView.findViewById(R.id.star_two);
            noRating = itemView.findViewById(R.id.star_null);

            itemView.setOnClickListener(view -> startLunchActivity());

        }


        /**
         * Start LunchActivity to show details on the selected restaurant, according to place_id
         */
        private void startLunchActivity() {
            String idRestaurant = mList.get(getAdapterPosition()).getResult().getPlaceId();
            Intent intent = new Intent(mContext, LunchActivity.class);
            intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, idRestaurant);
            mContext.startActivity(intent);
        }
    }


    /**
     * Set the visibility of the stars for rating
     */
    private void setStarVisibility(int rating, ListViewHolder holder) {

           if(rating == 1){
                holder.starOne.setVisibility(View.VISIBLE);
                holder.starTwo.setVisibility(View.GONE);
                holder.starThree.setVisibility(View.GONE);
            }else if(rating == 2) {
                holder.starOne.setVisibility(View.VISIBLE);
                holder.starTwo.setVisibility(View.VISIBLE);
                holder.starThree.setVisibility(View.GONE);
            }else if(rating == 3) {
                holder.starOne.setVisibility(View.VISIBLE);
                holder.starTwo.setVisibility(View.VISIBLE);
                holder.starThree.setVisibility(View.VISIBLE);
            }else {
                holder.starOne.setVisibility(View.GONE);
                holder.starTwo.setVisibility(View.GONE);
                holder.starThree.setVisibility(View.GONE);
            }

    }

    private Location setLocation(Double latitude, Double longitude){
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}