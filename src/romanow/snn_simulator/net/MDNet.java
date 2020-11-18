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
import romanow.snn_simulator.I_BinaryStream;
import romanow.snn_simulator.data.VectorId;
import romanow.snn_simulator.data.VectorLinkedId;

// Базовый класс сети, отвязанный от топологии (делается в ПК)
public class MDNet extends VectorId<MDNeuron> implements I_BinaryStream,FaceNet{
    private boolean valid=false;
    public boolean isValid(){ return valid; }
    // Нспледуется от вектора нейронов
    //---------- Начальные и текущие параметры сети
    int     Nsize=100;                  // Размерность сети
    float   P0=1;                       // Начальный мембранный потенциал
    int     Lcatch=4;                   // Макс. размерность ловушки
    int     Ncatch=40;                  // Макс. кол-во ловушек
    int     Nsyn=100;                   // Макс. кол-во синапсов
    int     Nnear=40;                   // Размерность окружения
    float   Ksens0=0.3F;                // Начальная чувствительность синапса
    float   DKsens=0.1F;                // Увеличение чувствительности ЛОВУШКИ при дендритном спайке
    float   Ucatch=0.3F;                 // Пороговый потенциал ловушки
    float   DKcatch=0.3F;                 // 
    float   Uax=0.4F;                    // Пороговый потенциал аксона
    float   KsensSyn=0.1F;               // Коэффициент чувствительности синапсов
                                        // (распространения синапт. потенциалов)
    int     Nrelax=10;                  // Кол-во циклов восстановления
    int     NaxDelay = 10;              // Кол-во циклов задержки распространения
                                        // дендритного потенциала к аксону +1 
                                        // (=1 нет задержки)
    int     PaceMaker0=5000;            // Интервал пейсмейкера = вероятный (0..200)
    float   KcatchMin=0.2F;              // Интервал чувствительности ловушки
    float   KcatchMax=10;
    int     CatchCreateInterval=100;    // Интервал создания ловушек
    //---------- Рабочие данные -----------------------------------------------
    boolean outputs[]=null;             // состояния выходов
    //--------------------------------------------------------------------------
    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeInt(Nsize);
        out.writeInt(Nnear);
        out.writeInt(Lcatch);
        out.writeInt(Ncatch);
        out.writeInt(Nsyn);
        out.writeInt(Nrelax);
        out.writeInt(NaxDelay);
        out.writeInt(PaceMaker0);
        out.writeFloat(P0);
        out.writeFloat(Ksens0);
        out.writeFloat(DKsens);
        out.writeFloat(DKcatch);
        out.writeFloat(Ucatch);
        out.writeFloat(Uax);
        out.writeFloat(KsensSyn);
        }
    @Override
    public void load(DataInputStream out) throws IOException {       
        super.load(out);
        Nsize = out.readInt();
        Nnear = out.readInt();
        Lcatch = out.readInt();
        Ncatch = out.readInt();
        Nsyn = out.readInt();
        Nrelax = out.readInt();
        NaxDelay = out.readInt();
        PaceMaker0 = out.readInt();
        P0 = out.readFloat();
        Ksens0 = out.readFloat();
        DKsens = out.readFloat();
        DKcatch = out.readFloat();
        Ucatch = out.readFloat();
        Uax = out.readFloat();
        KsensSyn = out.readFloat();
        createMap();
        setLinks();
        init();
        valid=true;
        }
    public void exec(int nstep, boolean isTeach,final CBNet back){
        int i=0;
        try {
            softReset(back);
            back.onStart();
            for(i=0;i<nstep;i++){
                if (back.testStop())
                    break;
                back.delay();
                final int ii=i;
                back.onStep(i);
                oneStep(i, isTeach,new CBNeuron(){
                    @Override
                    public void onAxonSpike(MDNeuron nr) {
                        back.onAxonSpike(ii,nr);
                        }
                    @Override
                    public void onPaceMakerSpike(MDNeuron nr) {
                        back.onPaceMakerSpike(ii,nr);
                        }
                    @Override
                    public void onDendritSpike(MDNeuron nr, MDCatch nc) {
                        back.onDendritSpike(ii,nr, nc);
                        }
                    @Override
                    public void onStateRelax(MDNeuron nr) {
                        back.onStateRelax(ii, nr);
                        }
                    @Override
                    public void onStateActive(MDNeuron nr) {
                        back.onStateActive(ii, nr);
                        }
                    @Override
                    public int getInputActivity(MDNeuron nr) {
                        return back.getInputActivity(nr);
                        }

                    @Override
                    public void onEvent(MDNeuron nr, MDCatch ch, String evt) {
                        back.onEvent(nr, ch, evt);
                        }
                    @Override
                    public int getTeachImageId() {
                        return back.getTeachImageId();
                        }
                    });
                }
            back.onFinish();
            } catch(Throwable ee){ 
                back.onFatal(i,ee);
                }
        }
    public void setLinks(){
        for(int i=0;i<size();i++)
            get(i).setLinks(this);
        }
    public void init(){
        for(int i=0;i<size();i++){
            MDNeuron nr = get(i);
            nr.setParent(this);
            nr.init();
            }
        }
    public void createNetParams(int size, int near){
        P0 = 1;
        Nsize = size;
        Nnear = near;                           // Кол-во нейронов окружения
        Nsyn = near * 3;                        // Синапсов
        Ncatch = (int)(2.*Nsyn/Lcatch);         // 2 - для равномерного распред.
        }
    @Override
    public void prepareNetBefore(){}
    @Override
    public void prepareNetAfter(){}
    public void create(int size, int near){
        createNetParams(size, near);
        int id=0;
        for(int i=0;i<Nsize;i++,id++){
            MDNeuron nr = createNeuron(this);
            nr.setId(id);
            add(nr);
            }
        createMap();                    // Создать хэш-таблицу для нейронов по id
        prepareNetBefore();
        for(int i=0;i<Nsize;i++){        // Полиморфный генератор контактов
            MDNeuron nr = get(i);
            nr.contacts = createNeuronLinks(i);
            }
        //----------- Рандомные ловушки не создавать ??? 
        for(int i=0;i<Nsize;i++){        // Сгенерировать ловушки
            get(i).createRandomCatches(Ncatch, Lcatch);
            }
        prepareNetAfter();
        init();
        hardReset();
        valid=true;
        }
    //------------ Создать ДК под топологию ------------------------------------
    // Линейный диапазон контактов в БК +/- near
    @Override
    public VectorLinkedId<MDNeuron> createNeuronLinks(int idx) {
        VectorLinkedId<MDNeuron> out = new VectorLinkedId();
        for(int j=idx - Nnear/2; j<=idx+Nnear/2;j++){
            int k=j;
            if (j == idx)
                continue;
            if (j<0)
                k=j+size();
            if (j>=size())
                k=j-size();
            out.add(get(k));
            }
        return out;
        }
    @Override
    public MDNeuron createNeuron(MDNet parent){
        return new MDNeuron(parent);
        }
    public void hardReset(){                    // Сброс сети
        for(int i=size()-1;i>=0;i--)            // с очисткой накопленной статистики  
            get(i).hardReset();
        }
    public void softReset(CBNet back){          // Сброс сети 
        for(int i=size()-1;i>=0;i--)            // для продолжения моделирования
            get(i).softReset(back);
        }
    public void setOutputs(){
        for(int i=size()-1;i>=0;i--)              
            outputs[i] = get(i).isFired();
        }
    public void oneStep(int step, boolean isTeach, final CBNeuron back){
        for(int i=size()-1;i>=0;i--){
            get(i).oneStep(step, isTeach,new CBNeuron(){
                @Override
                public void onAxonSpike(MDNeuron nr) {
                    back.onAxonSpike(nr);
                    }
                @Override
                public void onPaceMakerSpike(MDNeuron nr) {
                    back.onPaceMakerSpike(nr);
                    }
                @Override
                public void onDendritSpike(MDNeuron nr, MDCatch nc) {
                    back.onDendritSpike(nr, nc);
                    }
                @Override
                public void onStateRelax(MDNeuron nr) {
                    back.onStateRelax(nr);
                    }
                @Override
                public void onStateActive(MDNeuron nr) {
                    back.onStateActive(nr);
                    }
                @Override
                public int getInputActivity(MDNeuron nr) {
                    return back.getInputActivity(nr);
                    }

                @Override
                public void onEvent(MDNeuron nr, MDCatch ch, String evt) {
                    back.onEvent(nr, ch, evt);
                    }
                @Override
                public int getTeachImageId() {
                    return back.getTeachImageId();
                    }
                });
            }              
        for(int i=size()-1;i>=0;i--)               // В конце - общий переход к след. состоянию
            get(i).setNextState();
        }
    public int showStatistic(int n0,CBLog log){
        int i=n0;
        log.toLog("Нейрон:id [ловушек] ---Спайк аксона--Спайк пейсмейкера\n");
        log.toLog("Ловушка:(id образа) Чувств. Спайков Входных Вх.эффективных --- Вход:id (спайков)\n");
        for(i=n0;i<size(); i++){
            if (!log.toLog(""+get(i).getId()+"------"))
                get(i).showStatistic(log);
            else
                break;
            }
        if (i==size())
            i=0;
        return i;
        }
    public void paint(CBNetState back){
        for(int i=0;i<size(); i++)
            get(i).paint(back);
        }
    public int removeUnusedCatches(){
        int sum=0;
        for(int i=0;i<size(); i++)
            sum+=get(i).removeUnusedCatches();
        return sum;
        }
    public int[] calcCatchCounts(int size){
        int cnt[]=new int[size];
        for(int i=0;i<cnt.length;i++)
            cnt[i]=0;
        for(int i=0;i<size();i++)
            get(i).calcCatchCounts(cnt);
        return cnt;
        }
    public int[] calcSpikeCounts(int size){
        int cnt[]=new int[size];
        for(int i=0;i<cnt.length;i++)
            cnt[i]=0;
        for(int i=0;i<size();i++)
            get(i).calcSpikeCounts(cnt);
        return cnt;
        }
    public float increaseKcatch(float val){
        if (val< KcatchMax)
            val+=DKcatch;
        return val;
        }
    public float reduceKcatch(float val){
        if (val> KcatchMin)
            val-=DKcatch;
        return val;
        }
    public static void main(String argv[]) throws IOException{
        MDNet ll = new MDNet();
        ll.create(10,5);
        ll.save(new DataOutputStream(new FileOutputStream("net1.dat")));
        MDNet l2 = new MDNet();
        l2.load(new DataInputStream(new FileInputStream("net1.dat")));
        }
}
