/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulacja.samochodu;


import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Jakub
 */
public class Punkty extends JFrame implements ActionListener{
    SymulacjaSamochodu projekt;
    int rozmiar;
    int blokady;
    JComboBox startX;
    JComboBox startY;
    JComboBox koniecX;
    JComboBox koniecY;
    JButton btnRandom=new JButton("losuj współrzędne");
    JButton btnOK=new JButton("Kontynuuj");
    public Punkty(SymulacjaSamochodu projekt,int rozmiar, int blokady){
        setLayout(new BorderLayout());
        setSize(250,150);
        setLocationRelativeTo(null);
        setTitle("Opcje");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.projekt=projekt;
        this.rozmiar=rozmiar;
        this.blokady=blokady;
        String[] vals=new String[rozmiar];
        for(int i=0;i<rozmiar;i++)
            vals[i]=""+i;
        
        startX=new JComboBox(vals);
        startY=new JComboBox(vals);
        koniecX=new JComboBox(vals);
        koniecY=new JComboBox(vals);
        JPanel upperMid=new JPanel();
        JPanel lowerMid=new JPanel();
        upperMid.add("West",new JLabel("Sx:"));
        upperMid.add("West",startX);
        upperMid.add("West",new JLabel("Sy:"));
        upperMid.add("West",startY);
        lowerMid.add("East",new JLabel("Kx:"));
        lowerMid.add("East",koniecX);
        lowerMid.add("East",new JLabel("Ky:"));
        lowerMid.add("East",koniecY);
        JPanel mid=new JPanel(new BorderLayout());
        mid.add("North",upperMid);
        mid.add("South",lowerMid);
        btnOK.addActionListener(this);
        btnRandom.addActionListener(this);
        add("North",btnRandom);
        add("Center",mid);
        add("South",btnOK);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==btnRandom){
            Random r=new Random();
            do{
                startX.setSelectedIndex(r.nextInt(rozmiar));
                startY.setSelectedIndex(r.nextInt(rozmiar));
                koniecX.setSelectedIndex(r.nextInt(rozmiar));
                koniecY.setSelectedIndex(r.nextInt(rozmiar));
            }while(
                    startX.getSelectedIndex()==koniecX.getSelectedIndex() 
                    && startY.getSelectedIndex()==koniecY.getSelectedIndex());
        }
        else if(e.getSource()==btnOK){
            if(startX.getSelectedIndex()!=koniecX.getSelectedIndex() 
            || startY.getSelectedIndex()!=koniecY.getSelectedIndex()){
                this.dispose();
                projekt.pPocz=new Point(startX.getSelectedIndex(),startY.getSelectedIndex());
                projekt.pKonc=new Point(koniecX.getSelectedIndex(),koniecY.getSelectedIndex());
                projekt.state=SymulacjaSamochodu.AppState.symulator;
            }
        }
    }
}
