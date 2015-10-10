/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author SPC Cui
 * Check DJMS and generate a list of NPD Service members
 */
public class NPDListFinder {

    public static List<String> NPDlist;
    
    
    
    public NPDListFinder(List<String> ssnlist) throws UnsupportedFlavorException, IOException, AWTException {
        NPDlist = new ArrayList<>();
       // ssnlist = new ArrayList<>();
        //String fileName = args[0]; // file name in txt format
//        if (args.length > 1){
//            SPEED = Integer.parseInt(args[1]);
//            SPEED1 = Integer.parseInt(args[2]);
//        }
        //List<String> list = readSSN(fileName);
        List<String> list = ssnlist;
        //List<String> NPDlist = new ArrayList<String>();        
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();      
        Robot rob = new Robot();
        JOptionPane.showMessageDialog(null, "Are you ready to have DJMS come to the front?");          
        rob.delay(GlobalVar.SPEED_DJMS);//wait for the user to focus on DJMS
    
        String result = null;
        for (String ssn : list) {
            System.out.println("Now processing: " + ssn);

            keyInSSNandPu(ssn, rob);
            copyDJMSContent(rob);
            Transferable contents = cb.getContents(null);
            result = (String)contents.getTransferData(DataFlavor.stringFlavor); 
            if (!isPD(result)) {
                NPDlist.add(ssn.trim());
            }
        }        
        //System.out.println(NPDlist);    
    }
    
//    public List<String> getSSNList() {
//        return ssnlist;
//    }
    
    public List<String> getNPDList() {
        return NPDlist;
    }
    
    private void moveCurser2Front(Robot rob){
       rob.keyPress(KeyEvent.VK_F8);  //move the curser to the SSN field
       rob.keyRelease(KeyEvent.VK_F8);    
    }
    
    //given the DJMS content and determine if the soldier has pay due
    private boolean isPD(String result) {
        result = result.trim();
        //result = result.replace("[ |\n]+", " ");
        result = result.replace("PA-AMT-MM", "@@@");
        result = result.replace("PA-AMT-EOM", "@@@");
        result = result.replaceAll("COMPTR-PAY-COND", "@@@");
        //result = result.replace("COMPTR-PAY-COND", "@@@");
        
        System.out.println(result);  
        String[] strings = result.split("@@@");
        if (strings.length > 3){
            String paAMTMM = strings[1].replace(",","").trim();
            String paAMTEOM = strings[3].replace(",","").trim();            
            return paAMTMM.compareTo("0.00") > 0 || paAMTEOM.compareTo("0.00") > 0;       
        } else {
            return false;
        }
    }
    
    // Alt + e + y to copy screen content
    private void copyDJMSContent(Robot rob) {
         
        rob.delay(GlobalVar.SPEED2);
        rob.keyPress(KeyEvent.VK_ALT); 
        rob.keyRelease(KeyEvent.VK_ALT); 
         rob.delay(GlobalVar.SPEED2);
        rob.keyPress(KeyEvent.VK_E);
        rob.keyRelease(KeyEvent.VK_E); 
         rob.delay(GlobalVar.SPEED2);
        rob.keyPress(KeyEvent.VK_Y);
        rob.keyRelease(KeyEvent.VK_Y);
         rob.delay(GlobalVar.SPEED2);
    }
    
    // make sure 
    // check SSN and Pu line in DJMS
    private void keyInSSNandPu(String SSN, Robot r) {

        for (int i = 0; i < 9; i++) {
            char digit = SSN.charAt(i);
            //System.out.println(digit);
            r.keyPress(digitKeyEventValue(digit));
            r.keyRelease(digitKeyEventValue(digit)); 
            r.delay(GlobalVar.SPEED1);
        }
//        r.keyPress(letterKeyEventValue('p'));
//        r.keyRelease(letterKeyEventValue('p'));
//        r.keyPress(letterKeyEventValue('u'));
//        r.keyRelease(letterKeyEventValue('u'));        
        r.keyPress(KeyEvent.VK_F8);
        r.keyPress(KeyEvent.VK_F8);
        r.delay(GlobalVar.SPEED2);//wait for the user to focus on DJMS
    }
    
    private List<String> readSSN(String fileName) throws FileNotFoundException{
        Scanner input = new Scanner(new File(fileName));
        List<String> list = new ArrayList<String>();
        while(input.hasNext()) {   
            String ssn = validateSSN(input.nextLine());
            
            list.add(ssn);
        }       
        return list;
    }
    
    private String validateSSN(String ssn){
        while(ssn.length() < GlobalVar.SSN_LEN){
            ssn = "0" + ssn;
        }
        return ssn;
    }
    
    private char letterKeyEventValue(char letter){
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
            //default: return 0;
        }   
    }
    
    private char digitKeyEventValue(char digit){
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
}
