package com.example.movifirst;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class R_Conductor extends AppCompatActivity {


    private Spinner spn_Movilidades;
    private TextView tv_usuario, tv_contraseña, tv_correo, tv_telefono, tv_dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r__conductor);

        //Extraer datos de Textview

        tv_usuario = (TextView)findViewById(R.id.txt_NombreCon);
        tv_contraseña = (TextView)findViewById(R.id.txt_ContraseñaConductor);
        tv_correo = (TextView)findViewById(R.id.txt_CorreoCon);
        tv_telefono = (TextView)findViewById(R.id.txt_CelularCon);
        tv_dni = (TextView)findViewById(R.id.etxt_DNICon);

        //Fin extraccion de datos TextView

        //Añadir datos al Spinner

        spn_Movilidades = (Spinner)findViewById(R.id.spn_movilidades);

        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor lista = BaseDeDatos.rawQuery("select codregistro from movilidad",null);
        ArrayList<String> listaMovilidades = new ArrayList<String>();

        if(lista.moveToFirst())
        {
            do{
                listaMovilidades.add(lista.getString(0));
            }while(lista.moveToNext());
        }

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaMovilidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Movilidades.setAdapter(adapter);

        //Fin de añadido de datos
    }

    public void Guardar (View view) {
        AddConductor();
    }

    public void Limpiar (View view) {
        final TextView label = (TextView)findViewById(R.id.txt_NombreCon);
        final TextView label2 = (TextView)findViewById(R.id.etxt_DNICon);
        final TextView label4 = (TextView)findViewById(R.id.txt_CorreoCon);
        final TextView label5 = (TextView)findViewById(R.id.txt_CelularCon);
        final TextView label6 = (TextView)findViewById(R.id.txt_ContraseñaConductor);

        label.setText("");
        label2.setText("");
        label4.setText("");
        label5.setText("");
        label6.setText("");

    }

    public void AddConductor ()
    {
        AdminSQLite admin = new AdminSQLite(this,"dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        // Traer el ultimo idconductor
        int conductor = LastUserId();
        if(conductor != 0)
            conductor++;
        else
            conductor = 1;

        String usuario = tv_usuario.getText().toString();
        String contraseña = tv_contraseña.getText().toString();
        String correo = tv_correo.getText().toString();
        String telefono = tv_telefono.getText().toString();
        String dni = tv_dni.getText().toString();
        int dvencimientobrevette = 25;
        int movilidad = 1;

        if(!usuario.isEmpty() && !contraseña.isEmpty() && !correo.isEmpty() && !telefono.isEmpty() && !dni.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("idconductor", conductor);
            registro.put("usuario", usuario);
            registro.put("contraseña", contraseña);
            registro.put("correo", correo);
            registro.put("telefono", telefono);
            registro.put("dni", dni);
            registro.put("dvencimientobrevette",dvencimientobrevette);
            registro.put("idmovilidad", movilidad);

            BaseDeDatos.insert("conductor",null,registro);
            BaseDeDatos.close();
            tv_usuario.setText("");
            tv_contraseña.setText("");
            tv_correo.setText("");
            tv_telefono.setText("");
            tv_dni.setText("");

            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,"Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    public int LastUserId ()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT MAX(idconductor) FROM conductor",null);
        if(fila.moveToFirst()){
            return fila.getInt(0);
        } else {
            return 0;
        }

    }
}
