package com.studios.hsoni.noteencrypt.Other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.studios.hsoni.noteencrypt.R;

import java.util.ArrayList;

public class NoteEntryAdapter extends RecyclerView.Adapter<NoteEntryAdapter.ViewHolder> {
    ArrayList<NoteEntry> data;
    Context mContext;
    OnClick onClick;

    public interface OnClick{
        void onClick(int position);
        void delete(int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView password,content;
        Button delete;
        View c;
        ViewHolder(View v) {
            super(v);
            c = v.getRootView();
            content= v.findViewById(R.id.noteEntryFileName);
            password = v.findViewById(R.id.noteEntryDate);
            delete = v.findViewById(R.id.noteEntryDelete);
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onClick(getAdapterPosition());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.delete(getAdapterPosition());
                }
            });
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoteEntryAdapter(ArrayList<NoteEntry> myDataset, Context context, OnClick listener) {
        data = myDataset;
        onClick = listener;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public NoteEntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View  v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_entry, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        NoteEntry note = data.get(position);

        holder.password.setText(note.getEntryPassword());
        holder.content.setText(note.getEntryData());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(data == null){
            return 0;
        }
        return data.size();
    }
}
