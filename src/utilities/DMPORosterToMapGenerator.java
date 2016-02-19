/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;


/**
 *
 * @author SPC Cui
 */
import java.util.*;
import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class DMPORosterToMapGenerator {
    private Map<String, List<String>> db;

    
    // given the DMPO roster the file.  The header must follow the correct sequence
    
    /* 
    /  FILE at DMPO	
    // SSN
    // NAME
    // ORIGIN
    // TYPE CM
    // REC
    // DMPO notified
    // RELEASED
    // FACILITY
    // PAY STATUS
    // FILE DISPOSITION
    // ETS
    // Remarks
    */
    private static final int FILE_AT_DMPO_INDX = 0;
    private static final int SSN_INDX = 1;
    private static final int NAME_INDX = 2;
    private static final int ORIGIN_INDX = 3;
    private static final int TYPE_CM_INDX = 4;
//    private static final int REC_INDX = 5;
//    private static final int DMPO_NOTIFICATION_INDX = 6;
//    private static final int RELEASED_INDX = 7;
//    private static final int FACILITY_INDX = 8;
//    private static final int PAY_STATUS_INDX = 9;
//    private static final int FILE_DISPOSITION_INDX = 10;
//    private static final int ETS_INDX = 11;    
//    private static final int REMARKS_INDX = 12;  
    
    //private static final int SSN_LEN = 9;
   
    public DMPORosterToMapGenerator(String xlsxFileName) throws FileNotFoundException, IOException {
        db = new TreeMap();
        File myFile = new File(xlsxFileName); 
        
        FileInputStream fis = new FileInputStream(myFile);
        
        System.out.println(xlsxFileName);
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
        
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);
        Iterator<Row> rowIterator = mySheet.iterator();
        DataFormatter df = new DataFormatter();
//        System.out.println(IndexedColors.YELLOW);
//        System.out.println(IndexedColors.BLUE.getIndex());
//        System.out.println(IndexedColors.RED.getIndex());
//        System.out.println(IndexedColors.WHITE);
//        System.out.println(IndexedColors.BLACK);
//        System.out.println(IndexedColors.GREEN);
        
        rowIterator.next(); // skip the header row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cell = row.getCell(FILE_AT_DMPO_INDX); // File at DMPO      
            int type = cell.getCellType();
            //System.out.println(cell.getStringCellValue());
            
            if (type == HSSFCell.CELL_TYPE_STRING 
                    && cell.getStringCellValue().equalsIgnoreCase("Yes")) { // File at DMPO      
                CellStyle style = cell.getCellStyle();

                // getFillBackgroundColor() always 64.  getFillForegroundColor() is 64 for white
                // getFillForegroundColor() is 0 for yellow
//                System.out.println(style.getFillForegroundColor());
//                //if (style.getFillForegroundColorColor() != null)
//                    System.out.println(style.getFillForegroundColorColor());
//               
//                System.out.println(style.getFillBackgroundColor());
//                //if (style.getFillBackgroundColorColor() != null)
//                    System.out.println(style.getFillBackgroundColorColor());
                 //if(style.getFillForegroundColor() != IndexedColors.YELLOW.getIndex()) { 
                Color color = style.getFillForegroundColorColor();
                if(color == null || color.toString().equals(GlobalVar.WHITE)) { // no fill or fill with white

                    Cell ssnCell = row.getCell(SSN_INDX);
                    String ssnString = df.formatCellValue(ssnCell); //return ***-**-****
                    ssnString = readSSN(ssnString).trim();
                    if (!db.containsKey(ssnString)) {
                        List<String> list = new LinkedList<String>();
                        String ssn = displayFormatSSN(ssnString).trim();
                        String name = row.getCell(NAME_INDX).getStringCellValue().trim();
                        String dutyStation = row.getCell(ORIGIN_INDX).getStringCellValue().trim();
                        String typeCM = row.getCell(TYPE_CM_INDX).getStringCellValue().trim();
                        //String typeCM = row.getCell(TYPE_CM_INDX).getStringCellValue();
                        list.add(ssn);
                        list.add(name);
                        list.add(dutyStation);
                        list.add(typeCM);
                        db.put(ssnString, list);
                    }
                }                
            }       
        }        
    }
    
    public Map<String, List<String>> getDb() {
        return db;
    }
    // pre: 
    private String readSSN(String ssnString) {
       
        ssnString = ssnString.replace("-","");
        while(ssnString.length() < GlobalVar.SSN_LEN) {
            ssnString = "0" + ssnString;
        }
        //System.out.println(ssnString);
        
        return ssnString;
    }
    
    public void printMap() {
        int globalCount = 0;
        Set<String> keys = db.keySet();
        for (String key : keys) {
            globalCount++;
            //System.out.print(key + " ");
            List<String> list = db.get(key);
            //System.out.println(list);
        }
        System.out.println("Total: " + globalCount);
    }

    private String displayFormatSSN(String ssnString) {
        String ssn = ssnString.substring(0,3) + "-" + ssnString.substring(3,5) + 
                "-" + ssnString.substring(5); 
        return ssn;
    }
}
