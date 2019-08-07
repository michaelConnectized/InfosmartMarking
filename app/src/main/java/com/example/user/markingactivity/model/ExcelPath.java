package com.example.user.markingactivity.model;

import android.os.Environment;

public class ExcelPath {
    public static final String homepage = Environment.getExternalStorageDirectory()+"/infosmart";
    public static final String LogPath = homepage+"/log";

    public static final String Address = homepage+"/addr";
    public static final String Marking = homepage+"/MarkingData";
    public static final String FilterLandmark = homepage+"/lm";
    public static final String TargetLog = LogPath+"/targets";
    public static final String OtherLog = LogPath+"/others";

    public static final String SendingLog = LogPath+"/failSent";

    public static String xls(String file) {
        return file+".xls";
    }
    public static String xlsx(String file) {
        return file+".xlsx";
    }
}
