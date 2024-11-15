package com.example.proyecto_agenda.services;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.proyecto_agenda.Objetos.Nota;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteService {

    public static void  shareNote(String userUid, Nota note, NoteService.ShareNoteCallback callback) {
        final DatabaseReference SHARED_NOTES_REF = FirebaseReference.USERS_REF
                .child(userUid)
                .child("shared_notes").getRef();

        String key = SHARED_NOTES_REF.push().getKey();

        if (key == null) {
            callback.failure("Error al generar único registro");
            return;
        }

        SHARED_NOTES_REF.child(key).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.success();
                } else {
                    callback.failure("Fallo al registrar la nota compartida");
                }
            }
        });

    }

    public static void getSharedNotes(String userUid, GetSharedNotesCallback callback) {
        final DatabaseReference SHARED_NOTES_REF = FirebaseReference
                .USERS_REF.child(userUid)
            .child("shared_notes")
                .getRef();
        Log.d("sharedNote", "Request received: " + userUid);

        SHARED_NOTES_REF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Nota> notes = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Nota note = snapshot.getValue(Nota.class);
                    notes.add(note);
                }
                Log.d("sharedNote", "Notes: " + notes);
                callback.onSuccess(notes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public interface ShareNoteCallback {
        void success();
        void failure(String error);
    }

    public interface GetSharedNotesCallback {
        void onSuccess(List<Nota> notes);
        void onFailure(String error);
    }
}
