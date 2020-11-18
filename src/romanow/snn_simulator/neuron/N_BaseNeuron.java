package romanow.snn_simulator.neuron;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_ObjectName;
import romanow.snn_simulator.I_RegularConnector;
import romanow.snn_simulator.I_TextStream;
import romanow.snn_simulator.Token;

public abstract class N_BaseNeuron implements I_Neuron,I_RegularConnector,I_TextStream,I_ObjectName{
    private String name="...";
    protected int index=0;
    private float potential=0;              // мембранный потенциал
    private float L=0;                      // Утечка
    private float level=0;                  // уровень срабатывания
    private float spikeVal=0;               // спайк предыдущего шага
    protected float nextSpikeVal=0;         // спайк текущего шага
    private boolean spikeIsSet=false;
    private boolean learningMode=false;
    private int uid=0;

    public boolean isLearningMode() {
        return learningMode;
        }
    public void setLearningMode(boolean learningMode) {
        this.learningMode = learningMode;
        }
    public int getUID() {
        return uid;
        }
    public void setUID(int uid) {
        this.uid = uid;
        }
    public void setL(float leak) {
        this.L = leak;
        }
    public void setIndex(int i){
        index = i;
        }
    public N_BaseNeuron(){
        }
    public float getPotential() {
        return potential;
        }
    public void reset(){
        potential = 0;
        }
    public void setLevel(float level) {
        this.level = level;
        }
    public void addPotential(float val){
        potential+=val;
        }
    public float getSpike(){
        return spikeVal;
        }
    public void setSpike(boolean on){    // Явно задать спайка
        spikeIsSet = true;
        nextSpikeVal = on ? GBL.FireON : GBL.FireOFF;
        potential=0;
        }
    public void setOutValue(float val){    // Явно задать уровень спайка
        spikeIsSet = true;
        if (val > GBL.FireON)
            nextSpikeVal = GBL.FireON;
        else
            nextSpikeVal = val;
        potential = 0;
        }
    public void synch(){                    // Синхронный переход к следующему состоянию
        spikeVal = nextSpikeVal;
        }
    //------ Варианты срабатывания ---------------------------------------------
    //  1. Явный вызов setSpike/setOutValue - с явным уровнем выхода
    //  2. Неявное изменение потенциала по addPotential() в changePotential
    //  3. Явный спайк по true в вызове changePotential
    public void step(I_NeuronStep back){
        nextSpikeVal=GBL.FireOFF;
        spikeIsSet = false;
        potential -= L;
        if (potential<0)
            potential=0;
        changePotecial(back);                   // fire - потенциал высчитывается,
        if (potential > level || spikeIsSet){
            if (!spikeIsSet)                     
                nextSpikeVal=GBL.FireON;
            if (nextSpikeVal==GBL.FireON){
                onFire(back);                   // Отложенный вызов = событие-спайк
                if (back!=null)
                    back.onFire(this);
                potential=0;
                }
            }        
        }
    @Override
    public void addInputsLinear(int idx, int size, I_Layer src) throws UniException{
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public void addInputsOctave(int idx, int octaveStep, int size, I_Layer src) throws UniException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public boolean hasValue() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return false;
        }
    @Override
    public boolean getFire() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return false;
        }
    @Override
    public void save(BufferedWriter out) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public void addSynapse(I_NeuronOutput in, float weight) {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public void onFire(I_NeuronStep back) {
        }
    @Override
    public void changeLevel(float delta) {
        level+=delta;
        if (level<0)
            level=0;
        if (level>=Short.MAX_VALUE/2)
            level=Short.MAX_VALUE/2;
        }
    @Override
    public float getLevel() {
        return level;
        }
    @Override
    public String getTypeName() {
        return getClass().getSimpleName();
        }
    //-----------------------------------------------------------------------------
    @Override
    public String getFormatLabel() {
        return getObjectName()+"/"+getTypeName()+"/L";
        }    
    @Override
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException{
        L = in.getFloat();
        map.put(uid, this);
        }
    @Override
    public String save() throws IOException{
        return ""+L;
        }
    @Override
    public String getObjectName() {
        return name;
        }
    @Override
    public void setObjectName(String name) {
        this.name = name;
        }
    @Override
    public void changePotecial(I_NeuronStep back){
        }
}
