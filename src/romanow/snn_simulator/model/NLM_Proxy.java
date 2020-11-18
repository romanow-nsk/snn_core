package romanow.snn_simulator.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.layer.NL_DigitalSource;
import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_LayerModel;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.fft.FFTParams;
import romanow.snn_simulator.layer.LayerStatistic;

/**
 Прокси - модель (делегирование)
 */
public class NLM_Proxy implements I_LayerModel{
    private I_LayerModel orig=null;
    private int subToneCount=0;
    protected NL_NeuronLayer layer=null;
    protected NL_DigitalSource inputs=null;
    public NLM_Proxy(I_LayerModel orig){
        this.orig = orig;
        }
    public NLM_Proxy(){
        }

    public void setOrig(I_LayerModel orig) {
        this.orig = orig;
        }
    public void initModel(int subTones, I_NetParams params) throws Exception{
        if (orig==null)
            throw new IOException("В proxy нет оригинала");
        orig.initModel(subTones, params);
        }
    @Override
    public void reset(FFTParams pars) throws UniException{
        orig.reset(pars);
        }
    @Override
    public float[] step(float[] in, I_NeuronStep back)  throws UniException{
        return orig.step(in, back);
        }
    @Override
    public void setSubToneCount(int cnt) {
        orig.setSubToneCount(cnt);
        }
    @Override
    public int getSubToneCount() {
        return orig.getSubToneCount();
        }
    @Override
    public int size() {
        return orig.size();
        }
    @Override
    public I_NeuronOutput get(int idx) throws UniException {
        return orig.get(idx);
        }
    @Override
    public float[] getSpikes() throws UniException {
        return orig.getSpikes();
        }
    @Override
    public String getTypeName() {
        if (orig==null)
            return GBL.LoadedModel;
        return orig.getTypeName();
        }
    @Override
    public String getName() {
        return getTypeName();
        }
    @Override
    public void save(BufferedWriter out) throws IOException {
        if (orig==null)
            throw new IOException("В proxy нет оригинала");
        orig.save(out);
        }
    @Override
    public String save() throws IOException {
        throw new IOException("Ошибка формата: "+getClass().getSimpleName());
        }
    @Override
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        if (orig==null)
            throw new IOException("В proxy нет оригинала");
        orig.load(in,map);
        }
    @Override
    public String getFormatLabel() {
        if (orig==null)
            return "";
        return orig.getFormatLabel();
        }
    @Override
    public void numerateUID() {
        if (orig!=null)
            orig.numerateUID();
        }
    @Override
    public LayerStatistic getStatistic(int index) {
        if (orig!=null)
            return orig.getStatistic(index);
        GBL.notSupport();
        return null;
        }

    @Override
    public TypeFactory<LayerStatistic> getFactory() {
        if (orig!=null)
            return orig.getFactory();
        return new TypeFactory<LayerStatistic>();
    }
}
