package com.example.proyecto_agenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_agenda.AgregarNota.Agregar_Nota;
import com.example.proyecto_agenda.ListarNotas.Listar_Notas;
import com.example.proyecto_agenda.NotasImportantes.Notas_Importantes;
import com.example.proyecto_agenda.Perfil.Perfil_Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    Button AgregarNotas, ListarNotas, Importantes, Perfil, AcercaDe, CerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView UidPrincipal,nombresPrincipal,correoPrincipal;
    ProgressBar progressBarDatos;

    DatabaseReference Usuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Proyecto Agenda");

        UidPrincipal= findViewById(R.id.UidPrincipal);
        nombresPrincipal= findViewById(R.id.nombresPrincipal);
        correoPrincipal = findViewById(R.id.correoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        Usuarios= FirebaseDatabase.getInstance().getReference("Usuarios");

        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        Importantes = findViewById(R.id.Importantes);
        Perfil = findViewById(R.id.Perfil);
        AcercaDe = findViewById(R.id.AcercaDe);
        CerrarSesion = findViewById(R.id.CerrarSesion);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        AgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos la info de los textview
                String uid_usuario = UidPrincipal.getText().toString();
                String  correo_usuario = correoPrincipal.getText().toString();
                //Pasmops los datos a la siguiente actividad
            Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
            intent.putExtra("Uid", uid_usuario);
            intent.putExtra("Correo", correo_usuario);
            startActivity(intent);
            }
        });
        ListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
                Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });
        Importantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
                Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });
        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
                Toast.makeText(MenuPrincipal.this, "Notas compartidas", Toast.LENGTH_SHORT).show();
            }
        });
        AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show();
            }
        });
        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });
    }

    @Override
    protected void onStart() {
        comprobarInicioSesion();
        super.onStart();
    }

    private void comprobarInicioSesion(){
        if (user!=null){
            //El usuario ha iniciado sesión
            cargaDeDatos();
        }else {
            //Lo dirigirá al Main Activty(Registro)
            startActivity(new Intent(MenuPrincipal.this,MainActivity.class));
            finish();
        }
    }
    private void cargaDeDatos(){
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Si es que detecta un usuario
                if (snapshot.exists()){
                    //El progress bar se oculta(el cargando)
                    progressBarDatos.setVisibility(View.GONE);
                    //Se muestran los textos
                    // UidPrincipal.setVisibility(View.VISIBLE);
                    nombresPrincipal.setVisibility(View.VISIBLE);
                    // correoPrincipal.setVisibility(View.VISIBLE);

                    //Obtener los datos
                    String uid=""+snapshot.child("uid").getValue();
                    String nombres=""+snapshot.child("nombres").getValue();
                    String correo=""+snapshot.child("correo").getValue();

                    //Traer los datos y colocarlos en los text view
                    UidPrincipal.setText(uid);
                    nombresPrincipal.setText(nombres);
                    correoPrincipal.setText(correo);

                    //Habilitar botones del menú principal

                    AgregarNotas.setEnabled(true);
                    ListarNotas.setEnabled(true);
                    Importantes.setEnabled(true);
                    Perfil.setEnabled(true);
                    AcercaDe.setEnabled(true);
                    CerrarSesion.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SalirAplicacion() {
    firebaseAuth.signOut();
    startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
    }
}