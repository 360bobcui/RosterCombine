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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author SPC Cui
 */
public class PrintLES {
   // private static final int SSN_LEN = 9; 
    private static Robot ROBOT;
    private static Clipboard CLIP_BOARD;
    private static List<String> LESnotAvailableSSNList;
    public PrintLES(List<String> ssnList) throws AWTException, UnsupportedFlavorException, IOException{
        ROBOT = new Robot();
        CLIP_BOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
        int speed1 = GlobalVar.SPEED1;
        int speed2 = GlobalVar.SPEED2;
        // read the given list of SSN  
        //List<String> ssnList = readSSN(args[0]);    
        JOptionPane.showMessageDialog(null, "Make sure MULT JLES come to the front!");
        ROBOT.delay(GlobalVar.SPEED_DJMS);
        // Print out LES and generate a list of unprocessed SSN
        LESnotAvailableSSNList = processLES(ssnList);
//        PrintStream output = new PrintStream(new File(GlobalVar.UNPROCESSED_SSN_FILE_NAME));
//        for (String ssn : LESnotAvailableSSNList) {
//            output.println(ssn);
//        }
    }
    
    // return a list of SSN not processed (LES is not printed)
    public static List<String> getLESnotProcdSSNList() {
        return LESnotAvailableSSNList;
    }
    
    // return a list of ssn not printed
    private static List<String> processLES(List<String> ssnList) throws UnsupportedFlavorException, IOException {
        int count = 0;
        List<String> list = new ArrayList<String>();
        for (String ssn : ssnList) {
            GlobalVar.keyInSSNLES(ssn, ROBOT);
            count++;
            if(count == GlobalVar.NUM_SSN_PER_PAGE) {  //ready to print
                F9print();
                String res = copyDJMSContent();
                List<String> thisPage = getNotProcessedSSN(res);   // check not processed SSNs
                list.addAll(thisPage);
                count = 0;
                JOptionPane.showMessageDialog(null, "Continue to print the next 24?");
                ROBOT.delay(GlobalVar.SPEED_DJMS);
            }
        }
        // print out the left over of the SSN not processed
        F9print();
        String res = copyDJMSContent();
        List<String> thisPage = getNotProcessedSSN(res);   // check not processed SSNs
        list.addAll(thisPage);
        return list;
    }
    
    private static void F9print() {
        ROBOT.keyPress(KeyEvent.VK_F9);
        ROBOT.keyRelease(KeyEvent.VK_F9);
        ROBOT.delay(GlobalVar.SPEED_DJMS);
    }

    
     // Alt + e + y to copy screen content
    public static String copyDJMSContent() throws UnsupportedFlavorException, IOException {         
        ROBOT.delay(GlobalVar.SPEED2);//wait for the user to focus on DJMS
        ROBOT.keyPress(KeyEvent.VK_ALT); 
        ROBOT.keyRelease(KeyEvent.VK_ALT); 
        ROBOT.delay(GlobalVar.SPEED2);//wait for the user to focus on DJMS
        ROBOT.keyPress(KeyEvent.VK_E);
        ROBOT.keyRelease(KeyEvent.VK_E); 
        ROBOT.delay(GlobalVar.SPEED2);//wait for the user to focus on DJMS
        ROBOT.keyPress(KeyEvent.VK_Y);
        ROBOT.keyRelease(KeyEvent.VK_Y);
        ROBOT.delay(GlobalVar.SPEED2);//wait for the user to focus on DJMS
        Transferable contents = CLIP_BOARD.getContents(null);
        return (String)contents.getTransferData(DataFlavor.stringFlavor); 
    } 

    private static List<String> getNotProcessedSSN(String res) {
        List<String> list = new ArrayList<>();
        res = res.replaceAll("[^0-9 ]+", ""); //non digits and space 
        //System.out.println(res.trim());
        //res = res.trim();
        String[] SSNs = res.split(" ");
        System.out.println(res);
        System.out.println(Arrays.toString(SSNs));
        for (String SSN : SSNs) {
            if(SSN.length() == GlobalVar.SSN_LEN) { // prevent any double space
                list.add(SSN);
            }
        }
        System.out.println(list);
        return list;        
                
    }
}
