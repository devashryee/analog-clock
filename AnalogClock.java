package graphicsinclass;

/**
 *
 * @author dolly
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.net.ssl.*;
import javax.swing.*;


public class AnalogClock {
    static int sec;
    static int min;
    static int hr;
    public static void main(String[] args) {
        JFrame jf = new JFrame("Analog Clock");
        jf.setSize(400,400);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ClockPanel cp = new ClockPanel();
        jf.add(cp);
        getCurrTime();
        new Thread(){
            public void run(){
                while (true){
                    if(sec%60==0) {
                        getCurrTime();
                    }
                    sec+=1;
                    cp.repaint();
                    try { sleep(1000); } 
                    catch (InterruptedException ex) { }          
                }
            }
        }.start();
        jf.setVisible(true);
    }
    
    private static void getCurrTime() {
        long t=0;
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket("nist.time.gov", 443);
            socket.startHandshake();
            PrintStream sout = new PrintStream(socket.getOutputStream());
            Scanner sin = new Scanner(socket.getInputStream());
            sout.print("GET /actualtime.cgi HTTP/1.0\r\nHOST: nist.time.gov\r\n\r\n");
            String timeString="";
            while (sin.hasNext()) {
                timeString=sin.nextLine();
                if (timeString.equalsIgnoreCase("stop")) { continue; } 
            }
            int start=timeString.indexOf("\"");
            int end = timeString.indexOf("\"", start + 1);
            timeString=timeString.substring(start+1,end);
            t=Long.parseLong(timeString);
        }
        catch (Exception e) { System.out.println("OOpsie: " + e.toString()); }
        finally {
            t/=1000;
            Date curr = new Date(t);
            SimpleDateFormat hr1 = new SimpleDateFormat("HH");
            SimpleDateFormat min1 = new SimpleDateFormat("mm");
            SimpleDateFormat sec1 = new SimpleDateFormat("ss");
            hr = Integer.parseInt(hr1.format(curr));
            min = Integer.parseInt(min1.format(curr));
            sec = Integer.parseInt(sec1.format(curr));
        }
    }
    
}
class ClockPanel extends JPanel{
    int r;
    ClockPanel(){
        super();
        AnalogClock.sec=0;
    }
    Point calcSecLocation(){
        Point p = new Point();
        p.x = (int)(Math.sin(Math.toRadians(AnalogClock.sec)*6)*r);
        p.y = (int)(Math.cos(Math.toRadians(AnalogClock.sec)*6)*r);
        return p;
    }
    Point calcMinLocation(){
        Point p = new Point();
        p.x = (int)(Math.sin(Math.toRadians(AnalogClock.min*6+AnalogClock.sec/10))*r);
        p.y = (int)(Math.cos(Math.toRadians(AnalogClock.min*6+AnalogClock.sec/10))*r);
        return p;
    }
    
    Point calcHrLocation(){
        Point p = new Point();
        p.x = (int)(Math.sin(Math.toRadians((AnalogClock.hr%12) * 30+AnalogClock.min/2)) * r);
        p.y = (int)(Math.cos(Math.toRadians((AnalogClock.hr%12) * 30+AnalogClock.min/2)) * r);
        return p;
    }
    
    protected void paintComponent(Graphics g){
        int height = this.getSize().height;
        int width = this.getSize().width;
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, height);
        r = height/2;
        int centerX = width/2;
        int centerY = height/2;
        int startX = width/2;
        int startY = 0;
        g.setColor(Color.CYAN);
        g.fillOval(centerX-r, centerY-r, r*2, r*2);
        Point p = calcSecLocation();
        Point p2=calcMinLocation();
        Point p3=calcHrLocation();
        g.setColor(Color.BLUE);
        g.drawLine(centerX, centerY, centerX + p.x, centerY - p.y);
        g.setColor(Color.PINK);
        g.drawLine(centerX, centerY, centerX + p2.x, centerY - p2.y);
        g.setColor(Color.RED);
        g.drawLine(centerX, centerY, centerX + p3.x, centerY - p3.y);        
    }
}

class Point{
    int x;
    int y;
    Point(int newx, int newy){x = newx; y = newy;}
    Point(){this(0,0);}
}