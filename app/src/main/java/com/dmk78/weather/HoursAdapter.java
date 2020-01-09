package com.dmk78.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk78.weather.model.Day;

import java.util.List;

class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.HoursHolder> {
    private List<Day> days;
    private LayoutInflater inflater;

    public HoursAdapter(List<Day> days, Context context) {
        this.days = days;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HoursHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        final View view = inflater.inflate(R.layout.info_hour, parent, false);
        return new HoursHolder(view);
    }

    public void setData(List<Day> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final HoursAdapter.HoursHolder holder, int i) {
        final Day day = days.get(i);

        holder.textViewDate.setText(day.getDate());
        holder.textViewTime.setText(day.getTime());
        holder.textViewTemp.setText(String.valueOf(Math.round(day.getMain().getTemp()))+" C");
        holder.textViewDesc.setText(String.valueOf(day.getWeather().get(0).getDescription()));
        holder.imageViewDayWeather.setImageResource(Utils.convertIconSourceToId(day.getWeather().get(0).getIcon()));
        holder.bg.setBackgroundResource(BgColorSetter.set(Math.round(day.getMain().getMaxTemp())));


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callback.onDayClick(day);
            }
        });


    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    public class HoursHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewTime, textViewTemp, textViewDesc;
        ImageView imageViewDayWeather;
        CardView bg;
        View view;

        public HoursHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.tvHourDate);
            textViewTime = itemView.findViewById(R.id.tvHourTime);
            textViewTemp = itemView.findViewById(R.id.tvHourTemp);
            textViewDesc = itemView.findViewById(R.id.tvHourDesc);
            imageViewDayWeather=itemView.findViewById(R.id.ivHourWeather);
            bg = itemView.findViewById(R.id.hourBg);
            this.view = itemView;
        }


    }



}
