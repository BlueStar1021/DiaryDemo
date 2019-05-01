package com.example.diarydemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dell on 2019/3/31.
 */

public class DiaryAdapter extends ArrayAdapter<Diary> {

    private int resourceId;

    public DiaryAdapter(Context context, int resource, List<Diary> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Diary diary = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        TextView diaryTitle = (TextView)view.findViewById(R.id.diary_title);
        TextView diaryDate = (TextView)view.findViewById(R.id.diary_date);

        diaryTitle.setText(diary.getTitle());
        diaryDate.setText(diary.getDate().toString());

        return view;
    }
}
