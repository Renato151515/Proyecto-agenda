package com.example.proyecto_agenda.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;

import java.util.List;

public class ImportantNoteAdapter extends RecyclerView.Adapter<ImportantNoteAdapter.ViewHolder> {

    /**
     * References:
     * https://developer.android.com/develop/ui/views/layout/recyclerview#java
     */

    private final Context context;
    private List<Nota> items;

    public ImportantNoteAdapter(Context context, List<Nota> items) {
        this.context = context;
        this.items = items;
    }

    public void setItems(List<Nota> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(this.context)
                .inflate(R.layout.item_nota_importante, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nota note = this.items.get(position);
        String title = note.getTitulo();
        String desc = note.getDescripcion();
        String date = note.getFecha_nota();

        holder.getTvTitle().setText(title);
        holder.getTvDesc().setText(desc);
        holder.getTvDate().setText(date);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private final TextView tvTitle;
        private final TextView tvDesc;
        private final TextView tvDate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Getting widgets
            this.tvTitle = itemView.findViewById(R.id.Titulo_Item_I);
            this.tvDesc = itemView.findViewById(R.id.Descripcion_Item_I);
            this.tvDate = itemView.findViewById(R.id.Fecha_Item_I);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvDesc() {
            return tvDesc;
        }

        public TextView getTvDate() {
            return tvDate;
        }
    }
}
