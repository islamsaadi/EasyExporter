package com.islamsaadi.easyexporterlib.exporters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import com.islamsaadi.easyexporterlib.EasyExporter.ProgressListener;
import com.islamsaadi.easyexporterlib.config.PdfConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;

public class PdfExporter {

    public static <T> void export(Context ctx,
                                  File outputDir,
                                  List<T> data,
                                  List<String> fields,
                                  String fileName,
                                  ProgressListener progressListener,
                                  PdfConfig config) throws Exception {
        File file = new File(outputDir, fileName + ".pdf");
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo
                .Builder(config.getPageWidth(), config.getPageHeight(), 1)
                .create();

        Paint paint = new Paint();
        paint.setTextSize(config.getTextSize());

        int total = data.size();
        int xStart = config.getMarginLeft();
        int yStart = config.getMarginTop();
        int breakpoint = config.getPageHeight() - config.getMarginBottom();

        int x = xStart;
        int y = yStart;
        PdfDocument.Page page = doc.startPage(info);

        // draw header
        for (String header : fields) {
            page.getCanvas().drawText(header, x, y, paint);
            x += config.getColumnSpacing();
        }
        x = xStart;
        y += config.getRowSpacing();

        // draw rows
        for (int i = 0; i < total; i++) {
            T item = data.get(i);
            for (String fieldName : fields) {
                Field f = item.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                String text = String.valueOf(f.get(item));
                page.getCanvas().drawText(text, x, y, paint);
                x += config.getColumnSpacing();
            }
            x = xStart;
            y += config.getRowSpacing();

            // page break
            if (y > breakpoint) {
                doc.finishPage(page);
                page = doc.startPage(info);
                y = yStart;
            }

            if (progressListener != null) {
                progressListener.onProgress(i + 1, total);
            }
        }

        doc.finishPage(page);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            doc.writeTo(fos);
        }
        doc.close();
    }
}
