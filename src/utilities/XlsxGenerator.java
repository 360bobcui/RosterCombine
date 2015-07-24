package utilities;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Given the DMPO roster and RCF roster, generate the final xlsx report
 * @author SPC Cui
 */
public class XlsxGenerator {
    private static final String fileName = "NPD.xlsx";
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd");
    private final Workbook wb = new XSSFWorkbook(); 
    private final CellStyle styleRCF = createRCFStyle();
    private final CellStyle styleDMPO = createDMPOStyle();
    private final CellStyle standardStyle = createStandardStyle();
    private final CellStyle headerStyle = createHeaderStyle();
//    private final CellStyle formulaStyle = createFormulaStyle();
//    private final CellStyle ssnStyle = createSSNStyle();
    
    private final Map<String, List<String>> dmpoDb;
    private final Map<String, List<String>> rcfDb;
    private final List<String> npdList;  // a list of SSN of NPD service members
    private final List<String> lesNAList;  // a list of sm whose LES is not avilable
    
    private Sheet sheet;    
    
    // npd contains a list of inmates (SSN) not getting paid
    // lesList contains a list of inmates (SSN) not getting LES
    public XlsxGenerator (Map<String, List<String>> dmpo, Map<String, List<String>> rcf,
            List<String> npd, List<String> thisLesNAList) throws IOException, ParseException {
        lesNAList = thisLesNAList;
        dmpoDb = dmpo;
        rcfDb = rcf;
        npdList = npd;
        File file = new File(fileName);        
        if (file.exists()){
            int response = JOptionPane.showConfirmDialog(null,  fileName +  " "
                    + "already exsits. Do you want to overwrite it?", "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                //createXlsxMain(isNew, destFileName);
                createHeaderRow(fileName);
                createReportBody(fileName); 
            } else if (response == JOptionPane.NO_OPTION){
                String[] fileInfo = fileName.split("\\.");
                String destFileName = fileInfo[0] + " copy.xlsx";                
                createHeaderRow(destFileName);
                createReportBody(destFileName); 
            }
        }else {
            //createXlsxMain(isNew, destFileName);
            createHeaderRow(fileName);
            createReportBody(fileName); 
        } 
    }
    
    // create header for the xlsx file
    private void createHeaderRow(String fileName) 
            throws IOException {
       // WB = new XSSFWorkbook(); 
        FileOutputStream output;
        //Sheet sheet;
       
        int titleLen;
        String[] header; // the header of the spreadsheet

        titleLen = GlobalVar.REPORT_TITLES.length; 
        sheet = wb.createSheet("SHEET1");          
        header = GlobalVar.REPORT_TITLES;        
   
         //set column widths, the width is measured in units of 1/256th of a character width
        for (int i = 0; i < titleLen; i++){
            sheet.setColumnWidth(i, 192 * GlobalVar.COL_WIDTH);
        }
        
        sheet.setColumnWidth(0, 256 * GlobalVar.COL_WIDTH);  //SSN cell, RCF roster
        sheet.setColumnWidth(1, 488 * GlobalVar.COL_WIDTH);  // name cell, RCF roster
        sheet.setColumnWidth(GlobalVar.FRONT_PAD_NO - 2, 128 * GlobalVar.COL_WIDTH);  //LES
        sheet.setColumnWidth(GlobalVar.FRONT_PAD_NO - 1, 128 * GlobalVar.COL_WIDTH);  //NPD
        sheet.setColumnWidth(GlobalVar.FRONT_PAD_NO, 256 * GlobalVar.COL_WIDTH);  //SSN cell, DMPO roster
        sheet.setColumnWidth(GlobalVar.FRONT_PAD_NO + 1, 488 * GlobalVar.COL_WIDTH);  //name cell, DMPO roster
                
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        Row row = sheet.createRow(0); // first row
        
        //set value for the first row
        for(int i = 0; i < header.length; i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(header[i]);         
            cell.setCellStyle(headerStyle);
        }   
        
        //freeze the first row
        sheet.createFreezePane(0, 1);
        
        try {
            output = new FileOutputStream(fileName);
            wb.write(output);
            output.close(); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XlsxGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Failed: Excel worksheet is used by another user!");
        }
    }
    
    // generate the body of the spread sheetm
    private void createReportBody(String fileName) throws ParseException, IOException {
        //Scanner input = null;
        int rowNum = 1; // skip the header line
        //System.out.println(input.hasNextLine());
        Set<String> rcfSSNs = rcfDb.keySet();
        Set<String> dmpoSSNs = dmpoDb.keySet();
        Iterator<String> rcfSSNsIt = rcfSSNs.iterator();       
        
        while(rcfSSNsIt.hasNext()) {
            String rcfSSN = rcfSSNsIt.next();
            if (dmpoSSNs.contains(rcfSSN)) {
                List<String> listRcf = rcfDb.get(rcfSSN);
                List<String> listDmpo = dmpoDb.get(rcfSSN);
                if (npdList.contains(rcfSSN) && !lesNAList.contains(rcfSSN)) {
                    List<String> exportData = padListinMid(listRcf, listDmpo, true, false);  // with NPD marker, without LES marker
                    addNewLine(fileName, exportData, rowNum, standardStyle);
                } else if (npdList.contains(rcfSSN) && lesNAList.contains(rcfSSN)) {
                    List<String> exportData = padListinMid(listRcf, listDmpo, true, true);  // with NPD marker, with LES marker
                    addNewLine(fileName, exportData, rowNum, standardStyle);
                } else if (!npdList.contains(rcfSSN) && !lesNAList.contains(rcfSSN)){    // without NPD and LES marker              
                    List<String> exportData = padListinMid(listRcf, listDmpo, false, false);
                    addNewLine(fileName, exportData, rowNum, standardStyle);                    
                } else {
                    List<String> exportData = padListinMid(listRcf, listDmpo, false, true);
                    addNewLine(fileName, exportData, rowNum, standardStyle);        
                }
                dmpoDb.remove(rcfSSN);
            } else {
                List<String> listRcf = rcfDb.get(rcfSSN);
                if (npdList.contains(rcfSSN)){
                    List<String> exportList = padNPDfromBack(listRcf);
                    addNewLine(fileName, exportList, rowNum, styleRCF);  
                } else {                    
                    addNewLine(fileName, listRcf, rowNum, styleRCF);                
                }
            }
            rowNum++;
        }
        
        Iterator<String> dmpoSSNsIt = dmpoSSNs.iterator();        
        while(dmpoSSNsIt.hasNext()){   //rcf roster is drained
            String dmpoSSN = dmpoSSNsIt.next();
            List<String> listDmpo = dmpoDb.get(dmpoSSN);
            addNewLine(fileName, listDmpo, rowNum, styleDMPO);            
            rowNum++;
        }
        
        FileOutputStream output;
        try {
            output = new FileOutputStream(fileName);
            wb.write(output);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XlsxGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XlsxGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//    // pad null for the given list in the front 
    private List<String> padNPDfromFront(List<String> data) {
        List<String> ans = new ArrayList<String>();
        ans.add("");
        ans.add("NPD");
        for(String cellValue : data){
            ans.add(cellValue);
        }
        return ans;        
    }
//    
//     // pad null for the given list in the back 
    private List<String> padNPDfromBack(List<String> data) {
        List<String> ans = new ArrayList<String>();
        for(String cellValue : data){
            ans.add(cellValue);
        }
        ans.add("");
        ans.add("NPD");
        return ans;        
    }
//    
     // pad null for the given list in the middle 
    private List<String> padListinMid(List<String> data1, List<String> data2, Boolean isNPD, Boolean isLES_NA) {
        List<String> ans = new ArrayList<String>();
        for(String cellValue : data1){
            ans.add(cellValue);
        }
        if (isLES_NA){
            ans.add("N");
        } else {
            ans.add("");
        }
        if (isNPD){
            ans.add("NPD");
        } else {
            ans.add("");
        }
        for(String cellValue : data2){
            ans.add(cellValue);
        }
        return ans;        
    }
 
    
    
     // add a row of new leave to the spread sheet
    public void addNewLine(String destFilePath, List<String> data, int rowNum, CellStyle style) throws ParseException, FileNotFoundException, IOException {
        // WB = new XSSFWorkbook(); 
//        FileOutputStream output;
//        Calendar calendar = Calendar.getInstance(); // for dates;
        Row row;
        Cell cell;        
        //String[] data = input.nextLine().split(GlobalVar.PARSE);
        //System.out.println(Arrays.toString(data));
        row = sheet.createRow(rowNum); // row in excel 
        String[] header = GlobalVar.REPORT_TITLES;
        int dataSize = data.size();
        if (dataSize == GlobalVar.RCF_DATA_NO) {
            for(int i = 0; i < dataSize; i++){
                cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellValue(data.get(i));                             
            }
        } else if (dataSize == GlobalVar.DMPO_DATA_NO) {
            for(int i = 0; i < dataSize; i++){
                cell = row.createCell(GlobalVar.FRONT_PAD_NO + i);  // start with non-empty cell
                cell.setCellStyle(style);
                cell.setCellValue(data.get(i));                             
            }
        } else {
            for(int i = 0; i < dataSize; i++){
                cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellValue(data.get(i));                             
            }        
        
        }
            
//        output = new FileOutputStream(destFilePath);
//        wb.write(output);
//        output.close(); 
    }
    
    private CellStyle createStandardStyle(){
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short)8);
        style.setFont(font);
//        style.setBorderRight(CellStyle.BORDER_THIN);
//        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        style.setBorderBottom(CellStyle.BORDER_THIN);
//        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        style.setBorderLeft(CellStyle.BORDER_THIN);
//        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        style.setBorderTop(CellStyle.BORDER_THIN);
//        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createRCFStyle() {
        CellStyle style = createStandardStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDMPOStyle() {
        CellStyle style = createStandardStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }
    
    private CellStyle createDateStyle(){
        DataFormat df = wb.createDataFormat(); 

        CellStyle style = createStandardStyle();
        style.setDataFormat(df.getFormat("yyMMdd"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        return style;
    }
    
    private CellStyle createSSNStyle(){
        Font font3 = wb.createFont();
        DataFormat df = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
     
        style = createStandardStyle();
        style.setDataFormat(df.getFormat("000-00-0000"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        //style.setFont(font3);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createFormulaStyle(){
        Font font3 = wb.createFont();
        DataFormat df = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
//        font3.setFontHeightInPoints((short)14);
//        font3.setColor(IndexedColors.DARK_BLUE.getIndex());
//        font3.setBoldweight(Font.BOLDWEIGHT_BOLD);       
        style = createStandardStyle();

        style.setAlignment(CellStyle.ALIGN_LEFT);
        //style.setFont(font3);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createHeaderStyle() {
        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createStandardStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        //style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        //style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);  
        return style;
    }
}
