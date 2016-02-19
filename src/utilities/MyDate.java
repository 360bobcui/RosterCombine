package utilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
import javax.swing.JOptionPane;
import utilities.GlobalVar;
/**
 *
 * @author SPC Cui, Feb 15, 2015
 */
// the reaseon to extends from GregorianCalendar is this class still has constructor
// GregorianCalendar(int year, int month, int date)
public final class MyDate extends GregorianCalendar{

    private Calendar day;    
    private int thisYear;
    private int thisMonth;
    private int thisDate;
    
    // constructor for today's date
    public MyDate() {  //set today's date 
        // use computer time as the sign in time
        this(null);
    }
    
    public MyDate(String text){     
        if (text != null){           
            // is not 6.
            if (text.length() != GlobalVar.DATE_LEN || text.contains("/")) {            
                if (!text.contains("/")) //not m/d/yyyy format
                    throw new IllegalArgumentException("Date is invalid!");
                else {
                    text = text.trim();
                    String[] dates = text.split("/");
                    String firstPart = dates[0];
                    if (firstPart.length() <= 2) {   //mm/dd/yyyy or mm/dd/yy format
                        String date = sanitizeDate(dates[1]);
                        String month = sanitizeMonth(dates[0]);
                        String year = sanitizeYear(dates[2]);
                        text = year + month + date;
                        System.out.println("MyDate.java: from / Date is: " + text);
                    } else { // month.length() != 4  //yyyy/mm/dd format         
                        String date = sanitizeDate(dates[2]);
                        String month = sanitizeMonth(dates[1]);
                        String year = sanitizeYear(dates[0]);
                        text = year + month + date;
                        System.out.println("MyDate.java: from / Date is: " + text);
                    }
                }
            }
            String num = text.replaceAll("[^\\d]","");

            // throw illegal argeument exception if non-digits characters passed in
            if (num.length() != GlobalVar.DATE_LEN) {        
                throw new IllegalArgumentException("Date should only contain digits 0-9!");
            }

            int myYear = Integer.parseInt(text.substring(0,2)) + GlobalVar.YEAR_BASE; //
            int myMonth = Integer.parseInt(text.substring(2,4)) - 1;  // month range 0 - 11
            int myDate = Integer.parseInt(text.substring(4,GlobalVar.DATE_LEN));  // day range 1 - 31       

           // day = new GregorianCalendar(myYear, myMonth, myDate); // set date to 00:00:00 of the date   
            day = new GregorianCalendar(); 
            day.set(myYear, myMonth, myDate); //set the current time for the date

            if (myMonth < 0 || myMonth > 11) {  //month is invalid
                day = null;
            } else if (!isDateValid(myMonth, myDate, isLeapYear(myYear))){ //date is invalid
                day = null;   
            }
        } else {
           day = new GregorianCalendar(); 
           //JOptionPane.showMessageDialog(null, "Date might be incorrect.");
        }
        getString();  // update thisYear, thisMonth, thisDate
    }
    
    public Calendar getDay() {
        //System.out.println(day.toString());
        return day;
    }
    
    // given month, return true if the date for the month is valid
    private Boolean isDateValid(int month, int date, Boolean isLeap) {
        if (month != 1) {
                switch (month) {
                    case 0:
                    case 2:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 11: return date > 0 && date <= 31;
                    case 1:
                    case 3:
                    case 5:
                    case 8:                
                    case 10:
                    case 12: return date > 0 && date <= 30;
                    default: return false;  //invalid month
                }
            
        } else {
            if (isLeap) {
                return date > 0 && date <= 29;
            } else {
                return date > 0 && date <= 28;          
            }     
        }
    }    
    
    // compute the days difference between this date and the given date
    // always return a positive number
    public int getDaysDiff(MyDate D) {
        long date1 = day.getTimeInMillis();
        long date2 = D.day.getTimeInMillis();
        
        double days = Math.abs((date1 - date2)) / GlobalVar.ONE_DAY_IN_MILLISECOND + 1;  //inclusive             
        days = Math.round(days);
        return (int)days;
    }
 
    // compute the days difference between this date and the given date
    // always return a positive number
    public String getDaysDifftoString(MyDate D) {
        String str = "" + getDaysDiff(D);
        // pad the result with 0;
        for (int i = str.length(); i < GlobalVar.ORDER_LEAVE_DAYS_IN_DMO; i++ ) {
            str = "0" + str;
        }
        return str;
    }
    
    // return true if this date is after the given date
    // return false otherwise.
    public Boolean after(MyDate D) {
        long date1 = day.getTimeInMillis();  //day we overwrite in Calendar obj
        long date2 = D.day.getTimeInMillis();
//        System.out.println(date1);
//        System.out.println(date2);
     // Because we set date to 00:00:00 of the date, greater than at least 24 hours
     // return date1 > (date2 + GlobalVar.ONE_DAY_IN_MILLISECOND);
       return date1 > date2 + GlobalVar.SOME_SEC_IN_MILLISECOND; //
    }
    
    public Boolean afterOrEqual(MyDate D) {
        long date1 = day.getTimeInMillis();  //day we overwrite in Calendar obj
        long date2 = D.day.getTimeInMillis();
        
        // equal case
        if (Math.abs(date1 - date2) < GlobalVar.SOME_SEC_IN_MILLISECOND) {
            return true;
        }         
        //not equal case
        return date1 > date2;
    }
    
    public Boolean beforeOrEqual(MyDate D) {
        long date1 = day.getTimeInMillis();  //day we overwrite in Calendar obj
        long date2 = D.day.getTimeInMillis();
        
        // equal case
        if (Math.abs(date1 - date2) < GlobalVar.SOME_SEC_IN_MILLISECOND) {
            return true;
        }         
        //not equal case
        return date1 < date2;
    }
    
    // return yymmdd format for today
     public String getString() {        
        Map<String,String> monthMap = new TreeMap<String, String>();
        monthMap.put("Jan", "01");
        monthMap.put("Feb", "02");
        monthMap.put("Mar", "03");
        monthMap.put("Apr", "04");
        monthMap.put("May", "05");
        monthMap.put("Jun", "06");
        monthMap.put("Jul", "07");
        monthMap.put("Aug", "08");
        monthMap.put("Sep", "09");
        monthMap.put("Oct", "10");
        monthMap.put("Nov", "11");
        monthMap.put("Dec", "12");                
       // Calendar today = new GregorianCalendar();
        
        String dayTime = day.getTime().toString(); //Sun Mar 01 07:11:38 PST 2015
        String[] dayTimeArray = dayTime.split(" ");
        
        // Mon Feb 23 20:40:30 PST 2015
        //String weekDay = dayTimeArray[0];
        
        String month = dayTimeArray[1];
        month =  monthMap.get(month);
        thisMonth = Integer.parseInt(month);
        
        String date = dayTimeArray[2];       
        thisDate = Integer.parseInt(date);
        //String time = dayTimeArray[3];
        //String timeZone = dayTimeArray[4];
        
        String year = dayTimeArray[5];
        if (year.length() == 4) {
            year = year.substring(2,4);
            thisYear = Integer.parseInt(year);
        }
        
        //Mon Feb 23 20:40:30 PST 2015
        //151231 Mon,20:40:30,PST
        
        return year + month + date; 
    }
     
     public int getMyYear(){
         return thisYear;
     }
     
     public int getMyMonth() {
         return thisMonth;
     }
     
     public int getMyDate() {
         return thisDate;
     }
     
     // 
     public int getMonthDiff(MyDate oldDate) {
         int years = thisYear - oldDate.thisYear;
         int months = thisMonth - oldDate.thisMonth;
         return years * 12 + months;
     }
     
     //given the date, return YYMM of one month prior
     public String getOneMonPrior2String() {
         int year = thisYear;  //two digits
         int month = thisMonth;   //two digits
         
         if (month == 1) { //January
             year = year - 1;  //one month prior
             month = 12;  //move to December last year
         } else {
             month--;
         }     
         if (year < 0) {//  prevent year of 2000 becomes  -1
             year = 99;
         }
         String yearS = null;
         String monthS = null;

         if(year < 10) {
             yearS = "0" + year;
         } else {
             yearS = "" + year;
         }

         if(month < 10) {
             monthS = "0" + month;
         } else {
             monthS = "" + month;
         }
         return yearS + monthS;
     } 
     
     // given one char long date, return two char long date
     public String sanitizeDate(String date) {
         if (date != null && date.length() <= 2) {
             if (date.length() == 1){
                 return "0" + date;
             } else {
                 return date;
             }
         } else {
             throw new IllegalArgumentException("MyDate.java: Invalid Date format: date: " + date);
         }
     }
     
   // given one char long month, return two char long month
     public String sanitizeMonth(String month) {
         if (month != null && month.length() <= 2) {
             if (month.length() == 1){
                 return "0" + month;
             } else {
                 return month;
             }
         } else {
             throw new IllegalArgumentException("MyDate.java: Invalid Date format: month: " + month);
         }
     }
     
          
   // given four char long year, return two char long month
     public String sanitizeYear(String year) {
         if (year != null && year.length() == 4) {
             return year.substring(2); // 2015 - > 15
         } else if (year != null && year.length() == 2) {
             return year;
             
         } else {
             throw new IllegalArgumentException("MyDate.java: Invalid Date format, year: " + year);
         }
     }
}
