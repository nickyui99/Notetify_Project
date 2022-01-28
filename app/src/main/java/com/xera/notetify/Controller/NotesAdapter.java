package com.xera.notetify.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xera.notetify.Model.FinancialModel;
import com.xera.notetify.Model.NoteModel;
import com.xera.notetify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.Viewholder> {

    private Context context;
    private ArrayList<NoteModel> noteModelArrayList;
    private static ClickListener clickListener;

    //Constructor
    public NotesAdapter(Context context, ArrayList<NoteModel> noteModelArrayList){
        this.context = context;
        this.noteModelArrayList = noteModelArrayList;
    }

    @NonNull
    @Override
    public NotesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.Viewholder holder, int position) {
        NoteModel noteModel = noteModelArrayList.get(position);
        holder.txtViewTitle.setText(noteModel.getNoteTitle());
        holder.txtViewDate.setText(noteModel.getDateCreated().toString());

        if(noteModel.getPrivacyLockStatus() == true){
            holder.imgViewLock.setVisibility(View.VISIBLE);
        }else {
            holder.imgViewLock.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return noteModelArrayList.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtViewTitle, txtViewDate;
        private ImageView imgViewLock;

        public Viewholder(@NonNull View itemView){
            super(itemView);
            txtViewTitle = itemView.findViewById(R.id.txtViewTitle);
            txtViewDate = itemView.findViewById(R.id.txtViewFinancialDate);
            imgViewLock = itemView.findViewById(R.id.imgViewLock);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        NotesAdapter.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}


