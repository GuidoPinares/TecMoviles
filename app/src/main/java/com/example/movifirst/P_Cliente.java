package com.example.movifirst;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CALL_PHONE;

public class P_Cliente extends AppCompatActivity {

    private TextView distancia, movilidadAsignada;
    private TextView NombreCliente;
    Button botonRecordatorio;
    Button botonContactar;
    Button pause;
    private String usernameCliente;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p__cliente);
        distancia = (TextView)findViewById(R.id.txt_Distancia);
        movilidadAsignada = (TextView)findViewById(R.id.txt_movilidadAsignada);
        pause = (Button)findViewById(R.id.btn_pause);
        mp = MediaPlayer.create(this, R.raw.alarma);

        //Set Username on TextView
        usernameCliente = getIntent().getStringExtra("username");
        NombreCliente = (TextView)findViewById(R.id.txt_NombreCli);
        NombreCliente.setText(usernameCliente);


        //Traer el idcliente de BD
        int idcliente = getidCliente();

        //Traer el nombre de la movilidad
        String codregistro = getMovilidadCliente(idcliente);
        movilidadAsignada.setText(codregistro);


        //Boton Recordatorio
        botonRecordatorio = findViewById(R.id.btn_Recordatorio);

        botonRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                establecerAlarma("La Movilidad Llegara", 1,1);
            }
        });
        //Fin Boton Recordatorio

        //Boton Contactar
        botonContactar = findViewById(R.id.btn_ContactarCliente);

        botonContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:959929428"));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(i);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        });
        //Fin Boton Contactar

    }


    public void establecerAlarma(String mensaje, int hora, int minutos){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, mensaje)
                .putExtra(AlarmClock.EXTRA_HOUR, hora)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutos);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    public void Iniciar(View view) {
        new CountDownTimer(20000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                distancia.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            @Override
            public void onFinish() {
                ReproductirSonido();
                pause.setVisibility(View.VISIBLE);
                MensajeToCliente();
            }
        }.start();
    }

    public int getidCliente()
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery("SELECT idcliente FROM cliente where usuario ='"+usernameCliente+"'",null);
        if(fila.moveToFirst()){
            return fila.getInt(0);
        } else {
            return 0;
        }
    }

    public String getMovilidadCliente(int idcliente)
    {
        AdminSQLite admin = new AdminSQLite(this, "dbmovilidad",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Cursor cliente = BaseDeDatos.rawQuery("SELECT idmovilidad FROM cliente where idcliente ='"+ Integer.toString(idcliente) +"'",null);
        cliente.moveToFirst();
        Cursor movilidad = BaseDeDatos.rawQuery("SELECT codregistro FROM movilidad where idmovilidad ='"+ cliente.getString(0) +"'",null);
        if(movilidad.moveToFirst()){
            return movilidad.getString(0);
        } else {
            return "No hay";
        }

    }

    public void MensajeToCliente()
    {
        Toast.makeText(this,"Si la movilidad aun no llego, contactarse con el conductor",Toast.LENGTH_LONG).show();
    }

    public void ReproductirSonido()
    {
        mp.start();
    }
    public void PausarSonido(View view)
    {
        mp.pause();
    }

}
