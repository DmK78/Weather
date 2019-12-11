package com.dmk78.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk78.weather.Data.Day;

import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayHolder> {
    private static OnDayAdapterClickListener callback;
    private List<Day> days;
    private LayoutInflater inflater;

    public DaysAdapter(List<Day> days, Context context) {
        this.days = days;
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

        holder.textViewDate.setText(day.getDt_txt());
        holder.textViewTempMax.setText("" + Math.round(day.getMain().getTemp()));
        holder.textViewTempMin.setText("" + Math.round(day.getMain().getMinTemp()));
        holder.imageViewDayWeather.setImageResource(Utils.convertIconSourceToId(day.getWeather().get(0).getIcon()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDayClick(day);
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

        View view;

        public DayHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDayDate);
            textViewTempMax = itemView.findViewById(R.id.textViewDayTempMax);
            textViewTempMin = itemView.findViewById(R.id.textViewDayTempMin);
            imageViewDayWeather = itemView.findViewById(R.id.imageViewDay);
            this.view = itemView;
        }


    }

    public void setOnItemClickListener(OnDayAdapterClickListener clickListener) {
        DaysAdapter.callback = clickListener;
    }

    public interface OnDayAdapterClickListener {
        void onDayClick(Day day);

    }

}
