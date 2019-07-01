package com.example.movifirst;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class R_Hijo extends AppCompatActivity {

    private String idcliente;
    private int idhijo;
    private TextView tv_nombre, tv_apellidos, tv_dni, tv_edad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r__hijo);

        idhijo = LastUserId();
        idcliente = getIntent().getStringExtra("idcliente");
        tv_nombre = (TextView)findViewById(R.id.txt_NombreHijo);
        tv_apellidos = (TextView)findViewById(R.id.txt_ApellidosHijo);
        tv_dni = (TextView)findViewById(R.id.txt_DNICliente);
        tv_edad = (TextView)findViewById(R.id.txt_edad);
    }

    public void Agregar (View view) {
        AddHijo();
        Limpiar(view);
    }

    public void Limpiar (View view) {
        final TextView label = (TextView)findViewById(R.id.txt_NombreHijo);
        final TextView label1 = (TextView)findViewById(R.id.txt_ApellidosHijo);
        final TextView label2 = (TextView)findViewById(R.id.txt_DNICliente);
        final TextView label3 = (TextView)findViewById(R.id.txt_edad);

        label.setText("");
        label1.setText("");
        label2.setText("");
        label3.setText("");
    }

    public void AddHijo ()
    {
        AdminSQLite admin = new AdminSQLite(this,"dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        // Traer el ultimo idhijo
        int hijo = LastUserId();
        if(hijo != 0)
            hijo++;
        else
            hijo = 1;

        String nombre = tv_nombre.getText().toString();
        String apellidos = tv_apellidos.getText().toString();
        String dni = tv_dni.getText().toString();
        String edad =tv_edad.getText().toString();

        if(!nombre.isEmpty() && !apellidos.isEmpty() && !dni.isEmpty() && !edad.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("idhijo", hijo);
            registro.put("nombre", nombre);
            registro.put("apellidos", apellidos);
            registro.put("dni", dni);
            registro.put("edad", edad);
            registro.put("idcliente", idcliente);

            BaseDeDatos.insert("hijo",null,registro);
            BaseDeDatos.close();
            tv_nombre.setText("");
            tv_apellidos.setText("");
            tv_dni.setText("");
            tv_edad.setText("");

            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,"Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
        showLastHijo();

    }

    public int LastUserId ()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT MAX(idhijo) FROM hijo",null);
        if(fila.moveToFirst()){
            return fila.getInt(0);
        } else {
            return 0;
        }

    }

    public void showLastHijo(){

        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT MAX(idhijo), idcliente FROM hijo",null);
        if(fila.moveToFirst()){
            Toast.makeText(this, "Reg:"+fila.getString(0)+", P:"+fila.getString(1), Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(this, "No hay movilidades", Toast.LENGTH_SHORT).show();
        }
    }

}
