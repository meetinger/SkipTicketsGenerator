package code;


import java.awt.*;

public class Ticket {

    private String index, school, fio;
    private Image QRCode, stamp, teacher;

    public Ticket(String index, String school, String fio, String QRCode, Image teacher){
        this.index = index;
        this.school = school;
        this.fio = fio;
        //QRCODE
        this.teacher = teacher;
    }
}