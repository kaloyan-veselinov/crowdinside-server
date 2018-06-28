package com.kaloyanveselinov.dataexporter;

import com.kaloyanveselinov.datacollection.DataSet;
import java.io.File;

/**
 * Reads, parses and aggregates data send by the app; exports the result in a CSV file
 *
 * Usage:
 * - <code>java -jar data-exporter.jar dataset.JSON 1000 walk</code> to specify both aggregation time and gait ground truth
 * - <code>java -jar data-exporter.jar dataset.JSON 1000</code> to specify only aggregation time (no labeling)
 * - <code>java -jar data-exporter.jar dataset.JSON</code> to use default aggregation time (500ms) and no labeling
 *
 * @author Kaloyan Veselinov
 * @version 1.0
 */
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
                // usage example: java -jar data-exporter.jar dataset.JSON 1000 walk
                case 3:
                    dataSet = new DataSet(file);
                    dataSet.setAggregationTime(Integer.parseInt(args[1]));
                    dataSet.setType(args[1]);
                    break;
                // usage example: java -jar data-exporter.jar dataset.JSON 1000
                case 2:
                    dataSet = new DataSet(file);
                    dataSet.setAggregationTime(Integer.parseInt(args[1]));
                    break;
                // uses default aggregation time and no labeling
                default:
                    dataSet = new DataSet(file);
                    dataSet.setAggregationTime(500);
            }
            dataSet.toCSV();
        } else System.err.println("No such file");
    }
}
