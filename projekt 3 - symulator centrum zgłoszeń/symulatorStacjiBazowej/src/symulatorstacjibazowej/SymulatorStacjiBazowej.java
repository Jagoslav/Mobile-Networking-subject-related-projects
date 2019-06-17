/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulatorstacjibazowej;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Jakub
 */
public class SymulatorStacjiBazowej{
    Boolean poissonFrequencyType;   //rozkład poisson(true) lub deterministyczny(false)
    Double lambda;  //parametr rozkładu
    Double maxTime; //czas trwania
    Double signalProcesingTime;    //czas przerabiania zgłoszenia
    int maxQueLength;  //rozmiar kolejki zgłoszeń oczekujących
    int queLength;  //kolejka
    int numberOfChannels;   //ilość kanałów obsługujących zgłoszenia
    Boolean[] channel;  //stan zajęcia kanałów
    Double[] channelFinishTime; //czas w którym kanał zostanie zwolniony
    
    PrintWriter przychodząceZgloszenia,rozmiarKolejki,obslugaZgloszen;
    public SymulatorStacjiBazowej(){
        
        String rozklad="";
        try {//decyzja o używanym rozkładzie
            Object opcje[]={"Rozkład Poissona","rozkład deterministyczny"};
            
            rozklad= (String)JOptionPane.showInputDialog(null, "Wybierz stosowany rozkład",
                    "", JOptionPane.PLAIN_MESSAGE, null, opcje, rozklad);
        } catch(NullPointerException event) {
            System.exit(0);
        }
        if(rozklad==null)
            return;
        poissonFrequencyType=(rozklad=="Rozkład Poissona")?true:false;
        try {//decyzja o wartości parametru lambda
            Object opcje[]={0.33,2.0,5.0,8.0};
            
            lambda = (Double)JOptionPane.showInputDialog(null, "Podaj wartość lambda",
                    "", JOptionPane.PLAIN_MESSAGE, null, opcje, lambda);
        } catch(NullPointerException event) {
            System.exit(0);
        }
        if(lambda==null)
            return;
        try{//decyzja o długości czasu trwania programu
            Object opcje[]={1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0,5.5,6.0,6.5,7.0,7.5,8.0};
            maxTime=(Double) JOptionPane.showInputDialog(null,"Wybierz czas nasłuchiwania bazy",
                    "",JOptionPane.PLAIN_MESSAGE,null,opcje,maxTime);
        }catch(NullPointerException event) {
            System.exit(0);
        }
        if(maxTime==null)
            return;
        maxTime*=1000;
        try{//decyzja o długości czasu poświęcanego zgłoszeniom
            Object opcje[]={0.15,0.20,0.33,0.40,0.50};
            signalProcesingTime=(Double) JOptionPane.showInputDialog(null,"Wybierz czas przerabiania zgłoszenia",
                    "",JOptionPane.PLAIN_MESSAGE,null,opcje,signalProcesingTime);
        }catch(NullPointerException event) {
            System.exit(0);
        }
        if(signalProcesingTime==null)
            return;
        signalProcesingTime*=1000;
        try{//decyzja o rozmiarze kolejki zgłoszeń
            Object opcje[]={1,2,3,4,5,6};
            maxQueLength=-1;
            maxQueLength=(int) JOptionPane.showInputDialog(null,"Wybierz rozmiar kolejki oczekujących zgłoszeń",
                    "",JOptionPane.PLAIN_MESSAGE,null,opcje,maxQueLength);
            if(maxQueLength==-1)
                return;
        }catch(NullPointerException event) {
            System.exit(0);
        }
        try{//decyzja o ilośći kanałów
            Object opcje[]={1,2,3,4};
            numberOfChannels=-1;
            numberOfChannels=(int) JOptionPane.showInputDialog(null,"Wybierz ilość kanałów do obsługi zgłoszeń",
                    "",JOptionPane.PLAIN_MESSAGE,null,opcje,numberOfChannels);
            if(numberOfChannels==-1)
                    return;
        }catch(NullPointerException event) {
            System.exit(0);
        }
        channel=new Boolean[numberOfChannels];
        channelFinishTime=new Double[numberOfChannels];
        try {
            przychodząceZgloszenia=new PrintWriter("zgłoszeniaPrzychodzące.txt");
            przychodząceZgloszenia.println("#momentyZgłoszeń");
            rozmiarKolejki=new PrintWriter("kolejka.txt");
            rozmiarKolejki.println("#długość kolejki");
            obslugaZgloszen=new PrintWriter("obsługaZgłoszeń.txt");
            obslugaZgloszen.println("#zdarzenia");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SymulatorStacjiBazowej.class.getName()).log(Level.SEVERE, null, ex);
        }
        start();
        przychodząceZgloszenia.close();
        rozmiarKolejki.close();
        obslugaZgloszen.close();
    }
    public void start(){
        double currentTime=0;
        queLength=0;
        for(int i=0;i<numberOfChannels;i++){
            channel[i]=false;
            channelFinishTime[i]=currentTime;
        }
        double nextSignal=0;
        nextSignal+=(poissonFrequencyType)?1000*((-1/lambda)*log(new Random().nextDouble())):1000*(1/lambda); //kolejne zgłoszenie
        rozmiarKolejki.println(0+"\t"+0);
        while(currentTime<maxTime){
            double displayTime=currentTime/1000;
            rozmiarKolejki.println(displayTime+"\t"+queLength);
            if(nextSignal<=currentTime){    //jeśli nadeszło nowe zgłoszenie
            rozmiarKolejki.println(displayTime+"\t"+queLength);
                przychodząceZgloszenia.println(displayTime+"\t"+"0");
                if(queLength<maxQueLength){  //to próbujemy wrzucić je do kolejki
                    queLength++;
                    rozmiarKolejki.println(displayTime+"\t"+queLength);
                }
                nextSignal+=(poissonFrequencyType)?1000*((-1/lambda)*log(new Random().nextDouble())):1000*(1/lambda)
                        ; //kolejne zgłoszenie
            }
            if(queLength>0){    //jeśli są jakieś oczekujące zgłoszenia
                int i=0;
                for(i=0;i<channel.length;i++){
                    if(channel[i]==false)
                        break;
                }
                if(i<channel.length){   //jeśli znalazł się kanał wolny
                    obslugaZgloszen.println(displayTime+"\t"+(i+1));
                    if(currentTime+signalProcesingTime<=maxTime)
                        obslugaZgloszen.println(displayTime+signalProcesingTime/1000+"\t"+(i+1));
                    else
                        obslugaZgloszen.println(maxTime/1000+"\t"+(i+1));
                    obslugaZgloszen.println();
                    channel[i]=true;
                    channelFinishTime[i]=currentTime+signalProcesingTime;
                    queLength--;
                    rozmiarKolejki.println(displayTime+"\t"+queLength);
                }
            }
            for(int i=0;i<numberOfChannels;i++){
                if(channelFinishTime[i]<=currentTime)
                    channel[i]=false;
            }
            currentTime+=1;
            if(currentTime==maxTime-1)
                rozmiarKolejki.println(displayTime+"\t"+queLength);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new SymulatorStacjiBazowej();
        // TODO code application logic here
    }
    
}
