package com.example.movifirst;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.nio.file.Path;

public class AdminSQLite extends SQLiteOpenHelper {

    public AdminSQLite(Context context,String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cliente(idcliente int primary key, usuario text, contraseña text, correo text, telefono text, direccion text, idmovilidad int)");
        db.execSQL("create table hijo(idhijo int primary key, nombre text, apellidos text, dni text, edad int, idcliente int)");
        db.execSQL("create table movilidad(idmovilidad int primary key, codregistro text, placa text, dvencimientosoat int, dvencimientocontrol int, direccion text)");
        db.execSQL("create table conductor(idconductor int primary key, usuario text, contraseña text, correo text, telefono text, dni text, dvencimientobrevette int, idmovilidad int)");
        db.execSQL("create table rutas(idruta int primary key, idmovilidad int, idcliente int, direccioninicio text, direccionfin text, tpromedio int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
