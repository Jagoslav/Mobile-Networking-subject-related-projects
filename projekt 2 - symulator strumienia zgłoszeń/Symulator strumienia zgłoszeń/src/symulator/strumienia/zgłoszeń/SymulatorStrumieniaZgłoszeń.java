/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulator.strumienia.zgłoszeń;

import java.io.PrintWriter;
import static java.lang.Math.*;
import java.util.Random;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 *
 * @author Jakub
 */
public class SymulatorStrumieniaZgłoszeń {
    PrintWriter logi;
    Stack<Double> zgloszenia=new Stack<Double>();
    double timeLimit=8.0;
    double currTime=0.0;
    double lambda=0.33; // 0.33, 2, 5, 8
    /**
     * @param args the command line arguments
     */
    
    public SymulatorStrumieniaZgłoszeń(){
        try{//otwiernie pliku do zapisu logów
            logi=new PrintWriter("logi.txt","UTF-8");
            logi.println("#zgloszenia");
        }catch(Exception e){}
        
        try {//decyzja o wartości parametru lambda
            Object opcje[]={0.33,2.0,5.0,8.0};
            
            lambda = (Double)JOptionPane.showInputDialog(null, "Podaj wartość lambda",
                    "", JOptionPane.PLAIN_MESSAGE, null, opcje, lambda);
        } catch(NullPointerException event) {
            System.exit(0);
        }
        if(lambda==0.33)
            lambda=1.0/3.0;
        String rozklad="";
        try {//decyzja o używanym rozkładzie
            Object opcje[]={"Rozkład Poissona","rozkład deterministyczny"};
            
            rozklad= (String)JOptionPane.showInputDialog(null, "Wybierz stosowany rozkład",
                    "", JOptionPane.PLAIN_MESSAGE, null, opcje, rozklad);
        } catch(NullPointerException event) {
            System.exit(0);
        }
        if(rozklad=="Rozkład Poissona"){
            while(currTime<timeLimit){
                currTime+= (-1/lambda)*log(new Random().nextDouble());
                if(currTime<timeLimit){
                    System.out.println(currTime);
                    zgloszenia.add(currTime);
                    logi.println(currTime);
                }
            }
        }
        else if(rozklad=="rozkład deterministyczny"){
            while(currTime<timeLimit){
                currTime+=1/lambda;
                if(currTime<timeLimit){
                    System.out.println(currTime);
                    zgloszenia.add(currTime);
                    logi.println(currTime);
                }
            }
        }
        else System.exit(0);
        logi.close();
        Symulator symulator=new Symulator(zgloszenia,rozklad+" z parametrem "+lambda);
    }
    public static void main(String[] args) {
        SymulatorStrumieniaZgłoszeń symulator=new SymulatorStrumieniaZgłoszeń();
        
    }

}
