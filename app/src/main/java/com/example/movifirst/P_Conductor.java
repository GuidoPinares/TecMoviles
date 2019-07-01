package com.example.movifirst;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CALL_PHONE;

public class P_Conductor extends AppCompatActivity {

    private TextView tiempo01, tiempo02, tiempo03, tiempo04;
    private TextView direccion01, direccion02, direccion03, direccion04;
    private TextView NombreConductor, MovilidadAsignada;
    Button Contactar, IniciarRecorrido;
    private String usernameConductor, nameMovilidad;
    private int idmovilidad, idconductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p__conductor);

        tiempo01 = (TextView)findViewById(R.id.txt_Tiempo1);
        tiempo02 = (TextView)findViewById(R.id.txt_Tiempo2);
        tiempo03 = (TextView)findViewById(R.id.txt_Tiempo3);
        tiempo04 = (TextView)findViewById(R.id.txt_Tiempo4);

        direccion01 = (TextView)findViewById(R.id.txt_Destino1);
        direccion02 = (TextView)findViewById(R.id.txt_Destino2);
        direccion03 = (TextView)findViewById(R.id.txt_Destino3);
        direccion04 = (TextView)findViewById(R.id.txt_Destino4);

        //Set Username on TextView
        usernameConductor = getIntent().getStringExtra("username");
        NombreConductor = (TextView)findViewById(R.id.txt_NombreCon);
        NombreConductor.setText(usernameConductor);

        //Traer idconductor de BD

        idconductor = getidConductor();

        //Set Movilidad asignada en TextView
        String codregistro = getMovilidadConductor(idconductor);
        MovilidadAsignada = (TextView)findViewById(R.id.txt_Movilidad);
        MovilidadAsignada.setText(codregistro);


        Contactar = (Button)findViewById(R.id.btn_ContactarConductor);
        Contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:956369882"));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(i);
                } else {
                    //requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        });
    }

    public int getidConductor()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT idconductor FROM conductor where usuario ='"+usernameConductor+"'",null);
        if(fila.moveToFirst()){
            return fila.getInt(0);
        } else {
            return 0;
        }
    }

    public String getMovilidadConductor(int idconductor)
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor conductor = BaseDeDatos.rawQuery("SELECT idmovilidad FROM conductor where idconductor ='"+ Integer.toString(idconductor) +"'",null);
        conductor.moveToFirst();
        Cursor movilidad = BaseDeDatos.rawQuery("SELECT * FROM movilidad where idmovilidad ='"+ conductor.getString(0) +"'",null);
        if(movilidad.moveToFirst()){
            idmovilidad = movilidad.getInt(0);
            return movilidad.getString(1);
        } else {
            return "No hay";
        }
    }

    public Cursor getMoviConductor (int idconductor)
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor conductor = BaseDeDatos.rawQuery("SELECT idmovilidad FROM conductor where idconductor ='"+ Integer.toString(idconductor) +"'",null);
        conductor.moveToFirst();
        Cursor movilidad = BaseDeDatos.rawQuery("SELECT * FROM movilidad where idmovilidad ='"+ conductor.getString(0) +"'",null);
        return movilidad;
    }

    public Cursor getClientesbyMovilidad (){
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor clientes = BaseDeDatos.rawQuery("SELECT * FROM cliente where idmovilidad ='1'",null);
        return clientes;
    }

    public void insertarRutas()
    {
        int ruta = LastRutaId();

        Cursor movilidades = getMoviConductor(idconductor);
        movilidades.moveToFirst();

        Cursor clientes = getClientesbyMovilidad();
        clientes.moveToFirst();

        int cantidadclientes = clientes.getCount();

        onClickGrabar(movilidades.getString(2));

        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        for(int i=0; i<cantidadclientes; i++)
        {
            if(ruta != 0)
                ruta++;
            else
                ruta = 1;
            ContentValues registro = new ContentValues();
            registro.put("idruta", ruta);
            registro.put("idmovilidad", movilidades.getInt(0));
            registro.put("idcliente", clientes.getInt(0));
            registro.put("direccioninicio", "casa01");
            registro.put("direccionfin", "casa02");
            registro.put("tpromedio", 15);

            BaseDeDatos.insert("rutas", null, registro);
            clientes.moveToNext();
        }

        switch (cantidadclientes){
            case 1:
                CronoByOne(movilidades.getString(2));
                break;
            case 2:
                CronoByTwo(movilidades.getString(2));
                break;
            case 3:
                CronoByThree(movilidades.getString(2));
                break;
            case 4:
                ChromoByFour(movilidades.getString(2));
                break;
            default:
                break;
        }
    }

    public int LastRutaId ()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT MAX(idruta) FROM rutas",null);
        if(fila.moveToFirst()){
            return fila.getInt(0);
        } else {
            return 0;
        }
    }

    public void Iniciar(View view) {

        insertarRutas();

    }

    public void CronoByOne(final String movilidad)
    {
        direccion01.setText("Casa 01");

        new CountDownTimer(10000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo01.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {
                onClickGrabar(movilidad);
                tiempo01.setText("Completado");
            }
        }.start();
    }

    public void CronoByTwo(final String movilidad)
    {
        direccion01.setText("Casa 01");
        direccion02.setText("Casa 02");

        new CountDownTimer(10000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo01.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {
                onClickGrabar(movilidad);
                tiempo01.setText("Completado");
            }
        }.start();

        new CountDownTimer(90000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo02.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo02.setText("Completado");
            }
        }.start();
    }

    public void CronoByThree(final String movilidad)
    {
        direccion01.setText("Casa 01");
        direccion02.setText("Casa 02");
        direccion03.setText("Casa 03");

        new CountDownTimer(10000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo01.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo01.setText("Completado");
            }
        }.start();

        new CountDownTimer(90000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo02.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo02.setText("Completado");
            }
        }.start();

        new CountDownTimer(300000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo03.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo03.setText("Completado");
            }
        }.start();
    }

    public void ChromoByFour (final String movilidad)
    {
        direccion01.setText("Casa 01");
        direccion02.setText("Casa 02");
        direccion03.setText("Casa 03");
        direccion04.setText("Casa 04");

        new CountDownTimer(10000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo01.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo01.setText("Completado");
            }
        }.start();

        new CountDownTimer(80000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo02.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo02.setText("Completado");
            }
        }.start();

        new CountDownTimer(300000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo03.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo03.setText("Completado");
            }
        }.start();

        new CountDownTimer(600000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tiempo04.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {

                onClickGrabar(movilidad);
                tiempo04.setText("Completado");
            }
        }.start();
    }

    public void onClickGrabar (String movilidad)
    {
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        String filename = "//DCIM//archivoubicaciones.txt";
        File sd = Environment.getExternalStorageDirectory();
        File fileOutput = new File(sd, filename);

        String str = "La movilidad con codigo: "+ movilidad + " registro su ultima ubicacion en 'UBICACION01' a las: "+mydate;

        try {
            FileOutputStream fOut = new FileOutputStream(fileOutput);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(str);
            osw.flush();
            osw.close();
        }
        catch(IOException ios)
        {
            ios.printStackTrace();
        }
    }

}
