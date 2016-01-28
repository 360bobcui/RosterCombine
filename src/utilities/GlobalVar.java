/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;


import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
//import java.awt.SplashScreen;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 *
 * @author SPC Cui
 */
public class GlobalVar {  
  //   public static final int DATE_LEN = 6; //150101 -  Jan 1, 2015
    public static final int NUM_SSN_PER_PAGE = 24;
    public static final String WHITE = "org.apache.poi.xssf.usermodel.XSSFColor@1c8ac5fd";
    public static final String YELLOW = "org.apache.poi.xssf.usermodel.XSSFColor@495ecbdd";
//    //Excel writer
    public static final int COL_WIDTH = 14;  //column width of in units of 1/256th of a character width 
    public static final String[] REPORT_TITLES = {"SSN",
            "INMATE", "CONF TYPE", "ARR DT", "TYPE", "LES", "NPD",
        "SSN", "INMATE", "ORIGIN", "CM"}; 
    public static final int RCF_DATA_NO = 5;
    public static final int DMPO_DATA_NO = 4;
    public static final int FRONT_PAD_NO = 7;
    public static final int BACK_PAD_NO = 6;
    public static final int MIDDLE_PAD_NO = 2;
    public static int SPEED1 = 1;
    public static int SPEED2 = 200;
    public static int SPEED_DJMS = 2000;
    public static final int SSN_LEN = 9;
    
    public static String SSN_FILE_NAME = "roster.txt";  //ssn file unprocessedSSN.txt
    public static String UNPROCESSED_SSN_FILE_NAME = "unprocessedSSN_LES.txt";  // no LES available based on DJMS
    
    //dates
    public static final int DATE_LEN = 6; //150101 -  Jan 1, 2015
    public static final int ORDER_LEAVE_DAYS_IN_DMO = 3; // three digits in dmo
    public static final int YEAR_BASE = 2000;  //  two-digit year to four digit year,e.g. 15 -> 2015
    public static final int LAST4SSN_LEN = 4;
    public static final int PACID_LEN = 8;  //e.g. NM1FB5AA 
    public static final double ONE_DAY_IN_MILLISECOND = 86400000.0;
    public static final double SOME_SEC_IN_MILLISECOND = 10.0;  // unit: milliseconds
    
     public static final int CUT_OFF = 90;  // print les 90 days before todays date
    
    public static String validateSSN(String ssn){
        while(ssn.length() < SSN_LEN){
            ssn = "0" + ssn;
        }
        return ssn;
    }
    
    public static void keyInSSN(String SSN, Robot ROBOT) {
        for (int i = 0; i < 9; i++) {
            char digit = SSN.charAt(i);
            //System.out.println(digit);
            ROBOT.keyPress(GlobalVar.digitKeyEventValue(digit));
            ROBOT.keyRelease(GlobalVar.digitKeyEventValue(digit)); 
            ROBOT.delay(GlobalVar.SPEED2);
        }     
        ROBOT.keyPress(KeyEvent.VK_F8);
        ROBOT.keyPress(KeyEvent.VK_F8);
        ROBOT.delay(GlobalVar.SPEED2);//wait for the user to focus on DJMS
    }

    public static void keyInSSNLES(String SSN, Robot ROBOT) {
        for (int i = 0; i < 9; i++) {
            char digit = SSN.charAt(i);
            //System.out.println(digit);
            ROBOT.keyPress(GlobalVar.digitKeyEventValue(digit));
            ROBOT.keyRelease(GlobalVar.digitKeyEventValue(digit)); 
            ROBOT.delay(GlobalVar.SPEED1);
        }     
    }
        
    public static char digitKeyEventValue(char digit){
        switch(digit){
            case '0': return KeyEvent.VK_0; 
            case '1': return KeyEvent.VK_1;
            case '2': return KeyEvent.VK_2;
            case '3': return KeyEvent.VK_3;
            case '4': return KeyEvent.VK_4;
            case '5': return KeyEvent.VK_5;
            case '6': return KeyEvent.VK_6;   
            case '7': return KeyEvent.VK_7;
            case '8': return KeyEvent.VK_8;            
            default: return KeyEvent.VK_9;
        }    
    }
      
    public static char letterKeyEventValue(char letter){
        switch(letter){
            case 'a': return KeyEvent.VK_A; 
            case 'b': return KeyEvent.VK_B;
            case 'c': return KeyEvent.VK_C;
            case 'd': return KeyEvent.VK_D;
            case 'e': return KeyEvent.VK_E;
            case 'f': return KeyEvent.VK_F;
            case 'g': return KeyEvent.VK_G;   
            case 'h': return KeyEvent.VK_H;
            case 'i': return KeyEvent.VK_I;
            case 'j': return KeyEvent.VK_J;
            case 'k': return KeyEvent.VK_K;
            case 'l': return KeyEvent.VK_L;
            case 'm': return KeyEvent.VK_M;
            case 'n': return KeyEvent.VK_N;
            case 'o': return KeyEvent.VK_O;
            case 'p': return KeyEvent.VK_P;
            case 'q': return KeyEvent.VK_Q;
            case 'r': return KeyEvent.VK_R;
            case 's': return KeyEvent.VK_S;
            case 't': return KeyEvent.VK_T;
            case 'u': return KeyEvent.VK_U;
            case 'v': return KeyEvent.VK_V;
            case 'w': return KeyEvent.VK_W;
            case 'x': return KeyEvent.VK_X;
            case 'y': return KeyEvent.VK_Y;
            default: return KeyEvent.VK_Z;   
        }   
    }    
}
