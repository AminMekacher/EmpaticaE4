package com.example.android.empaticae4;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by aminmekacher on 02.08.18.
 */

/** Class used to write the data collected by the wristband and registered on Firebase to an Excel file. Was not working when I
 * was working on the project
 *
 */

public class WriteExcel {

    private WritableCellFormat timesBoldUnderLine;
    private WritableCellFormat times;
    private String inputFile;

    public void setOutputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void write() throws IOException, WriteException {

        String root = Environment.getExternalStorageDirectory().toString();
        File newDir = new File(root + "/empaticaOutput");
        newDir.mkdirs();

        String filename = "output.xls";
        File file = new File(newDir, filename);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("fr", "CH"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet excelSheet = workbook.getSheet(0);
        createLabel(excelSheet);
        createContent(excelSheet);

        workbook.write();
        workbook.close();
    }

    private void createLabel(WritableSheet sheet) throws WriteException {
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        times = new WritableCellFormat(times10pt);
        times.setWrap(true);

        WritableFont times10ptBoldUnderline = new WritableFont(
                WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderLine = new WritableCellFormat(times10ptBoldUnderline);
        timesBoldUnderLine.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderLine);

        addCaption(sheet, 0, 0, "Header 1");
        addCaption(sheet, 1, 0, "This is another header");
    }

    private void createContent(WritableSheet sheet) throws WriteException, RowsExceededException {
        for (int i = 1; i < 10; i++) {
            addNumber(sheet, 0, i, i + 10);
            addNumber(sheet, 1, i, i * i);
        }

        StringBuffer buf = new StringBuffer();
        buf.append("SUM(A2:A10)");
        Formula f = new Formula(0, 10, buf.toString());
        sheet.addCell(f);
        buf = new StringBuffer();
        buf.append("SUM(B2:B10)");
        f = new Formula(1, 10, buf.toString());
        sheet.addCell(f);
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
        throws RowsExceededException, WriteException {

        Label label;
        label = new Label(column, row, s, timesBoldUnderLine);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row, Integer integer)
        throws RowsExceededException, WriteException {

        Number number;
        number = new Number(column, row, integer, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
        throws RowsExceededException, WriteException {

        Label label;
        label = new Label(column, row, s , times);
        sheet.addCell(label);
    }

    public static void main(String[] args)
        throws WriteException, IOException {

        WriteExcel test = new WriteExcel();
        test.setOutputFile("output.xls");
        test.write();
    }
}
