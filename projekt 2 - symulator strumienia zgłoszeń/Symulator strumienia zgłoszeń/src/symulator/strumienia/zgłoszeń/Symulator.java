/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulator.strumienia.zgłoszeń;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Stack;
import javax.swing.JFrame;

/**
 *
 * @author Jakub
 */
public class Symulator extends JFrame{
    int height;
    int width;
    boolean running;
    public Stack<Double> zgloszenia;
    public int[] iloscZgloszen=new int[8];
    public Symulator(Stack<Double> zgloszenia, String title){
        this.zgloszenia=zgloszenia;
        this.width=900;
        this.height=600;
        setTitle(title);
        for(int i=0;i<8;i++)
            iloscZgloszen[i]=0;
        for(double d: zgloszenia)
            iloscZgloszen[(int)(d/1)]++;
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        run();
    }
    public void run(){
        running=true;
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60.0;
        double delta = 0;
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                delta--;
            }
            if (running) {
                render();
            }
        }
        System.exit(0);
        //
    }
    public void render(){
        
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        //tło
        g.setColor(Color.BLACK);
        g.fillRect(0,0,width,height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("arial",32,32));
        
        //górny wykres
        g.fillRect(40,50,10,370);
        g.fillRect(40,50,40,10);
        g.fillRect(width-50,50,10,370);
        g.fillRect(width-80,50,30,10);
        g.fillRect(50,410,800,10);
        for(int i=1;i<12;i++){
            g.fillRect(45,50+i*30,20,10);
            g.fillRect(width-65,50+i*30,20,10);
        }
        //dolny wykres
        g.fillRect(45,height-90,10,40);
        g.fillRect(width-55,height-90,10,40);
        g.fillRect(50,height-60,800,10);
        for(int i=1;i<8;i++){
            g.fillRect(45+i*100,400,10,20);
            g.fillRect(45+i*100,height-70,10,10);
        }
        //wartości na osi
        g.setFont(new Font("Arial",12,12));
        
        for(int i=0;i<13;i++)
            g.drawString(i+"",25,420-i*30);
        for(int i=0;i<9;i++){
            g.drawString(i+"",45+i*100,430);
            g.drawString(i+"",45+i*100,height-30);
        }
        g.drawString("ilość zgłoszeń w czasie", 45, 450);
        g.drawString("momenty wystąpienia zgłoszeń", 45, 500);
        g.setColor(Color.RED);
        for(double d: zgloszenia){
            g.fillOval(45+(int)(d*100),height-90,10,10);
        }
        for(int i=0;i<8;i++)
            g.fillRect(50+100*i,410-iloscZgloszen[i]*30,100,10);
        
        g.dispose();
        bs.show();
    }
}
