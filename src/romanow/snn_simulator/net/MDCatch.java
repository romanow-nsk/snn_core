/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import romanow.snn_simulator.I_BinaryStream;
import romanow.snn_simulator.data.VectorId;
import romanow.snn_simulator.data.VectorX;
import romanow.snn_simulator.desktop.Static;

// Синаптическая ловушка - набор синапсов
// TODO: Пока синапсов как объектов нет - прямые ссылки на нейроны (аксоны)
// TODO: Тормозящие синапсы = добавить MDSonapse или булевский вектор
public class MDCatch  extends VectorX<MDSynapse> implements I_BinaryStream{
    //--------------- Параметры ловушки ----------------------------------------
    private MDNeuron parent=null;
    float Pcatch = 0;              // Уровень мембранного потенциала
    float Kcatch=1;                // Коэффициент РАЗРАСТАНИЯ ловушки
    public final static int stable=0;       // Постоянная
    public final static int temporal=1;     // Временная
    public final static int virtual=2;      // Не участвует в генерации потенциала
    int state=stable;
    int SpikeCount=0;               // Счетчик срабатываний ловушки
    int TotalSynCount=0;            // Сумма вх. спайков в синапсах
    int FireSynCount=0;             // Сумма ПОЛЕЗНЫХ вх. спайков в синапсах (явно вызывающих дендрю спайк)
    boolean fired=false;            // Индикатор срабатывания (в момент пeрехода уровня И ДО КОНЦА)
    int localSynCount=0;            // Текущий счетчик ПОЛЕЗНЫХ входных спайков
    int TeachImageId=0;             // Идентификатор обучающего образа
    int FireStep=0;                 // Шаг моделирования = срабатывание
    //---------------------------------------------------------------- ----------
    public float getKcatch() { return Kcatch; }
    public int getTeachImageId() { return TeachImageId; }
    public boolean isStable(){ return state==stable; }
    public boolean isTemporal(){ return state==temporal; }
    public boolean isVirtual(){ return state==virtual; }
    public void setStable(){ state = stable; }
    public void setTemporal(){ state = temporal; }
    public void setVirtual(){ state = virtual; }
    public boolean isFired(){ return fired; }
    public MDCatch(){
        }
    public MDCatch(MDNeuron parent){
        this.parent = parent;
        }
    public void setParent(MDNeuron parent){
        this.parent = parent;
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);                        // Сохранить id-ы
        out.writeFloat(Pcatch);
        out.writeFloat(Kcatch);
        out.writeInt(state);
        out.writeInt(SpikeCount);
        out.writeInt(TotalSynCount);
        out.writeInt(FireSynCount);
        out.writeInt(TeachImageId);
        }
    @Override
    public void load(DataInputStream out) throws IOException {
        super.load(out);       
        Pcatch = out.readFloat();
        Kcatch = out.readFloat();
        state = out.readInt();
        SpikeCount = out.readInt();
        TotalSynCount = out.readInt();
        FireSynCount = out.readInt();
        TeachImageId = out.readInt();
        }
    public void setLinks(VectorId<MDNeuron> src){
        for(int i=size()-1;i>=0;i--)
            get(i).setLinks(src);
        }
    public void hardReset(MDNet parent){
        Kcatch = 1;
        SpikeCount=0;    
        TotalSynCount=0;
        FireSynCount=0;
        TeachImageId=0;
        for(int i=size()-1;i>=0;i--)
            get(i).hardReset(parent);
        }
    public void softReset(MDNet parent){        // Исходное состояние ловушки
        Pcatch = parent.P0;
        localSynCount=0;
        FireStep=-1;
        fired = false;
        for(int i=size()-1;i>=0;i--)
            get(i).softReset(parent);
        }
    public void onFireReset(MDNet parent){  // Исходное состояние ловушки
        Pcatch = parent.P0;
        localSynCount=0;
        fired = false;
        for(int i=size()-1;i>=0;i--)
            get(i).onFireReset(parent);
        }
    public float oneStep(int step){
        float sum=0;
        boolean virtual = isVirtual();
        for(int i=size()-1;i>=0;i--){
            MDSynapse sp = get(i);
            if(sp.input.isFired()){
                sum+=sp.Ksens;
                sp.Ksens+=parent.parent.DKsens;
                sp.SpikeCount++;
                TotalSynCount++;
                localSynCount++;
                }
            }
        // Уровень потенциала приведен к размерности и чувствительности
        Pcatch -= Kcatch*sum/size();               
        if (!fired && Pcatch < parent.parent.Ucatch){   // Дендритовый спайк        
            fired = true;
            SpikeCount++;                   
            Kcatch+=parent.parent.DKcatch;  // Чувствительность ЛОВУШКИ при срабатывании увеличивается
            FireSynCount+=localSynCount;
            localSynCount=0;
            FireStep = step;
            }
        return  virtual ? 0 : sum;
        }
    public void increareKcatch(){
        Kcatch = parent.parent.increaseKcatch(Kcatch);
        }
    public void reduceKcatch(){
        Kcatch = parent.parent.reduceKcatch(Kcatch);
        }
    public void showStatistic(CBLog log){
        if (SpikeCount==0)
            return;
        log.toLog("("+TeachImageId+") "+Static.put(Kcatch)+" "+SpikeCount+" "+TotalSynCount+" "+FireSynCount+" / ");
        for(int i=0;i<size();i++)
            get(i).showStatistic(log);
        log.toLog("\n");
        }

}
