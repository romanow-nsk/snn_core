package romanow.snn_simulator.lang;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import romanow.snn_simulator.layer.NL_DigitalSource;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_LayerModel;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronLayer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.fft.FFTParams;
import romanow.snn_simulator.layer.LayerFactory;
import romanow.snn_simulator.layer.LayerStatistic;
import romanow.snn_simulator.layer.NL_Integrate;
import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.neuron.N_BaseNeuron;
import romanow.snn_simulator.neuron.NeuronFactory;

/**
 *
 * @author romanow
 */
public class NLM_Constructive implements I_LayerModel{
    private int subToneCount=0;
    private int outLayerIdx=0;
    private int inLayerIdx=0;
    protected Vector<LayerStatistic> statistics=new Vector();       // СЕРИАЛИЗАЦИЯ ????
    protected Vector<I_NeuronLayer> layers=new Vector();
    protected NL_DigitalSource inputs=null;
    public NL_DigitalSource getInputs() {
        return inputs;
        }
    public void setOutLayerIdx(int outLayerIdx) {
        this.outLayerIdx = outLayerIdx;
        }
    public void setInLayerIdx(int inLayerIdx) {
        this.inLayerIdx = inLayerIdx;
        }
    public void add(I_NeuronLayer xx){
        layers.add(xx);
        }
    public I_NeuronLayer getLayerByName(String name){
        for(int i=0;i<layers.size();i++){
            if (layers.get(i).getObjectName().compareTo(name)==0)
                return layers.get(i);
            }
        return null;
        }
    public LayerStatistic getStatisticByName(String name){
        for(int i=0;i<statistics.size();i++){
            if (statistics.get(i).getObjectName().compareTo(name)==0)
                return statistics.get(i);
            }
        return null;
        }
    public NLM_Constructive(){}
    public void initModel(int subTones, I_NetParams params) throws Exception{
        subToneCount = subTones;
        if (inputs==null)       // ДВА РАЗА ВЫЗЫВАЕТСЯ, при создании и при старте
            inputs = new NL_DigitalSource(size());
        if (params==null)
            return;
        boolean learning = params.getLearningMode();
        for(int i=0;i<layers.size();i++)
            layers.get(i).setLearningMode(learning);
        }
    @Override
    public void reset(FFTParams pars) throws UniException{
        for(int i=0;i<layers.size();i++)
            layers.get(i).reset(pars);
        for(int i=0;i<statistics.size();i++)
        statistics.get(i).reset();
        }
    @Override
    public float[] step(float[] in, I_NeuronStep back)  throws UniException{
        inputs.setValues(in);
        for(int i=0;i<layers.size();i++)
            layers.get(i).step(back);
        for(int i=0;i<layers.size();i++)
            layers.get(i).synch();
        for(int i=0;i<statistics.size();i++)
            statistics.get(i).addStatistic();
        return layers.get(outLayerIdx).getSpikes();
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
        return layers.get(outLayerIdx).get(idx);
        }
    @Override
    public float[] getSpikes() throws UniException {
        return layers.get(outLayerIdx).getSpikes();
        }
    @Override
    public String getTypeName() {
        return GBL.LoadedModel;
        }
    @Override
    public String getName() {
        return getTypeName();
        }
    @Override
    public void save(BufferedWriter out) throws IOException {
        numerateUID();
        out.write(getFormatLabel());
        out.newLine();
        out.write(getTypeName());
        out.newLine();
        out.write(subToneCount+"/"+layers.size()+"/"+layers.get(outLayerIdx).getObjectName()+"/");
        out.write(inLayerIdx==-1 ? "???" : layers.get(inLayerIdx).getObjectName());
        out.newLine();        
        inputs.save(out);
        for(int i=0;i<layers.size();i++)
            layers.get(i).save(out);
        }

    @Override
    public void load(Token tk, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        tk.nextLine();
        subToneCount = tk.getInt();
        int lCount = tk.getInt();
        String inLayer = tk.get();
        String outLayer = tk.get();
        tk.nextLine();
        tk.get();
        tk.get();
        int lSize0 = tk.getInt();
        inputs = new NL_DigitalSource(lSize0);
        int uid0 = tk.getInt();        
        inputs.numerateUID(uid0);
        inputs.toMap(uid0, map);
        while(lCount--!=0){            // Читать слои
            tk.nextLine();
            String lName = tk.get();
            String lType = tk.get();
            String nType = tk.get();
            int lSize = tk.getInt();
            if (lType.compareTo(GBL.NeuronLayerName)==0){
                NL_NeuronLayer layer = new NL_NeuronLayer();
                layer.setObjectName(lName);
                NeuronFactory nFac = new NeuronFactory();
                N_BaseNeuron proto = nFac.getByName(nType);
                try {
                    layer.createLayer(proto, lSize);
                    } catch (Exception ex) {
                        new IOException("Ошибка создания: "+lName);
                        }
                layer.load(tk, map);
                layers.add(layer);
                }
            else{
                LayerFactory lFac = new LayerFactory();
                NL_Integrate layer = lFac.getByName(lType);
                layer.load(tk, map);
                layers.add(layer);
                }
            }
        //TODO связать синапсы ---------------------------------------------
        Object all[] = map.values().toArray();
        for(int i=all.length-1;i>=0; i--){
            I_Neuron nr = (I_Neuron)all[i];
            nr.createLinks(map);
            }
        }
    @Override
    public String save() throws IOException {
        throw new IOException("Ошибка формата: "+getClass().getSimpleName());
        }
    @Override
    public String getFormatLabel() {
        return "//<Тип модели>/<Счетчик полутонов>/<Кол-во слоев>/<Слой выхода>/<Слой входа>{<Слои>}";
        }
    @Override
    public void numerateUID() {
       int uid=0;
        for(int i=0;i<layers.size();i++)
            uid = layers.get(i).numerateUID(uid++);
        inputs.numerateUID(uid);
        }
    @Override
    public LayerStatistic getStatistic(int index) {
        return statistics.get(index);
        }
    @Override
    public TypeFactory<LayerStatistic> getFactory() {
        return new TypeFactory<LayerStatistic>(){{      // Инициализирующий код в анонимном классе
            for(int i=0;i<statistics.size();i++)
                add(statistics.get(i)); 
                }
            };
        }  
}
