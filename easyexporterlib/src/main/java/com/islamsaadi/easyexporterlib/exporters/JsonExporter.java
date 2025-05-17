package com.islamsaadi.easyexporterlib.exporters;

import android.content.Context;

import com.google.gson.Gson;
import com.islamsaadi.easyexporterlib.EasyExporter.ProgressListener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

public class JsonExporter {

    public static <T> void export(Context ctx,
                                  File outputDir,
                                  List<T> data,
                                  String fileName,
                                  ProgressListener progressListener) throws Exception {
        File file = new File(outputDir, fileName + ".json");
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(file)) {
            writer.write("[");
            int total = data.size();
            for (int i = 0; i < total; i++) {
                T item = data.get(i);
                gson.toJson(item, writer);
                if (i < total - 1) {
                    writer.write(",");
                }

//                // TEST PROGRESS BAR
//                try {
//                    Thread.sleep(1000);  // 1sec per row
//                } catch (InterruptedException ignored) {}

                if (progressListener != null) {
                    progressListener.onProgress(i + 1, total);
                }
            }
            writer.write("]");
        }
    }
}
