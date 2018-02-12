package com.example.amal.linkbike.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amal.linkbike.Objects.UserRide;
import com.example.amal.linkbike.R;

import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.MyViewHolder> {

    private List<UserRide> TripsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView StationName, StartDate, EndDate,Time,Fare;

        public MyViewHolder(View view) {
            super(view);
            StationName = (TextView) view.findViewById(R.id.StationName);
            StartDate = (TextView) view.findViewById(R.id.StartDate);
            EndDate = (TextView) view.findViewById(R.id.EndDate);
            Time = (TextView) view.findViewById(R.id.Duration);
            Fare = (TextView) view.findViewById(R.id.Fare);
        }
    }


    public TripsAdapter(List<UserRide> TripList) {
        this.TripsList = TripList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserRide userRide = TripsList.get(position);

        holder.Fare.setText("$"+String.valueOf(userRide.getFare())+" CAD");
        holder.StartDate.setText(userRide.getStartDate());
        holder.EndDate.setText(userRide.getEndDate());
        holder.Time.setText(userRide.getDuration());
        holder.StationName.setText(userRide.getStationDescripton());

    }

    @Override
    public int getItemCount() {
        return TripsList.size();
    }
}