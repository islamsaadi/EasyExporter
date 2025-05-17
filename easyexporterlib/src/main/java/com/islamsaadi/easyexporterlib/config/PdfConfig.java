package com.islamsaadi.easyexporterlib.config;

public class PdfConfig {
    private final int pageWidth;
    private final int pageHeight;
    private final int marginLeft;
    private final int marginTop;
    private final int marginBottom;
    private final int columnSpacing;
    private final int rowSpacing;
    private final float textSize;

    private PdfConfig(Builder b) {
        this.pageWidth      = b.pageWidth;
        this.pageHeight     = b.pageHeight;
        this.marginLeft     = b.marginLeft;
        this.marginTop      = b.marginTop;
        this.marginBottom   = b.marginBottom;
        this.columnSpacing  = b.columnSpacing;
        this.rowSpacing     = b.rowSpacing;
        this.textSize       = b.textSize;
    }

    public int getPageWidth()     { return pageWidth; }
    public int getPageHeight()    { return pageHeight; }
    public int getMarginLeft()    { return marginLeft; }
    public int getMarginTop()     { return marginTop; }
    public int getMarginBottom()  { return marginBottom; }
    public int getColumnSpacing() { return columnSpacing; }
    public int getRowSpacing()    { return rowSpacing; }
    public float getTextSize()    { return textSize; }

    public static class Builder {
        private int pageWidth     = 595;
        private int pageHeight    = 842;
        private int marginLeft    = 10;
        private int marginTop     = 25;
        private int marginBottom  = 50;
        private int columnSpacing = 100;
        private int rowSpacing    = 20;
        private float textSize    = 12f;

        public Builder setPageWidth(int w)   { this.pageWidth = w; return this; }
        public Builder setPageHeight(int h)  { this.pageHeight = h; return this; }
        public Builder setMarginLeft(int m)  { this.marginLeft = m; return this; }
        public Builder setMarginTop(int m)   { this.marginTop = m; return this; }
        public Builder setMarginBottom(int m){ this.marginBottom = m; return this; }
        public Builder setColumnSpacing(int s){ this.columnSpacing = s; return this; }
        public Builder setRowSpacing(int s)  { this.rowSpacing = s; return this; }
        public Builder setTextSize(float s)  { this.textSize = s; return this; }

        public PdfConfig build() {
            return new PdfConfig(this);
        }
    }
}
