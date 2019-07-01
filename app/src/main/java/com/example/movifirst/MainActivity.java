package com.example.movifirst;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView txtUsuario;
    private TextView txtContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtUsuario = (TextView)findViewById(R.id.txt_LoginUsuario);
        txtContraseña = (TextView)findViewById(R.id.txt_LoginContraseña);

        showLastMovilidad();

        /*
        //Añadir Movilidades

        showLastMovilidad();

        if(!getLastMovilidad()){
            AddMovilidad();
        }
        */

    }

    public void ToRegConductor (View view) {
        Intent RegConductor = new Intent(this, R_Conductor.class);
        startActivity(RegConductor);
    }

    public void ToRegCliente (View view) {
        Intent RegCliente = new Intent(this, R_Cliente.class);
        startActivity(RegCliente);
    }

    public void BKDB (View view)
    {
        exportDatabse("dbmovilidad");
    }


    public void ToIniSesion (View view) {

        String valortxt_Usuario = txtUsuario.getText().toString();
        String valortxt_Contraseña = txtContraseña.getText().toString();

        int result = matchUserPassword(valortxt_Usuario,valortxt_Contraseña);

        if(result == 1 || result == 2)
        {
            if(result == 1) {

                Intent IniCliente = new Intent(this, P_Cliente.class);
                IniCliente.putExtra("username",valortxt_Usuario);
                startActivity(IniCliente);
            }else
            {
                Intent IniConductor = new Intent(this, P_Conductor.class);
                IniConductor.putExtra("username",valortxt_Usuario);
                startActivity(IniConductor);
            }
        }
        else
        {
            Toast.makeText(this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
            txtUsuario.setText("");
            txtContraseña.setText("");
        }

    }

    public void AddMovilidad ()
    {
        AdminSQLite admin = new AdminSQLite(this,"dbmovilidad", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        // Datos movilidad 01

        int idmovilidad  = 1;
        String codregistro = "movilidad01";
        String placa = "XAJ-646";
        int dvencimientosoat = 10;
        int dvencimientocontrol = 38;
        String direccion = "Urbanizacion Cerro Colorado V-1";

        ContentValues registro = new ContentValues();
        registro.put("idmovilidad",idmovilidad);
        registro.put("codregistro",codregistro);
        registro.put("placa",placa);
        registro.put("dvencimientosoat",dvencimientosoat);
        registro.put("dvencimientocontrol",dvencimientocontrol);
        registro.put("direccion",direccion);

        BaseDeDatos.insert("movilidad",null,registro);

        // Datos movilidad 02

        idmovilidad  = 2;
        codregistro = "movilidad02";
        placa = "XAJ-555";
        dvencimientosoat = 87;
        dvencimientocontrol = 42;
        direccion = "Urbanizacion La Estancia E-2";

        ContentValues registro2 = new ContentValues();
        registro2.put("idmovilidad",idmovilidad);
        registro2.put("codregistro",codregistro);
        registro2.put("placa",placa);
        registro2.put("dvencimientosoat",dvencimientosoat);
        registro2.put("dvencimientocontrol",dvencimientocontrol);
        registro2.put("direccion",direccion);

        BaseDeDatos.insert("movilidad",null,registro2);

        // Datos movilidad 03

        idmovilidad  = 3;
        codregistro = "movilidad03";
        placa = "XAJ-983";
        dvencimientosoat = 150;
        dvencimientocontrol = 130;
        direccion = "Urbanizacion Coloquial H-5";

        ContentValues registro3 = new ContentValues();
        registro3.put("idmovilidad",idmovilidad);
        registro3.put("codregistro",codregistro);
        registro3.put("placa",placa);
        registro3.put("dvencimientosoat",dvencimientosoat);
        registro3.put("dvencimientocontrol",dvencimientocontrol);
        registro3.put("direccion",direccion);

        BaseDeDatos.insert("movilidad",null,registro3);

        BaseDeDatos.close();
        showLastMovilidad();
    }


    public void showLastMovilidad(){

        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT codregistro, placa FROM movilidad WHERE idmovilidad = 2",null);
        if(fila.moveToFirst()){
            //Toast.makeText(this, "Reg:"+fila.getString(0)+", P:"+fila.getString(1), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"Movilidades creadas",Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(this, "No hay movilidades", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean getLastMovilidad()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT MAX(idmovilidad) FROM movilidad",null);
        if(fila.moveToFirst()){
            return false;
        } else {
            return true;
        }
    }

    public int matchUserPassword(String username, String password)
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor cliente = BaseDeDatos.rawQuery("select idcliente from cliente where usuario ='"+ username+"' and contraseña ='"+password+"'",null);
        Cursor conductor = BaseDeDatos.rawQuery("select idconductor from conductor where usuario ='"+ username+"' and contraseña ='"+password+"'",null);

        if(cliente.moveToFirst()){
            return 1;
        } else {
            if(conductor.moveToFirst())
                return 2;
            else
                return 3;
        }

    }

    public void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "//DCIM//bkmovi.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
        //DeleteDatabase();
    }

    public void DeleteDatabase()
    {
        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/com.example.movifirst/databases/dbmovilidad";
        File currentDB = new File(data, currentDBPath);
        SQLiteDatabase.deleteDatabase(currentDB);
    }
}
