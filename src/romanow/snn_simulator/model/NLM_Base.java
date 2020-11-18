package romanow.snn_simulator.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.layer.NL_DigitalSource;
import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_LayerModel;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.fft.FFTParams;
import romanow.snn_simulator.layer.LayerStatistic;

/**
 *
 * @author romanow
 */
public class NLM_Base implements I_LayerModel{
    private int subToneCount=0;
    protected NL_NeuronLayer layer=null;
    protected NL_DigitalSource inputs=null;
    private LayerStatistic stat=null;       // ПОКА НА ВХОДЕ -------------------    
    public NL_NeuronLayer getLayer() {
        return layer;
        }
    public NL_DigitalSource getInputs() {
        return inputs;
        }
    public NLM_Base(){}
    public void initModel(int subTones, I_NetParams params) throws Exception{
        subToneCount = subTones;
        layer = new NL_NeuronLayer();
        inputs = new NL_DigitalSource(size());
        stat = new LayerStatistic(inputs,"Вход");        
        }
    @Override
    public void reset(FFTParams pars) throws UniException{
        layer.reset(pars);
        }
    @Override
    public float[] step(float[] in, I_NeuronStep back)  throws UniException{
        inputs.setValues(in);
        layer.step(back);
        layer.synch();
        stat.addStatistic();        
        return layer.getSpikes();
        }
    @Override
    public void setSubToneCount(int cnt) {
        subToneCount = cnt;
        }
    @Override
    public int getSubToneCount() {
        return subToneCount;
        }
    @Override
    public int size() {
        return subToneCount*FFT.Octaves*12;
        }
    @Override
    public I_NeuronOutput get(int idx) throws UniException {
        return layer.get(idx);
        }
    @Override
    public float[] getSpikes() throws UniException {
        return layer.getSpikes();
        }
    @Override
    public String getTypeName() {
        return getClass().getSimpleName();
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
    public void load(romanow.snn_simulator.Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public String getFormatLabel() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return null;
        }
    @Override
    public void numerateUID() {
        layer.numerateUID(1);
        }
    @Override
    public LayerStatistic getStatistic(int index){
        return stat;
        }
    @Override
    public TypeFactory<LayerStatistic> getFactory() {
        return new TypeFactory<LayerStatistic>(){
            { if (stat!=null) add(stat); }
            };
        }
}
