package com.islamsaadi.easyexporterlib.ui;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.text.TextUtilsCompat;

import com.islamsaadi.easyexporterlib.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExportButton extends AppCompatButton {
    public enum ExportFormat { CSV, JSON, PDF }

    public interface OnExportClickListener {
        void onExportClicked(ExportFormat format, String fileName, List<String> selectedFields);
    }

    private OnExportClickListener listener;
    private List<String> availableFields = new ArrayList<>();

    public ExportButton(Context context) {
        this(context, null);
    }

    public ExportButton(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.buttonStyle);
    }

    public ExportButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setText(R.string.export_button_label);
        setOnClickListener(v -> showFormatDialog());
    }

    public void setAvailableFields(List<String> fields) {
        this.availableFields = fields;
    }

    public void setOnExportClickListener(OnExportClickListener listener) {
        this.listener = listener;
    }

//    private void showFormatDialog() {
//        String[] items = {
//                getContext().getString(R.string.format_csv),
//                getContext().getString(R.string.format_json),
//                getContext().getString(R.string.format_pdf)
//        };
//        new AlertDialog.Builder(getContext())
//                .setTitle(R.string.dlg_select_format_title)
//                .setItems(items, (dialog, which) -> {
//                    ExportFormat format = ExportFormat.valueOf(items[which]);
//                    if (format == ExportFormat.JSON) {
//                        showFileNameDialog(format, availableFields);
//                    } else {
//                        showFieldSelectionDialog(format);
//                    }
//                })
//                .show();
//    }

    private void showFormatDialog() {
        final ExportFormat[] formats = ExportFormat.values();
        String[] labels = new String[] {
                getContext().getString(R.string.format_csv),
                getContext().getString(R.string.format_json),
                getContext().getString(R.string.format_pdf)
        };

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dlg_select_format_title)
                .setItems(labels, (dlg, which) -> {
                    ExportFormat format = formats[which];
                    if (format == ExportFormat.JSON) {
                        showFileNameDialog(format, availableFields);
                    } else {
                        showFieldSelectionDialog(format);
                    }
                })
                .create();

        applyDialogLayoutDirection(dialog);
        dialog.show();
    }

    private void showFieldSelectionDialog(ExportFormat format) {
        if (availableFields.isEmpty()) {
            Toast.makeText(
                    getContext(),
                    R.string.err_no_fields,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        boolean[] checked = new boolean[availableFields.size()];
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dlg_select_fields_title)
                .setMultiChoiceItems(
                        availableFields.toArray(new String[0]),
                        checked,
                        (dlg, which, isChecked) -> checked[which] = isChecked
                )
                .setPositiveButton(
                        R.string.action_next,
                        (dlg, which) -> {
                            List<String> selected = new ArrayList<>();
                            for (int i = 0; i < availableFields.size(); i++) {
                                if (checked[i]) selected.add(availableFields.get(i));
                            }
                            if (selected.isEmpty()) {
                                Toast.makeText(
                                        getContext(),
                                        R.string.err_select_at_least_one,
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                showFileNameDialog(format, selected);
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        applyDialogLayoutDirection(dialog);
        dialog.show();
    }

    private void showFileNameDialog(ExportFormat format, List<String> selectedFields) {
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.hint_filename);

        String title = getContext().getString(R.string.dlg_filename_title, format.name());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setView(input)
                .setPositiveButton(
                        android.R.string.ok,
                        (dlg, which) -> {
                            String fileName = input.getText().toString().trim();
                            if (fileName.isEmpty()) {
                                fileName = "export_" + System.currentTimeMillis();
                            }
                            if (listener != null) {
                                listener.onExportClicked(format, fileName, selectedFields);
                            } else {
                                Toast.makeText(
                                        getContext(),
                                        R.string.err_no_listener,
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        applyDialogLayoutDirection(dialog);
        dialog.show();
    }

    private void applyDialogLayoutDirection(@NonNull AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            View decor = window.getDecorView();
            int dir = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
            decor.setLayoutDirection(dir);
        } else {
            Log.w("ExportButton", "Dialog window was null; skipping RTL/LTR adjustment");
        }
    }
}
