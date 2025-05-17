package com.islamsaadi.easyexporterlib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.islamsaadi.easyexporterlib.config.PdfConfig;
import com.islamsaadi.easyexporterlib.exporters.CsvExporter;
import com.islamsaadi.easyexporterlib.exporters.JsonExporter;
import com.islamsaadi.easyexporterlib.exporters.PdfExporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EasyExporter {
    private final Context context;
    private final File outputDir;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /** Callback for completion or error, always on the MAIN thread */
    public interface Callback {
        void onSuccess(String filePath);
        void onError(Exception e);
    }

    /** Progress listener: done / total, always on the MAIN thread */
    public interface ProgressListener {
        void onProgress(int done, int total);
    }

    public EasyExporter(Context context) {
        this(context, context.getExternalFilesDir(null));
    }

    public EasyExporter(Context context, File outputDir) {
        this.context = context.getApplicationContext();
        if (outputDir == null || (!outputDir.exists() && !outputDir.mkdirs())) {
            throw new IllegalArgumentException("Cannot create outputDir: " + outputDir);
        }
        this.outputDir = outputDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    // ——— CSV ———

    public <T> void exportAsCsv(List<T> data,
                                List<String> fields,
                                String fileName,
                                Callback callback) {
        exportAsCsv(data, fields, fileName, t -> true, null, callback);
    }

    public <T> void exportAsCsv(List<T> data,
                                List<String> fields,
                                String fileName,
                                Predicate<T> filter,
                                ProgressListener progressListener,
                                Callback callback) {
        new Thread(() -> {
            try {
                // filter data
                List<T> filtered = new ArrayList<>();
                for (T item : data) {
                    if (filter.test(item)) filtered.add(item);
                }
                // delegate to exporter
                CsvExporter.export(
                        context, outputDir, filtered, fields, fileName,
                        wrapProgress(progressListener)
                );
                String path = new File(outputDir, fileName + ".csv").getAbsolutePath();
                mainHandler.post(() -> callback.onSuccess(path));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        }).start();
    }

    // ——— JSON ———

    public <T> void exportAsJson(List<T> data,
                                 String fileName,
                                 Callback callback) {
        exportAsJson(data, fileName, t -> true, null, callback);
    }

    public <T> void exportAsJson(List<T> data,
                                 String fileName,
                                 Predicate<T> filter,
                                 ProgressListener progressListener,
                                 Callback callback) {
        new Thread(() -> {
            try {
                List<T> filtered = new ArrayList<>();
                for (T item : data) {
                    if (filter.test(item)) filtered.add(item);
                }
                JsonExporter.export(
                        context, outputDir, filtered, fileName,
                        wrapProgress(progressListener)
                );
                String path = new File(outputDir, fileName + ".json").getAbsolutePath();
                mainHandler.post(() -> callback.onSuccess(path));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        }).start();
    }

    // ——— PDF ———

    // Default pdf config
    public <T> void exportAsPdf(List<T> data,
                                List<String> fields,
                                String fileName,
                                Callback callback) {
        PdfConfig defaultConfig = new PdfConfig.Builder().build();
        exportAsPdf(data, fields, fileName, t -> true, null, defaultConfig, callback);
    }

    // Configurable pdf config
    public <T> void exportAsPdf(List<T> data,
                                List<String> fields,
                                String fileName,
                                Predicate<T> filter,
                                ProgressListener progressListener,
                                PdfConfig pdfConfig,
                                Callback callback) {
        new Thread(() -> {
            try {
                List<T> filtered = new ArrayList<>();
                for (T item : data) {
                    if (filter.test(item)) filtered.add(item);
                }
                PdfExporter.export(
                        context, outputDir, filtered, fields, fileName,
                        wrapProgress(progressListener), pdfConfig
                );
                String path = new File(outputDir, fileName + ".pdf").getAbsolutePath();
                mainHandler.post(() -> callback.onSuccess(path));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        }).start();
    }

    private ProgressListener wrapProgress(ProgressListener listener) {
        if (listener == null) return null;
        return (done, total) ->
                mainHandler.post(() -> listener.onProgress(done, total));
    }
}
