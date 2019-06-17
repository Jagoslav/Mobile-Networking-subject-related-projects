/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulacja.samochodu;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import symulacja.samochodu.SymulacjaSamochodu.AppState;

/**
 *
 * @author Jakub
 */
public class Parametry extends JFrame implements ChangeListener,ActionListener{
        SymulacjaSamochodu projekt;
        JSlider sldRozmiar=new JSlider(JSlider.HORIZONTAL,10,30,20);
        int rozmiar=sldRozmiar.getValue();
        JSlider sldBlokady=new JSlider(JSlider.HORIZONTAL,10,50,30);
        int blokady=sldBlokady.getValue();
        JLabel txtRozmiar=new JLabel();
        JLabel txtBlokady=new JLabel();
        JButton btnOK=new JButton("kontynuuj");
        JButton btnRandom=new JButton("losuj wartości");
            
    public Parametry(SymulacjaSamochodu projekt){
        this.projekt=projekt;
        setLayout(new BorderLayout());
        setSize(350,150);
        setLocationRelativeTo(null);
        setTitle("Opcje");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel top=new JPanel();
        JPanel mid=new JPanel();
        JPanel bot=new JPanel();
        sldRozmiar.setMajorTickSpacing(5);
        sldRozmiar.setMinorTickSpacing(1);
        sldRozmiar.setPaintTicks(true);
        sldRozmiar.addChangeListener(this);
        sldBlokady.setMajorTickSpacing(5);
        sldBlokady.setMinorTickSpacing(1);
        sldBlokady.setPaintTicks(true);
        sldBlokady.addChangeListener(this);
        top.add(sldRozmiar);
        top.add(txtRozmiar);
        txtRozmiar.setText("rozmiar: "+rozmiar);
        mid.add(sldBlokady);
        mid.add(txtBlokady);
        txtBlokady.setText("blokady: "+blokady);
        btnRandom.addActionListener(this);
        btnOK.addActionListener(this);
        bot.add(btnRandom);
        bot.add(btnOK);
        add("North",top);
        add("Center",mid);
        add("South",bot);
        setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) { //suwaki i okna tekstowe
            if(e.getSource()==sldRozmiar){
                rozmiar=sldRozmiar.getValue();
                txtRozmiar.setText("rozmiar: "+rozmiar);
            }
            else if(e.getSource()==sldBlokady){
                blokady=sldBlokady.getValue();
                txtBlokady.setText("blokady: "+blokady);
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) { //przyciski
        if(e.getSource()==btnRandom){
            Random r=new Random();
            rozmiar=r.nextInt(21)+10;
            sldRozmiar.setValue(rozmiar);
            blokady=r.nextInt(41)+10;
            sldBlokady.setValue(blokady);
        }
        else if(e.getSource()==btnOK){
            rozmiar=sldRozmiar.getValue();
            blokady=sldBlokady.getValue();
            projekt.rozmiar=rozmiar;
            projekt.blokady=blokady;
            projekt.state=AppState.punkty;
            dispose();
        }
    }
}
