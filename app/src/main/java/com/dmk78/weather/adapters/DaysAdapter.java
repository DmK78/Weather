package com.dmk78.weather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk78.weather.utils.BgColorSetter;
import com.dmk78.weather.R;
import com.dmk78.weather.utils.Utils;
import com.dmk78.weather.model.Day;

import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayHolder> {

    private List<Day> days;
    private LayoutInflater inflater;
    private Context context;

    public DaysAdapter(List<Day> days, Context context) {
        this.days = days;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
        final View view = inflater.inflate(R.layout.info_day, parent, false);
        return new DayHolder(view);
    }

    public void setData(List<Day> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final DayHolder holder, int i) {
        final Day day = days.get(i);

        holder.textViewDate.setText(day.getDate());
        holder.textViewTempMax.setText(String.valueOf(Math.round(day.getMain().getMaxTemp()))+" C");
        holder.textViewTempMin.setText(String.valueOf(Math.round(day.getMain().getMinTemp()))+" C");
        //holder.imageViewDayWeather.setImageResource(Utils.convertIconSourceToId(day.getWeather().get(0).getIcon()));
        holder.imageViewDayWeather.setImageResource(Utils.getStringIdentifier(context, "i" + day.getWeather().get(0).getIcon(), "drawable"));



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

    public class DayHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewTempMax, textViewTempMin;
        ImageView imageViewDayWeather;
        ConstraintLayout bg;
        View view;

        public DayHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDayDate);
            textViewTempMax = itemView.findViewById(R.id.textViewDayTempMax);
            textViewTempMin = itemView.findViewById(R.id.textViewDayTempMin);
            imageViewDayWeather = itemView.findViewById(R.id.imageViewDay);
            bg = itemView.findViewById(R.id.dayBg);
            this.view = itemView;
        }


    }



}