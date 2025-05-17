package com.islamsaadi.easyexporter;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.islamsaadi.easyexporterlib.EasyExporter;
import com.islamsaadi.easyexporterlib.EasyExporter.ProgressListener;
import com.islamsaadi.easyexporterlib.ui.ExportButton;
import com.islamsaadi.easyexporterlib.config.PdfConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private CheckBox cbAdultsOnly;
    private EasyExporter exporter;
    private List<SampleItem> dataList;
    private List<String> allFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Locale rtl = new Locale("ar");  // or "he"
//        Locale.setDefault(rtl);
//        Resources res = getResources();
//        Configuration appcfg = res.getConfiguration();
//        appcfg.setLocale(rtl);
//        appcfg.setLayoutDirection(rtl);
//        res.updateConfiguration(appcfg, res.getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) Bind views
        progressBar    = findViewById(R.id.progress_bar);
        cbAdultsOnly   = findViewById(R.id.cb_adults_only);
        ExportButton exportButton = findViewById(R.id.btn_export);

        // 2) Prepare sample data
        dataList = new ArrayList<>();
        dataList.add(new SampleItem("Alice,is",   30.01));
        dataList.add(new SampleItem("Bob",     25.1));
        dataList.add(new SampleItem("Charlie", 15.2));

        // 3) All available fields on SampleItem
        allFields = Arrays.asList("name", "age");

        // 4) Initialize exporter,
        // if you want to pass the Path where to save the exported file
        // use the new EasyExporter(this, outputDir);
        exporter = new EasyExporter(this);

        // 5) Shared completion callback
        EasyExporter.Callback callback = new EasyExporter.Callback() {
            @Override
            public void onSuccess(String filePath) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(
                        MainActivity.this,
                        getString(R.string.export_saved_to, filePath),
                        Toast.LENGTH_LONG
                ).show();
            }
            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(
                        MainActivity.this,
                        getString(R.string.export_error, e.getMessage()),
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        // 6) Progress listener for determinate bar
        ProgressListener progressListener = (done, total) -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(total);
            progressBar.setProgress(done);
        };

        // 7) Make fields selectable in the button dialog
        exportButton.setAvailableFields(allFields);

        // 8) Handle export click with chosen format, filename, and fields
        exportButton.setOnExportClickListener((format, fileName, selectedFields) -> {
            // Decide on filter based on the checkbox
            Predicate<SampleItem> filter = item ->
                    !cbAdultsOnly.isChecked() || item.age >= 18;

            progressBar.setVisibility(View.VISIBLE);
            switch (format) {
                case CSV:
                    exporter.exportAsCsv(
                            dataList,
                            selectedFields,
                            fileName,
                            filter,
                            progressListener,
                            callback
                    );
                    break;

                case JSON:
                    exporter.exportAsJson(
                            dataList,
                            fileName,
                            filter,
                            progressListener,
                            callback
                    );
                    break;

                case PDF:
                    PdfConfig pdfcfg = new PdfConfig.Builder()
                            .setPageWidth(800)
                            .setPageHeight(1200)
                            .setMarginLeft(20)
                            .setMarginTop(30)
                            .setMarginBottom(40)
                            .setColumnSpacing(100)
                            .setRowSpacing(25)
                            .setTextSize(14f)
                            .build();

                    exporter.exportAsPdf(
                            dataList,
                            selectedFields,
                            fileName,
                            filter,
                            progressListener,
                            pdfcfg,
                            callback
                    );
                    break;
            }
        });
    }
}