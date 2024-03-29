package com.menard.go4lunch.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.menard.go4lunch.R;
import com.menard.go4lunch.controller.activity.LunchActivity;
import com.menard.go4lunch.model.User;
import com.menard.go4lunch.utils.Constants;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.menard.go4lunch.utils.PhotoUtilsKt.getProgressDrawableSpinner;
import static com.menard.go4lunch.utils.PhotoUtilsKt.loadImageProfile;

/**
 * Adapter for the RecyclerView that's show the list of workmates and the restaurant there're choosed,
 * using FirestoreRecyclerAdapter and FirestoreRecyclerOptions
 */

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder> {

    private final Context mContext;
    private final boolean mAllUser;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     * @param options options
     */
    public WorkmatesAdapter(Context context, @NonNull FirestoreRecyclerOptions<User> options, boolean allUser) {
        super(options);
        mContext = context;
        mAllUser = allUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int i, @NonNull User user) {
        //-- Hide item if it's current user's information --
        if (user.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.container.getLayoutParams();
            param.height = 0;
            param.width = RecyclerView.LayoutParams.MATCH_PARENT;
            holder.itemView.setLayoutParams(param);

            //-- Show the other users's information --
        } else {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.container.getLayoutParams();
            param.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            param.width = RecyclerView.LayoutParams.MATCH_PARENT;
            holder.itemView.setLayoutParams(param);
            //--Profile 's photo --
            if (user.getUserPhoto() != null) {
                loadImageProfile(holder.userPhoto, user.getUserPhoto(), null, getProgressDrawableSpinner(mContext));
            } else {
                loadImageProfile(holder.userPhoto, null, R.drawable.user, getProgressDrawableSpinner(mContext));
            }
            holder.itemView.setTag(user.getUserRestaurantId());
            //-- Restaurant's selected --
            if (mAllUser) {
                String restaurantChoosed;
                if (!user.getUserRestaurantName().equals("")) {
                    restaurantChoosed = mContext.getString(R.string.workmates_infos_with_restaurant, user.getUserName(), user.getUserRestaurantName());
                    holder.userInfos.setText(restaurantChoosed);
                    holder.userInfos.setTextColor(Color.BLACK);
                    holder.userInfos.setTypeface(Typeface.DEFAULT);
                } else {
                    holder.userInfos.setTextColor(Color.GRAY);
                    holder.userInfos.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                    restaurantChoosed = mContext.getString(R.string.workmates_infos_not_yet, user.getUserName());
                    holder.userInfos.setText(restaurantChoosed);
                }
            } else {
                holder.userInfos.setText(mContext.getString(R.string.is_joining, user.getUserName()));
            }
        }

    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.workmates_item, parent, false);
        return new WorkmatesViewHolder(view);
    }


    class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView userPhoto;
        private final TextView userInfos;
        private final ConstraintLayout container;

        WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.image_profile);
            userInfos = itemView.findViewById(R.id.workmates_infos);
            container = itemView.findViewById(R.id.workmates_item_container);

            itemView.setOnClickListener(v -> {
                if(!itemView.getTag().toString().equals("")) {
                    String idRestaurant = itemView.getTag().toString();
                    Intent intent = new Intent(mContext, LunchActivity.class);
                    intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, idRestaurant);
                    mContext.startActivity(intent);
                }
            });
        }


    }

}