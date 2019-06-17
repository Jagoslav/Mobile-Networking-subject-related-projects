/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulacja.samochodu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.io.PrintWriter;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Random;
import java.util.Stack;
import javax.swing.JFrame;
/**
 *
 * @author Jakub
 */
enum SimState{
    movement, //ruch
    decision, //decyzja
    finish, //zakończenie
    end //po zakończeniu
}
public class Symulator extends JFrame {
    SymulacjaSamochodu projekt;
    SimState simState;
    boolean running; //pętla programu
    int size; //rozmiar mapy
    int blockChance; //szansa zablokowania drogi
    Point[][] map; //mapa współrzędnych punktów
    Point start; //punkt początkowy (wartości x i y = 0-size-1)
    Point finish; //punkt końcowy (wartości x i y = 0-size-1)
    Point nextNode; //węzeł sprawdzany (wartości x i y = 0-size-1)
    Point lastVisited; //poprzednio odwiedzony węzeł
    Point currentNodeOnScreen; //węzeł w ruchu wyświetlany na ekranie 
    Stack<Point> blockades; //blokady widoczne w danej iteracji
    int timeIterator; //jednostka czasu
    
    PrintWriter logi; //plik z zapisanym logiem symulacji
    PrintWriter script; //plik z zapisanym logiem symulacji
    
    public Symulator(SymulacjaSamochodu projekt,int size,int blockChance,Point startP, Point koniecP){
    simState=SimState.decision;
        this.projekt=projekt;
        this.size=size;
        this.blockChance=blockChance;
        blockades=null;
        //okno aplikacji
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setUndecorated(true);
        setVisible(true);
        //współrzędne i wartości
        map=new Point[size][size];
        for(int y=0;y<size;y++){ //ustawianie współrzędnych węzłów
            for(int x=0;x<size;x++){
                map[x][y]=new Point(50+ x*500/(size-1),50+ y*500/(size-1));
            }
        }
        if(startP==null)
            start=new Point(new Random().nextInt(size), new Random().nextInt(size));
        else start=new Point(startP);
        if(koniecP==null){
            do{
                int x,y;
                x=new Random().nextInt(size); 
                y=new Random().nextInt(size); 
                finish=new Point(x,y);
            }while(start.x==finish.x && start.y==finish.y);
        }
        else finish=new Point(koniecP);
        nextNode=new Point(start);
        lastVisited=null;
        currentNodeOnScreen=new Point(map[nextNode.x][nextNode.y].x,map[nextNode.x][nextNode.y].y);
        timeIterator = 0; //flaga czasu, potrzebna do wykresu
        running=true;
        run();
    }
    public void run(){
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60.0;
        double delta = 0;
        try{ //otwarcie pliku do zapisu
            logi=new PrintWriter("logi.txt","UTF-8");
        }catch(Exception e){}
        logi.println("#time\tnode\tdistance");
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
            }
        }
        System.exit(0);
    }
    public void tick() {
        switch(simState){
            case decision:
                if(nextNode.x==finish.x && nextNode.y==finish.y){
                    simState=SimState.finish;
                    break;
                }
                double[] dist=new double[4];
                dist[0]=dist[1]=dist[2]=dist[3]=MAX_VALUE;
                dist[0]=sqrt(pow(finish.x-nextNode.x,2)+pow(finish.y-(nextNode.y-1),2));
                dist[1]=sqrt(pow(finish.x-(nextNode.x+1),2)+pow(finish.y-nextNode.y,2));
                dist[2]=sqrt(pow(finish.x-nextNode.x,2)+pow(finish.y-(nextNode.y+1),2));
                dist[3]=sqrt(pow(finish.x-(nextNode.x-1),2)+pow(finish.y-nextNode.y,2));
                if(nextNode.y==0){dist[0]=MAX_VALUE;}
                else if(nextNode.x>=size-1){dist[1]=MAX_VALUE;}
                else if(nextNode.y>=size-1){dist[2]=MAX_VALUE;}
                else if(nextNode.x<=0){dist[3]=MAX_VALUE;}
                //obierz punkt
                if(lastVisited!=null){ //nie chcemy starego węzła
                    if(lastVisited.y==nextNode.y-1)dist[0]=MAX_VALUE;
                    if(lastVisited.x==nextNode.x+1)dist[1]=MAX_VALUE;
                    if(lastVisited.y==nextNode.y+1)dist[2]=MAX_VALUE;
                    if(lastVisited.x==nextNode.x-1)dist[3]=MAX_VALUE;
                }
                //szukanie najlepszego węzła
                int bestNode;
                double bestVal;
                boolean block;
                while(true){ 
                    bestNode=4;
                    bestVal=MAX_VALUE-1;
                    block=false;
                    for(int i=0;i<4;i++){
                        if(dist[i]<bestVal){
                            bestNode=i;
                            bestVal=dist[i];
                        }
                    }
                    //jeśli znaleziono ścieżkę lepsza niż nieistniejąca 
                    if(bestNode!=4 && new Random().nextInt(100)<blockChance){ //to o ile wystąpiła blokada
                        if(blockades==null)
                            blockades=new Stack<Point>();
                        Point blockade=new Point(50+nextNode.x*500/(size-1) , 50+ nextNode.y*500/(size-1));
                        if(bestNode==0)blockade.y-=8;
                        if(bestNode==1)blockade.x+=8;
                        if(bestNode==2)blockade.y+=8;
                        if(bestNode==3)blockade.x-=8;
                        blockades.push(blockade);
                        dist[bestNode]=MAX_VALUE; //trzeba popsuć ścieżkę
                        continue;
                    }
                    else 
                        break;
                }
                //decyzja o wyborze węzła
                lastVisited=new Point(nextNode);
                logi.println(timeIterator+"\t"+(nextNode.x+nextNode.y*size)+"\t"+(float)sqrt(pow(finish.x-nextNode.x,2)+pow(finish.y-nextNode.y,2))*10);
                if(bestNode==4)
                    simState=SimState.finish;//jeśli nie ma węzła właściwego to koniec szukania
                else{
                    if(bestNode==0){ nextNode.y--; }
                    else if(bestNode==1){ nextNode.x++; }
                    else if(bestNode==2){ nextNode.y++; }
                    else if(bestNode==3){ nextNode.x--; }
                    simState=SimState.movement;
                }
                break;
            case movement:
                if(nextNode.x<0||nextNode.y<0||nextNode.x>=size||nextNode.y>=size){
                    simState=SimState.finish;
                    break;
                }
                Point NextNodeOnScreen=new Point(50+nextNode.x*500/(size-1),50+nextNode.y*500/(size-1));
                if(NextNodeOnScreen.x!=currentNodeOnScreen.x || NextNodeOnScreen.y!=currentNodeOnScreen.y){ //jeśli trzeba się przesunąć to to robimy
                    if(NextNodeOnScreen.x>currentNodeOnScreen.x)
                        currentNodeOnScreen.x++;
                    else if(NextNodeOnScreen.x<currentNodeOnScreen.x)
                        currentNodeOnScreen.x--;
                    if(NextNodeOnScreen.y>currentNodeOnScreen.y)
                        currentNodeOnScreen.y++;
                    else if(NextNodeOnScreen.y<currentNodeOnScreen.y)
                        currentNodeOnScreen.y--;
                    //jeśli nie ma co się przesuwać to nullujemy
                }
                else{
                    simState=SimState.decision;
                    blockades=null;
                    timeIterator++;
                }
                break;
            case finish:
                logi.println(timeIterator+"\t"+(nextNode.x+nextNode.y*size)+"\t"+(float)sqrt(pow(finish.x-nextNode.x,2)+pow(finish.y-nextNode.y,2))*10);
                logi.close();
                try{ //otwarcie pliku do zapisu
                    script=new PrintWriter("script.txt","UTF-8");
                    script.println("set title \"Odleglosc od punktu\"");
                    script.println("set xlabel \"czas\""); 
                    script.println("set ylabel \"węzeł/Odległość\""); 
                    script.println("plot \"F:/logi.txt\" using 1:2 title 'Węzeł*10' with points, \\");
                    script.println("\"F:/logi.txt\" using 1:3 title 'Odległość' with linespoints");
                }catch(Exception e){}
                script.close();
                simState=SimState.end;
                break;
            case end:
                projekt.state=SymulacjaSamochodu.AppState.koniec;
                break;
            default:
                break;
        }
                
    };
    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        //tło
        g.setColor(Color.BLACK);
        g.fillRect(0,0,800,600);
        
        //gui
        g.setColor(Color.ORANGE);
        g.setFont(new Font("arial",32,32));
        g.drawLine(600, 0, 600, 600);
        g.setColor(Color.BLUE);
        g.drawString("start: "+start.y+","+start.x , 605, 60);
        g.setColor(Color.RED);
        g.drawString("koniec: "+finish.y+","+finish.x , 605, 120);
        g.setColor(Color.YELLOW);
        g.drawString("cel: "+nextNode.y+","+nextNode.x, 605, 180);
        g.setColor(Color.WHITE);
        g.drawString("czas: "+timeIterator,605, 240);
        g.drawString("mapa: "+size+"x"+size,605, 300);
        g.drawString("blokady: "+blockChance+"%",605, 360);
        if(simState==SimState.end){
            g.setColor(Color.YELLOW);
            if(finish.x==nextNode.x && finish.y==nextNode.y)
              g.drawString("cel osiągnięty",602,500);
            else g.drawString("Zablokowany",602,500);
        }
        /////mapa
        //ulice poziome
        g.setColor(Color.WHITE);
        for(int y=0;y<size;y++)
            for(int x=0;x<size-1;x++)
                g.drawLine(50+ x*500/(size-1)+8,50+ y*500/(size-1),50+ (x+1)*500/(size-1)-8, 50+ y*500/(size-1) );
        //ulice pionowe
        for(int y=0;y<size-1;y++)
            for(int x=0;x<size;x++)
                g.drawLine(50+ x*500/(size-1), 50+ y*500/(size-1) +8,50+ x*500/(size-1) ,50+ (y+1)*500/(size-1)-8 );
        //skrzyżowania
        //szczególne
        g.setColor(Color.BLUE);
        g.fillOval(map[start.x][start.y].x -8 ,map[start.x][start.y].y -8, 16, 16);
        g.setColor(Color.RED);
        g.fillOval(map[finish.x][finish.y].x -8 ,map[finish.x][finish.y].y -8, 16, 16);
        g.setColor(Color.ORANGE);
        //zwykłe
        for(int y=0;y<size;y++){
            for(int x=0;x<size;x++){
                g.drawOval(map[x][y].x -8 ,map[x][y].y -8, 16, 16);
            }
        }
        //aktywne pole
        g.fillOval(currentNodeOnScreen.x-4,currentNodeOnScreen.y-4, 8, 8);
        //blokady
        if(blockades!=null){
            g.setColor(Color.PINK);
            for(Point p : blockades){
                if(p.x>=50 && p.y>=50 && p.y<=550 && p.x<=550)
                g.fillOval(p.x-4, p.y-4, 8, 8);
            }
        }
        g.dispose();
        bs.show();
    };
}