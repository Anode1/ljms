package org.is.gui;

import java.util.Calendar;
import java.sql.Date;
import javax.swing.*;

/**
 * ITextField for displaying a date
 *
 * @since jdk1.2
 */
public class IDateTextField extends ITextField{

    public IDateTextField(){

        setEditable(false);
    }

    public void setCurrentTime(){

        Date date=new Date(Calendar.getInstance().getTime().getTime());
        setText(date.toString());
    }

    public void setTime(java.sql.Date time){

       if(time!=null)
        setText(time.toString());
    }

    public void setTime(String time){

       if(time!=null)
        setText(time);
    }


    public boolean userInputIsValid(){

        return true;
    }

    public void sendData(String string){

       setText(string);
    }


    public String getData(){

       if(getText()==null)return null;
       return getText();
    }

}