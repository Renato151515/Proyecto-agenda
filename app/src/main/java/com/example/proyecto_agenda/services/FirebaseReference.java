package com.example.proyecto_agenda.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface FirebaseReference {

    DatabaseReference ROOT_REF = FirebaseDatabase.getInstance().getReference();

    DatabaseReference USERS_REF = ROOT_REF.child("Usuarios");

    DatabaseReference NOTES_REF = ROOT_REF.child("Notas_Publicadas");

}
