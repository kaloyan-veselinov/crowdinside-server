package com.kaloyanveselinov.dataanalyzer;

import com.kaloyanveselinov.datacollection.DataSet;

import java.io.File;

public class DataExporter {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java DataExporter filename.JSON");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if (file.exists() && !file.isDirectory()) {
            DataSet dataSet;
            switch (args.length) {
                case 3:
                    dataSet = new DataSet(file, args[1], Integer.parseInt(args[2]));
                    break;
                case 2:
                    dataSet = new DataSet(file, "", Integer.parseInt(args[1]));
                    break;
                default:
                    dataSet = new DataSet(file, "", 500);
            }
            dataSet.toCSV();
        } else System.err.println("No such file");
    }
}
