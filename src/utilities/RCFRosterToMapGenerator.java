/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import java.util.Map;

/**
 *  The xlsx file should have two sheets
 *  the first sheet contains personal info of the prisoners 
 *  REG# SSN INMATE CONF TYPE PRESENCE ARRDT MRD
 *  the second sheet contains the service info of the prisoners.  
 *   REG# INMATE SEX SERVICE(branch) TYPE(enlisted/officers) HOUSING CUSTODY
 * @author SPC Cui
 */
public class RCFRosterToMapGenerator {
    private Map<String, List<String>> db;
    
    // sheet number 1 for personal info. i.ie SSN, name, Arrival date, confinement type
    private static final int SHEET_NO_PERSONAL = 0;  // sheet contains personal info, arrival date
    private static final int REG_INDX_PERSONAL = 0;
    private static final int SSN_INDX_PERSONAL = 1;
    private static final int INMATE_NAME_INDX_PERSONAL = 2;
    private static final int CONF_TYPE_INDX_PERSONAL = 3; 
    //private static final int 
    private static final int ARR_DT_INDX_PERSONAL = 5;  
    
  
    //sheet number 2 for service info. i.e. army, navy, marines
    private static final int SHEET_NO_SERVICES = 1;  // sheet contains service info
    private static final int REG_INDX_SERVICES = 0;  
    //private static final int INMATE_NAME_INDX_SERVICES = 1;
    private static final int INMATE_BRANCH_TYPE_INDX_SERVICES = 3;   // branch type
    private static final int INMATE_SERVICE_TYPE_INDX_SERVICES = 4;  //officers / enlisted
 
    private static final String[] header = {"SSN", "INMATE", "CONF TYPE", "ARR DT", "TYPE"};
    
    public RCFRosterToMapGenerator(String xlsxFileName) throws FileNotFoundException, IOException {
        
        Map<String, String> armyPrisoners = getArmyPrisoners(xlsxFileName);
        Map<String, List<String>> personalInfo = getPersonalInfo(xlsxFileName);
        db = dbGenerator(armyPrisoners, personalInfo);
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
    
    // generate a roster and saved as roster.txt
    public List<String> rosterGenerator() throws FileNotFoundException {
        List<String> list = new ArrayList<String>();
        PrintStream output = new PrintStream(new File(GlobalVar.SSN_FILE_NAME));
        Set<String> ssnSet = db.keySet();
        for (String ssn : ssnSet) {
            output.println(ssn);
            list.add(ssn);
        }
        return list;
    }
    
    // return a list of soldier who arrives the RCF days before given cutoff (default: 60 days)
    public List<String> lesSSNList(int cutoff) throws FileNotFoundException {
        List<String> list = new ArrayList<String>();
        //PrintStream output = new PrintStream(new File(GlobalVar.SSN_FILE_NAME));
        Set<String> ssnSet = db.keySet();
        MyDate today = new MyDate();
        for (String ssn : ssnSet) {
            List<String> value = db.get(ssn);
            String dateString = value.get(ARR_DT_INDX_PERSONAL);  // 6/16/2015
            MyDate date = new MyDate(dateString);  
            String confType = value.get(CONF_TYPE_INDX_PERSONAL);
            if(date.getDaysDiff(today) <= cutoff){           
                list.add(ssn);
            } else if (confType != null && confType.equalsIgnoreCase("PRE-TRIAL")) {
                list.add(ssn);
            }
        }
        return list;
    }
    
    // create a map of army prisoners <REG, TYPE>
    private Map<String, String> getArmyPrisoners (String xlsxFileName) throws FileNotFoundException, IOException {
        Map<String, String> map = new TreeMap<String, String>();
        File myFile = new File(xlsxFileName);
        FileInputStream fis = new FileInputStream(myFile);
        
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(SHEET_NO_SERVICES);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();  //skip the header row;

        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cellReg = row.getCell(REG_INDX_SERVICES);            
            Cell cellBranch = row.getCell(INMATE_BRANCH_TYPE_INDX_SERVICES);
            Cell cellService = row.getCell(INMATE_SERVICE_TYPE_INDX_SERVICES); //officer / enlisted
            String reg = cellReg.getStringCellValue();           
            String service = cellService.getStringCellValue();
            String branch = cellBranch.getStringCellValue();
            
            if (!map.containsKey(reg) && branch.equalsIgnoreCase("Army")) {
                map.put(reg, service);
            }
        }
        return map;
    }
    
    // get the personal info of each prisoner, army, navy, af, etc
    private Map<String, List<String>> getPersonalInfo(String xlsxFileName) throws FileNotFoundException, IOException {
        Map<String, List<String>> map = new TreeMap<>();
        File myFile = new File(xlsxFileName);
        FileInputStream fis = new FileInputStream(myFile);
        
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(SHEET_NO_PERSONAL);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();  //skip the header row;
        DataFormatter df = new DataFormatter();                                
        
        
        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cellReg = row.getCell(REG_INDX_PERSONAL);

            String reg = cellReg.getStringCellValue();
            
            if (!map.containsKey(reg)) {
                List<String> list = new LinkedList<>();
                String ssn = row.getCell(SSN_INDX_PERSONAL).getStringCellValue();
                String name = row.getCell(INMATE_NAME_INDX_PERSONAL).getStringCellValue();
                String confinementType = row.getCell(CONF_TYPE_INDX_PERSONAL).getStringCellValue();
                String arrDt = df.formatCellValue(row.getCell(ARR_DT_INDX_PERSONAL));
                list.add(ssn.trim());
                list.add(name.trim());
                list.add(confinementType.trim());
                list.add(arrDt.trim());
                map.put(reg, list);
            }
        }
        return map;   
    }
    
    // given the two maps generated from the two xlsx sheets, and generate the final map
    // key = SSN,  value = list<String>
    private Map<String, List<String>> dbGenerator(Map<String, String> armyPrisonersMap
            , Map<String, List<String>> personalInfoMap) {
    
        Map<String, List<String>> mydb = new TreeMap<>(); 
        Set<String> regs = armyPrisonersMap.keySet();  //all the army prisoners
        for(String reg : regs) {
            if(personalInfoMap.containsKey(reg)) {
               List<String> l = personalInfoMap.get(reg); //extract the list of personal info
               String ssn = l.get(0);  //first added into the list
               String truncatedSSN = ssn.replaceAll("-",""); // get rid of "-"
               l.add(armyPrisonersMap.get(reg)); // add one more info from a different map
               mydb.put(truncatedSSN, l); 
            }
        }
        return mydb;
    }
    
    public void printMap() {
        int globalCount = 0;
        Set<String> keys = db.keySet();
        for (String key : keys) {
            globalCount++;
            System.out.print(key + " ");
            List<String> list = db.get(key);
            System.out.println(list);
        }
        System.out.println("Total: " + globalCount);
    }
}


