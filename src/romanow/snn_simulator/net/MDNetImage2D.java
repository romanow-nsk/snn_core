/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import romanow.snn_simulator.data.VectorLinkedId;

/**
 *
 * @author romanow
 */
public class MDNetImage2D extends MDNet{
    public int XYSize=0;
    public int XYNear=0;
    //----------------- Создание с параметрами - размерностями по одной координате
    @Override
    public void create(int xySize, int xyNear){
        super.create(xySize*xySize, xyNear*xyNear);
        }
    @Override
    public void createNetParams(int size, int near){
        XYSize = (int)Math.sqrt(size);
        XYNear = (int)Math.sqrt(near);
        super.createNetParams(size, near);
        }
    @Override
    public MDNeuron createNeuron(MDNet parent) {
        return new MDNeuron2D(parent);
        }
    @Override
    public VectorLinkedId<MDNeuron> createNeuronLinks(int idx) {
        int y1 = idx / XYSize;      // Квадрат соседей
        int x1 = idx % XYSize;
        VectorLinkedId<MDNeuron> out = new VectorLinkedId<MDNeuron>();
        for(int i=y1-XYNear/2; i<=y1+XYNear/2;i++){
            if (i<0 || i>=XYSize) 
                continue;
            for(int j=x1-XYNear/2; j<=x1+XYNear/2;j++){
                if (j<0 || j>=XYSize) 
                    continue;
                if (i==y1 && j==x1)
                    continue;
                out.add(get(i*XYSize+j));
                }
            }
        return out;
        }
    @Override
    public void prepareNetBefore(){
        for(int i=0; i<size();i++){
            MDNeuron2D nr = (MDNeuron2D)get(i);
            nr.setY(i/XYSize);
            nr.setX(i%XYSize);
            }
        }
    @Override
    public void prepareNetAfter(){}
     @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeInt(XYSize);
        out.writeInt(XYNear);
        }
    @Override
    public void load(DataInputStream out) throws IOException {       
        super.load(out);
        XYSize = out.readInt();
        XYNear = out.readInt();
        }
    public static void main(String argv[]) throws IOException{
        MDNetImage2D ll = new MDNetImage2D();
        ll.create(10,4);
        ll.save(new DataOutputStream(new FileOutputStream("net2.dat")));
        MDNetImage2D l2 = new MDNetImage2D();
        l2.load(new DataInputStream(new FileInputStream("net2.dat")));
        }
}
