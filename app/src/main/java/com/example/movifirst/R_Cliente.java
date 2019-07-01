package com.example.movifirst;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class R_Cliente extends AppCompatActivity {

    private Spinner spn_MovilidadAsignada;
    private TextView tv_usuario, tv_contraseña, tv_correo, tv_telefono, tv_direccion;
    private Button btn_hijo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r__cliente);

        //Extraer datos de Textview

        tv_usuario = (TextView)findViewById(R.id.txt_NombreCli);
        tv_contraseña = (TextView)findViewById(R.id.txt_ContraseñaCliente);
        tv_correo = (TextView)findViewById(R.id.txt_CorreoCli);
        tv_telefono = (TextView)findViewById(R.id.txt_CelularCli);
        tv_direccion = (TextView)findViewById(R.id.txt_DireccionCli);


        //Datos para Spinner

        spn_MovilidadAsignada = (Spinner)findViewById(R.id.spn_Movilidad);

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
        spn_MovilidadAsignada.setAdapter(adapter);

    }

    public void Guardar (View view)
    {
        AddCliente();
        btn_hijo = (Button)findViewById(R.id.button13);
        btn_hijo.setEnabled(true);
    }

    public void ToRegHijo (View view) {

        int clienteid = LastUserId();
        String idcliente = Integer.toString(clienteid);


        Intent RegHijo = new Intent(this, R_Hijo.class);
        RegHijo.putExtra("idcliente",idcliente);
        startActivity(RegHijo);
    }

    public void Limpiar (View view) {
        final TextView label = (TextView)findViewById(R.id.txt_NombreCli);
        final TextView label2 = (TextView)findViewById(R.id.txt_DNICliente);
        final TextView label3 = (TextView)findViewById(R.id.txt_DireccionCli);
        final TextView label4 = (TextView)findViewById(R.id.txt_CorreoCli);
        final TextView label5 = (TextView)findViewById(R.id.txt_CelularCli);
        final TextView label6 = (TextView)findViewById(R.id.txt_ContraseñaCliente);
        label.setText("");
        label2.setText("");
        //label3.setText("");
        label4.setText("");
        //label5.setText("");
        label6.setText("");
    }

    public void AddCliente ()
    {
        AdminSQLite admin = new AdminSQLite(this,"dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        // Traer el ultimo idcliente
        int cliente = LastUserId();
        if(cliente != 0)
            cliente++;
        else
            cliente = 1;

        String usuario = tv_usuario.getText().toString();
        String contraseña = tv_contraseña.getText().toString();
        String correo = tv_correo.getText().toString();
        String telefono = tv_telefono.getText().toString();
        String direccion = tv_direccion.getText().toString();
        int movilidad = 1;

        if(!usuario.isEmpty() && !contraseña.isEmpty() && !correo.isEmpty() && !telefono.isEmpty() && !direccion.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("idcliente", cliente);
            registro.put("usuario", usuario);
            registro.put("contraseña", contraseña);
            registro.put("correo", correo);
            registro.put("telefono", telefono);
            registro.put("direccion", direccion);
            registro.put("idmovilidad", movilidad);

            BaseDeDatos.insert("cliente",null,registro);
            BaseDeDatos.close();


            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,"Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    public int LastUserId ()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT MAX(idcliente) FROM cliente",null);
        if(fila.moveToFirst()){
            return fila.getInt(0);
        } else {
            return 0;
        }

    }

}
