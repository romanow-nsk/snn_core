/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import sun.audio.*;

/**
 *
 * @author romanow
 */
public class OutputDataPlayer extends Thread{
    class elem{                                 // Цепочка воспроизведения потоков
        AudioDataStream stream;
        int delay = 0;
        elem(AudioDataStream stream, int delay){
            this.delay = delay;
            this.stream = stream;
            }
        }
    private Vector<elem> queue = new  Vector(); // Цепочка воспроизведения потоков
    private volatile boolean finish=false;
    private volatile boolean noPlay=false;
    private ByteArrayOutputStream flow=null;
    private DataOutputStream  format=null;
    private int delay = 0;
    private int blockCount = 0;
    private int count=0;
    private int dataSize=0;
    public OutputDataPlayer(int blockCount){
        this.blockCount = blockCount;
        finish=false;
        start();
        }
    synchronized private void addToPlay() throws IOException{
        count++;
        if (count!=blockCount)
            return;
        delay = 1000*dataSize/(2*44100);
        synchronized(this){
            queue.add(new elem(new AudioDataStream(new AudioData(flow.toByteArray())),delay));
            this.notify();
            }
        flow.close();
        format.close();
        initInput();
        }
    //-------------- К НЕ РАБОТАЕТ ???????????????????????????????????
    public void addToPlay(float data[],int procOver, float K) throws IOException{
        testFlowHeader();
        int sz = data.length*(100-procOver)/100;
        dataSize +=  sz*2;
        for(int i=0;i<sz;i++){
            int vv = (int)(data[i]*Short.MAX_VALUE);
            format.writeByte(vv>>8);      // Старшим байтом ВПЕРЕД
            format.writeByte(vv);
            }
        addToPlay();
        }
    public void addToPlay(byte data[]) throws IOException{
        testFlowHeader();
        dataSize+=data.length;
        for(int i=0;i<data.length/2;i++){
            format.writeByte(data[2*i+1]);      // Старшим байтом ВПЕРЕД
            format.writeByte(data[2*i]);
            }
        addToPlay();
        }
    private void testFlowHeader()throws IOException{
        if (flow != null)
            return;
        flow = new ByteArrayOutputStream();
        format = new DataOutputStream(flow);
        format.writeInt(0x2E736E64);    // Формат AU - магическое число
        format.writeInt(24);            // Смещение до данных - 6 слов = 24 байта
        format.writeInt(-1);            // размерность поля данных - неизвестна       
        format.writeInt(3);             // 16-бит линейный РСМ
        format.writeInt(44100);         // Частота
        format.writeInt(1);             // Каналов - моно
        count=0;
        dataSize=0;
        }
    synchronized public void interruptPlay(){
        noPlay=true;
        }
    public void close(){
        finish = true;
        interrupt();
        }
    public void initInput(){
        flow = null;
        count = 0;
        dataSize = 0;
        }
    public void restart(){
        if (!finish)
            return;
        initInput();
        start();
        }
    public void run(){
        while(!finish){
            synchronized(this){         // Ожидание с засыпанием
                if (queue.size()==0)
                    try {
                        noPlay = false;
                        this.wait();
                        } catch (InterruptedException ex) {}
                }
            elem  copy = null;
            synchronized(this){         // Извлечение первой порции
                if (queue.size()!=0)
                    copy = queue.remove(0);
                }
            if (copy!=null && !noPlay)
                try {
                    AudioPlayer.player.start(copy.stream);
                    sleep(copy.delay);
                    } catch (Exception ex) {
                        finish=true;
                        }
                }
            }
    }
