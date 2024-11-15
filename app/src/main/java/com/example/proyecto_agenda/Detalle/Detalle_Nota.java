package com.example.proyecto_agenda.Detalle;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_agenda.Objetos.Nota;
import com.example.proyecto_agenda.R;
import com.example.proyecto_agenda.entities.UserEntity;
import com.example.proyecto_agenda.services.NoteService;
import com.example.proyecto_agenda.services.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Detalle_Nota extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Button Boton_Importante, btnShare;
    TextView Id_nota_Detalle, Uid_usuario_Detalle, Correo_usuario_Detalle, Titulo_Detalle, Descripcion_Detalle,
        Fecha_Registro_Detalle, Fecha_Nota_Detalle, Estado_Detalle;

    String id_nota_R, uid_usuario_R, correo_usuario_R, fecha_registro_R, titulo_R, descripcion_R, fecha_R, estado_R;

    boolean ComprobarNotaImportante = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_nota);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle de nota");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Setup share note Button
        this.btnShare = this.findViewById(R.id.bnt_share_note);
        this.btnShare.setOnClickListener(view -> this.createDialog().show());

        InicializarVistas();
        RecuperarDatos();
        SetearDatosRecuperados();
        VerificarNotaImportante();

        Boton_Importante.setOnClickListener(v -> {
            if(ComprobarNotaImportante){
                Eliminar_Nota_Importante();
            }else {
                Agregar_Notas_Importantes();
            }
        });

    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_share_black);
        builder.setTitle("Compartir nota");

        // Add EditText to alert
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.share_note_alert, null);
        builder.setView(view);

        // Get email to share
        EditText etMail = view.findViewById(R.id.etEmailShare);

        // Setup buttons actions
        builder.setPositiveButton("Compartir", ((dialog, which) -> {
            // Share note with user
            final String emailDest = etMail.getText().toString();
            this.shareNote(emailDest);
        }));

        builder.setNegativeButton("Cancelar", ((dialog, which) -> {}));


        return builder.create();
    }

    private void shareNote(String emailDest) {

        // User dest it need to be different from actual user
        Log.d("sharedNote", "shareNote: " + correo_usuario_R);
        if (emailDest.equals(correo_usuario_R)) {
            this.createErrorAlert(
                    "No te puedes enviar notas a ti mismo"
            ).show();
            return;
        }

        // Get user's uid by email
        UserService.getUserByEmail(emailDest, new UserService.CallbackGetUser() {
            @Override
            public void exist(UserEntity user) {
                // Get note information
                Nota note = new Nota(
                        id_nota_R,
                        uid_usuario_R,
                        correo_usuario_R,
                        fecha_R,
                        titulo_R,
                        descripcion_R,
                        fecha_registro_R,
                        estado_R
                );

                // Send to note to dest
                NoteService.shareNote(user.getUid(), note, new NoteService.ShareNoteCallback() {
                    @Override
                    public void success() {
                        // Create an alert to show success information
                        createSuccessAlert("Nota compartida con: " + user.getNames()).show();
                    }

                    @Override
                    public void failure(String error) {
                        // Create an alert to show error information
                        createErrorAlert("Error: " + error).show();
                    }
                });
            }

            @Override
            public void notExist(String msg) {
                // Show an alert to show information
                createErrorAlert("Error: " + msg).show();
            }

            @Override
            public void error(String msg) {
                // Show an alert to show error information
                createErrorAlert("Error: " + msg).show();
            }
        });
    }

    private AlertDialog createErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_error);
        builder.setTitle("ERROR");
        builder.setMessage(msg);

        // Action buttons setup
        builder.setPositiveButton("Ok", ((dialog, which) -> {}));

        return builder.create();
    }

    private AlertDialog createSuccessAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_success);
        builder.setTitle("OPERACIÓN EXITOSA");
        builder.setMessage(msg);

        // Action buttons setup
        builder.setPositiveButton("Ok", ((dialog, which) -> {}));

        return builder.create();
    }


    private void InicializarVistas(){
        Id_nota_Detalle = findViewById(R.id.Id_nota_Detalle);
        Uid_usuario_Detalle = findViewById(R.id.Uid_usuario_Detalle);
        Correo_usuario_Detalle = findViewById(R.id.Correo_usuario_Detalle);
        Titulo_Detalle = findViewById(R.id.Titulo_Detalle);
        Descripcion_Detalle = findViewById(R.id.Descripcion_Detalle);
        Fecha_Registro_Detalle = findViewById(R.id.Fecha_Registro_Detalle);
        Fecha_Nota_Detalle = findViewById(R.id.Fecha_Nota_Detalle);
        Estado_Detalle = findViewById(R.id.Estado_Detalle);
        Boton_Importante = findViewById(R.id.Boton_Importante);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }
    private void RecuperarDatos(){
        Bundle intent = getIntent().getExtras();

        id_nota_R= intent.getString("id_nota");
        uid_usuario_R= intent.getString("uid_usuario");
        correo_usuario_R= intent.getString("correo_usuario");
        fecha_registro_R= intent.getString("fecha_registro");
        titulo_R= intent.getString("titulo");
        descripcion_R= intent.getString("descripcion");
        fecha_R= intent.getString("fecha_nota");
        estado_R= intent.getString("estado");
    }

    private void SetearDatosRecuperados(){
        Id_nota_Detalle.setText(id_nota_R);
        Uid_usuario_Detalle.setText(uid_usuario_R);
        Correo_usuario_Detalle.setText(correo_usuario_R);
        Descripcion_Detalle.setText(descripcion_R);
        Titulo_Detalle.setText(titulo_R);
        Fecha_Registro_Detalle.setText(fecha_registro_R);
        Fecha_Nota_Detalle.setText(fecha_R);
        Estado_Detalle.setText(estado_R);
    }

    private void Agregar_Notas_Importantes(){
        if (user == null){
            //si el usuario no existe saldra error
            Toast.makeText(Detalle_Nota.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }else {
            //Tenemos los datos detalle nota
            Bundle intent = getIntent().getExtras();

            id_nota_R= intent.getString("id_nota");
            uid_usuario_R= intent.getString("uid_usuario");
            correo_usuario_R= intent.getString("correo_usuario");
            fecha_registro_R= intent.getString("fecha_registro");
            titulo_R= intent.getString("titulo");
            descripcion_R= intent.getString("descripcion");
            fecha_R= intent.getString("fecha_nota");
            estado_R= intent.getString("estado");

            String identificador_nota_importante = uid_usuario_R+titulo_R;

            HashMap<String , String> Nota_Importante= new HashMap<>();
            Nota_Importante.put("id_nota", id_nota_R);
            Nota_Importante.put("uid_usuario", uid_usuario_R);
            Nota_Importante.put("correo_usuario", correo_usuario_R);
            Nota_Importante.put("fecha_hora_actual", fecha_registro_R);
            Nota_Importante.put("titulo", titulo_R);
            Nota_Importante.put("descripcion", descripcion_R);
            Nota_Importante.put("fecha_nota", fecha_R);
            Nota_Importante.put("estado", estado_R);
            Nota_Importante.put("id_nota_importante", identificador_nota_importante);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference.child(firebaseAuth.getUid()).child("Mis notas importantes").child(identificador_nota_importante)
                    .setValue(Nota_Importante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Detalle_Nota.this, "Se ha añadido a notas importantes", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Detalle_Nota.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void Eliminar_Nota_Importante(){
        if(user ==null){
            Toast.makeText(Detalle_Nota.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }else {
            Bundle intent = getIntent().getExtras();
            uid_usuario_R= intent.getString("uid_usuario");
            titulo_R= intent.getString("titulo");

            String identificador_nota_importante = uid_usuario_R+titulo_R;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference.child(firebaseAuth.getUid()).child("Mis notas importantes").child(identificador_nota_importante)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Detalle_Nota.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Detalle_Nota.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void VerificarNotaImportante(){
        if (user == null){
            Toast.makeText(Detalle_Nota.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }else {
            Bundle intent = getIntent().getExtras();
            id_nota_R= intent.getString("id_nota");
            titulo_R= intent.getString("titulo");

            String identificador_nota_importante = uid_usuario_R+titulo_R;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference.child(firebaseAuth.getUid()).child("Mis notas importantes").child(identificador_nota_importante)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ComprobarNotaImportante= snapshot.exists();
                            if(ComprobarNotaImportante){
                                String importante = "Importante";
                                Boton_Importante.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.icono_nota_importante,0,0);
                                Boton_Importante.setText(importante);
                            }else {
                                String no_importante= "No importante";
                                Boton_Importante.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.icono_nota_no_importante,0,0);
                                Boton_Importante.setText(no_importante);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}