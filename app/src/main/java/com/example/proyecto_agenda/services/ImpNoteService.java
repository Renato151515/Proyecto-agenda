package com.example.proyecto_agenda.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.proyecto_agenda.Objetos.Nota;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImpNoteService {

    /**
     * References:
     * https://stackoverflow.com/questions/54244973/getting-data-from-firebase-realtime-database-android-studio-java

     */


    private final static DatabaseReference ROOT_REF = FirebaseDatabase.getInstance()
            .getReference();

    private final static DatabaseReference USERS_REF =
            ROOT_REF.child("Usuarios");

    public static void getImportantNotes(ImpNoteService.Listener.GetNotes listener, String userUuid) {

        final DatabaseReference IMP_NOTES_REF = USERS_REF.child(userUuid)
                .child("Mis notas importantes");

        IMP_NOTES_REF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Nota> notes = new ArrayList<>();

                // Get data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Nota note = snapshot.getValue(Nota.class);
                    notes.add(note);
                    Log.d("NOTE_SERVICE", "onDataChange: Note value -> " + note.toString());
                }
                listener.success(notes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d("NOTE_SERVICE", "onCancelled: " + error);
                listener.error(error.getMessage());
            }
        });
    }

    public interface Listener {
        interface GetNotes {
            void success(List<Nota> notes);
            void error(String msg);
        }
    }
}
