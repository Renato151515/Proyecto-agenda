package com.example.proyecto_agenda.Perfil;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;
import com.example.proyecto_agenda.ViewHolder.ImportantNoteAdapter;
import com.example.proyecto_agenda.services.NoteService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Perfil_Usuario extends AppCompatActivity {


    private ImportantNoteAdapter adapter;
    private FirebaseAuth fbAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);

        // Firebase Auth
        this.fbAuth = FirebaseAuth.getInstance();
        this.user = this.fbAuth.getCurrentUser();

        // Adapter setup
        this.adapter = new ImportantNoteAdapter(this, new ArrayList<>());

        // Recycler view setup
        RecyclerView rv = this.findViewById(R.id.rvSharedNotes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(this.adapter);

        // Fetch data from Firebase
        this.fetchSharedNotes();
    }

    private void fetchSharedNotes() {
        Log.d("sharedNote", "Getting notes for: " + this.user.getEmail());
        Log.d("sharedNote", "Getting notes for: " + this.user.getUid());

        NoteService.getSharedNotes(this.user.getUid(), new NoteService.GetSharedNotesCallback() {
            @Override
            public void onSuccess(List<Nota> notes) {
                if (!notes.isEmpty()) {
                    Log.d("sharedNote", "Notes size: " + notes.size());
                    adapter.setItems(notes);
                } else {
                    createInformationAlert("INFORMACIÓN",
                            R.drawable.icon_success,
                            "No tienes notas compartidas");
                }
            }

            @Override
            public void onFailure(String error) {
                createInformationAlert(
                        "ERROR",
                        R.drawable.icon_error,
                        "Error: " + error);
            }
        });
    }

    private AlertDialog createInformationAlert(String title, int iconId, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title.toUpperCase());
        builder.setIcon(iconId);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", ((dialog, which) -> {}));
        return builder.create();

    }
}