package com.menard.go4lunch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.menard.go4lunch.R;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.model.Message;
import com.menard.go4lunch.model.User;

import org.threeten.bp.LocalDateTime;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.menard.go4lunch.utils.DateUtilsKt.parseMessageDateToDateOnly;
import static com.menard.go4lunch.utils.DateUtilsKt.parseMessageDateToHoursOnly;
import static com.menard.go4lunch.utils.PhotoUtilsKt.getProgressDrawableSpinner;
import static com.menard.go4lunch.utils.PhotoUtilsKt.loadImageProfile;

/**
 * Adapter for the RecyclerView that's show the chat between users and their messages got from Firebase,
 * using FirestoreRecyclerAdapter and FirestoreRecyclerOptions
 */

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder> {

    private Context mContext;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(Context context, @NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
        mContext = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //-- Add two ViewType --
        View view;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.chat_item, parent, false);
        } else if (viewType == 1) {
            view = inflater.inflate(R.layout.chat_item_self, parent, false);
        } else {
            throw new IllegalStateException();
        }
        return new ChatViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        //-- Select ViewType according to userId --
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId.equals(getItem(position).getUserId())) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Message message) {
        //-- Message --
        holder.message.setText(message.getMessage());

        //-- Date parsed --
        String today = parseMessageDateToDateOnly(LocalDateTime.now().withSecond(2).withNano(0).toString());
        String dateMessage = parseMessageDateToDateOnly(message.getDate());
        if (!today.equals(dateMessage)) {
            holder.date.setText(dateMessage);
        } else {
            holder.date.setText(parseMessageDateToHoursOnly(message.getDate()));
        }

        //-- Profile's photo
        UserHelper.getUser(message.getUserId()).addOnSuccessListener(documentSnapshot -> {
            User currentUser = documentSnapshot.toObject(User.class);
            String photoUrl = currentUser.getUserPhoto();
            if (photoUrl != null) {
                loadImageProfile(holder.userPhoto, photoUrl, null, getProgressDrawableSpinner(mContext));
            } else {
                loadImageProfile(holder.userPhoto, null, R.drawable.user, getProgressDrawableSpinner(mContext));
            }
        });

    }


    class ChatViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userPhoto;
        private TextView message;
        private TextView date;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.image_profile);
            message = itemView.findViewById(R.id.chat_item_message);
            date = itemView.findViewById(R.id.chat_item_date);
        }
    }


}