package com.example.proyecto_agenda.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.proyecto_agenda.entities.UserEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserService {

    public static void getUserByEmail(String email, UserService.CallbackGetUser callback) {
        Query query = FirebaseReference.USERS_REF.orderByChild("correo").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        String name = userSnapshot
                                .child("nombres")
                                .getValue(String.class);
                        String password = userSnapshot
                                .child("password")
                                .getValue(String.class);

                        String email = userSnapshot
                                .child("correo")
                                .getValue(String.class);

                        String uid = userSnapshot
                                .child("uid")
                                        .getValue(String.class);

                        Log.d("sharedNote", "Dest: " + name);
                        callback.exist(new UserEntity(uid, email, name, password));
                    }
                } else {
                    Log.d("shareNote", "Dest: Not exists");
                    callback.notExist(email + " no existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("shareNote", "Database error: " + error.getMessage());
                callback.error("Ha ocurrido un erro con la base de datos");
            }
        });
    }


    public interface CallbackCheckEmail {
        void exist(boolean exist);
        void error(String errorMsg);
    }

    public interface CallbackGetUser {
        void exist(UserEntity user);
        void notExist(String msg);
        void error(String msg);
    }

}
