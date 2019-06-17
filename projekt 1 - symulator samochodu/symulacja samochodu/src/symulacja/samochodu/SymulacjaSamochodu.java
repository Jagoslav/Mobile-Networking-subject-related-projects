/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulacja.samochodu;

import java.awt.Point;



/**
 *
 * @author Jakub
 */
public class SymulacjaSamochodu{

    enum AppState{
        parametry,
        punkty,
        symulator,
        koniec
    }
        AppState state=AppState.parametry;
        Boolean running;
        public Parametry parametry; //obiekt klasy Parametry
        public Punkty punkty;   //obiekt klasy Punkty
        public Symulator symulator; //obiekt klasy Symulator
        public int rozmiar; //rozmiar kwadratowej macierzy pól, reprezentujacej mapę
        public int blokady; //liczba punktów procentowych szansy wystąpienia blokady na drodze
        public Point pPocz; //współrzędne punktu początkowego
        public Point pKonc; //współrzędne punktu końcowego
    public SymulacjaSamochodu(){
        parametry=null;
        punkty=null;
        symulator=null;
        run();
    }
    public void run(){
        running=true;
        while(running){
            //System.out.println(state);
            switch(state){
                case parametry:
                    if(parametry==null){
                        parametry=new Parametry(this);
                    }
                    break;
                case punkty:
                    if(punkty==null){
                        punkty=new Punkty(this,rozmiar,blokady);
                    }
                    break;
                case symulator:
                    if(symulator==null){
                        symulator=new Symulator(this,rozmiar,blokady,pPocz,pKonc);
                        punkty=null;
                    }
                    break;
                default:
                    running=false;
                    break;
            }
            if(state==AppState.koniec)
                running=false;
        }
    }
    public static void main(String[] args) {
        SymulacjaSamochodu symulacja=new SymulacjaSamochodu();
        // TODO code application logic here
    }
}