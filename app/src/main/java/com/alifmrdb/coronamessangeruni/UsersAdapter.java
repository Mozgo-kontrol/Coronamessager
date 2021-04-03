package com.alifmrdb.coronamessangeruni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alifmrdb.coronamessangeruni.common.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private static final String TAG = "UsersAdapter";
    private DatabaseReference _databaseReference;
    // Reference for Storage
    private StorageReference _fileStorage;
    public interface UsersClickListener { void onUserItemClick(int clickedItemIndex); }

     private UsersClickListener mOnClickListener;


    private final List<User> usersList;
   // private Context context;
    private boolean preferenceDistanceIn;
    Activity activity;

    //------------------------------------------------------------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name, age, sex, distance, lastOnline, newMessage;
        public RelativeLayout relaitiveLayout;
        public ImageView pic;
        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.name);
            age = (TextView) view.findViewById(R.id.age);
            sex = (TextView) view.findViewById(R.id.sex);
            distance = (TextView) view.findViewById(R.id.distance);
            lastOnline = (TextView) view.findViewById(R.id.lastOnline);
            newMessage = (TextView) view.findViewById(R.id.new_message);
            pic = (ImageView) view.findViewById(R.id.users_images);
            relaitiveLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
            relaitiveLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onUserItemClick(clickedPosition);
        }
    }
    //-----------------------------------------------------------------------------------------------
    public UsersAdapter(List<User> usersList, UsersClickListener listener, boolean preferenceDistanceIn, Activity activity) {

        this.usersList = usersList;
        this.mOnClickListener = listener;
        this.preferenceDistanceIn = preferenceDistanceIn;
        this.activity = activity;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = usersList.get(position);
        holder.name.setText(user.getUserName());

        holder.age.setText(activity.getResources().getString(R.string.age_users_activity) + " " + String.valueOf(user.getUserAge()));
        if (user.getUserSex()) {
            holder.sex.setText(R.string.search_male);
        }
        else {
            holder.sex.setText(R.string.search_female);
        }
        if (user.getIsUserOnline()) {
            //holder.lastOnline.setTextColor(R.color.hell_blue);
            holder.lastOnline.setTextColor(ContextCompat.getColor(holder.lastOnline.getContext(), R.color.GREEN));
            holder.lastOnline.setText(R.string.online);
        }
        else {
            holder.lastOnline.setTextColor(Color.GRAY);
            long lastOnline = user.getLastUserOnline();
            if (lastOnline == 0) {
                holder.lastOnline.setText(R.string.last_online_no_data);
            }
            else {
                String data = String.valueOf(android.text.format.DateFormat.format("dd.MM.yy | HH:mm", user.getLastUserOnline()));
                holder.lastOnline.setText(activity.getResources().getString(R.string.last_online) + " " + data);
            }
        }
        int newMessage = user.getNewMessage();
        if (newMessage > 0) {
            holder.newMessage.setText(String.valueOf(newMessage));
            holder.newMessage.setVisibility(View.VISIBLE);
        }
        else {
            holder.newMessage.setVisibility(View.GONE);
        }

        double distance = user.getUserDistance();


        if (preferenceDistanceIn&& distance>= 1000) {holder.distance
                .setText(String.format(activity.getResources()
                .getString(R.string.distance_users_activity) + " " + "%.1f", distance/1000)
                + " " + activity.getResources().getString(R.string.km_users_activity));}

        else{
            holder.distance.setText(String.format(activity.getResources()
                .getString(R.string.distance_users_activity) +
                " " + "%.1f", distance) + " " + activity
                .getResources().getString(R.string.m_users_activity));}

        if(user.getUserPhoto()!=null){

            _fileStorage = FirebaseStorage.getInstance().getReference();
                    StorageReference fileRef = _fileStorage.child("images/" +user.getUserPhoto());
            fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                    Glide.with(activity).load(uri)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(holder.pic)).addOnFailureListener(e -> {
                    });}

        else {holder.pic.setImageResource(R.drawable.default_profile);}

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


}