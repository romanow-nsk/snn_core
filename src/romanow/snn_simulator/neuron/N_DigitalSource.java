/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.UniExcept;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.Token;

/**
 *
 * @author romanow
 */
//----- Элемент - двоичный источник данных
public class N_DigitalSource implements I_Neuron{
    private float value=0;
    private int uid=0;
    public int getUID() {
        return uid;
        }
    public void setUID(int uid) {
        this.uid = uid;
        }    
    public void setSpike(float vv) throws UniException{
        if (vv <0 || vv>1)
            UniExcept.calcEx("Значение спайка "+vv);
        value = (float)vv;
        }
    @Override
    public float getSpike() {
        return value;
        }
    @Override
    public boolean hasValue() {
        return true;
        }
    @Override
    public boolean getFire() {
        return value !=0;
        }

    @Override
    public void changePotecial(I_NeuronStep Back) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOutValue(float val) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSpike(boolean val) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSynapse(I_NeuronOutput in, float weight) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFire(I_NeuronStep back) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeLevel(float delta) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getLevel() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return 0;
        }

    @Override
    public void createLinks(HashMap<Integer, I_NeuronOutput> map) {
        }

    @Override
    public String getTypeName() {
        return "Источник звука";
        }

    @Override
    public String getName() {
        return getTypeName();
        }

    @Override
    public void save(BufferedWriter out) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }

    @Override
    public String save() throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return null;
        }

    @Override
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }

    @Override
    public String getFormatLabel() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return null;
        }

    @Override
    public void setLearningMode(boolean learinig) {
        }
}
