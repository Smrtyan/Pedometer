package com.example.ubnt.pedometer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SimpleDBHelper
//    private static final String UPDATE_STUDENT_TABLE
//            = "alter table " + DAY_OF_CALORIE + " add height integer";


    (context: Context, version: Int) : SQLiteOpenHelper(context,
    DB_STEP_COUNT, null, version) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DAY_OF_CALORIE_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

        when (i) {
            1,
                //upgrade logic from 1 to 2
                //                sqLiteDatabase.execSQL(CREATE_DAY_OF_CALORIE_TABLE);
            2 -> {
            }
            else -> throw IllegalStateException("unknown oldVersion $i")
        }// upgrade logic from 2 to 3

    }

    companion object {

        private val DB_STEP_COUNT = "StepCount.db"
        private val TB_STEP = "step"
        private val CREATE_DAY_OF_CALORIE_TABLE =
            "create table $TB_STEP(id integer primary key autoincrement,day text, stepCount text)"
    }
}