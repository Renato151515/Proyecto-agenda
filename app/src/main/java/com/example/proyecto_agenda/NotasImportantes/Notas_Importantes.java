package com.example.proyecto_agenda.NotasImportantes;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;
import com.example.proyecto_agenda.ViewHolder.ImportantNoteAdapter;
import com.example.proyecto_agenda.services.ImpNoteService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;

public class Notas_Importantes extends AppCompatActivity implements ImpNoteService.Listener.GetNotes{

    private RecyclerView rvImportantNotes;
    private ImportantNoteAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private static final String TAG = "ImportantNotes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notas_archivadas);

        // FirebaseAuth setup
        this.firebaseAuth = FirebaseAuth.getInstance();

        // Adapter setup
        this.adapter = new ImportantNoteAdapter(this, new ArrayList<>());

        // Recycler view
        rvImportantNotes = findViewById(R.id.recyclerviewNotasImportantes);
        rvImportantNotes.setLayoutManager(new LinearLayoutManager(this));
        rvImportantNotes.setHasFixedSize(true);
        rvImportantNotes.setAdapter(this.adapter);

        // Fetching data
        user = firebaseAuth.getCurrentUser();
        this.getImportantNotes();
    }

    private boolean isUserAuthenticated() {
        return user != null;
    }

    private void getImportantNotes () {
        if (this.isUserAuthenticated()) {
            // Get important notes
            ImpNoteService.getImportantNotes(this, user.getUid());
        } else {
            this.showShortToast("Usuario no autenticado");
        }
    }

    private void showShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(List<Nota> notes) {
        Log.d(TAG, "Favorite notes: " + notes.size());
        this.adapter.setItems(notes);
    }

    @Override
    public void error(String msg) {
        this.showShortToast("Error: " + msg);
    }
}
