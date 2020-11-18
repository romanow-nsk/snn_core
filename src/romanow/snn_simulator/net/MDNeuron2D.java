/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import romanow.snn_simulator.data.Object2D;

/**
 *
 * @author romanow
 */
public class MDNeuron2D extends MDNeuron implements Object2D{
    private int xx=0;
    private int yy=0;
    @Override
    public int getX() { return xx; }
    @Override
    public int getY() { return yy; }
    @Override
    public void setX(int x) { xx=x; }
    @Override
    public void setY(int y) { yy=y; }
    @Override
    public float diff(Object2D two) {
        return (float)(Math.sqrt((xx-two.getX())*(xx-two.getX()) + 
            (yy-two.getY())*(yy-two.getY())));
        }
    public MDNeuron2D() {
        super();
        }
    public MDNeuron2D(MDNet parent) {
        super(parent);
        }
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeInt(xx);
        out.writeInt(yy);
        }
    @Override
    public void load(DataInputStream out) throws IOException {       
        super.load(out);
        xx = out.readInt();
        yy = out.readInt();
        }
}
