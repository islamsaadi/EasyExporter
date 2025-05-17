package com.islamsaadi.easyexporterlib.exporters;

import android.content.Context;

import com.islamsaadi.easyexporterlib.EasyExporter.ProgressListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CsvExporter {

    public static <T> void export(Context ctx,
                                  File outputDir,
                                  List<T> data,
                                  List<String> fields,
                                  String fileName,
                                  ProgressListener progressListener) throws Exception {

        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        File file = new File(outputDir, fileName + ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {

            // Header
            writer.writeNext(fields.toArray(new String[0]));

            // rows
            int total = data.size();
            for (int i = 0; i < total; i++) {
                T item = data.get(i);
                String[] row = new String[fields.size()];
                for (int j = 0; j < fields.size(); j++) {
                    Field f = item.getClass().getDeclaredField(fields.get(j));
                    f.setAccessible(true);
                    Object val = f.get(item);
                    if (val instanceof Number) {
                        // This ensures no problem with double numbers 10.00
                        // which is 10,00 in some langs(like Italian) so it keeps 10.00 always
                        // because csv delimiter is , by default.
                        row[j] = nf.format(val);
                    } else {
                        row[j] = val != null ? val.toString() : "";
                    }
                }
                writer.writeNext(row);

                // TEST PROGRESS BAR
                try {
                    Thread.sleep(1000);  // 1sec per row
                } catch (InterruptedException ignored) {}

                if (progressListener != null) {
                    progressListener.onProgress(i + 1, total);
                }
            }
        }
    }
}
