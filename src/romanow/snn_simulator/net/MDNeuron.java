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
import romanow.snn_simulator.data.ObjectId;
import romanow.snn_simulator.data.VectorId;
import romanow.snn_simulator.data.VectorLinkedId;
import romanow.snn_simulator.data.VectorX;

//----- Базовый класс нейрона, не привязанный к топологии НС
// Диапазон контактов
// Дендрит - вектор ЛОВУШЕК
// CallBack для событий
public class MDNeuron extends ObjectId{
    //------- Закрытые параметры нейрона  ---------------------------------------
    private float Pax[]=null;                  // Текущий мембранный потенциал аксона
    private int paxIdx=0;                       // Индекс в циклической задержке
    private int nextState=0;                    // Следующее состояние
    private int relaxCount=0;
    private int paceMaker=0;
    private Vector<Integer> contactsIds=null;   // Временный вектор id контактов
    //------- Параметры для модели ---------------------------------------------
    public final static int stateActive=0;      // Накопление потенциала
    public final static int stateSpike=1;       // Генерация спайка
    public final static int stateRelax=2;       // Восстановление
    int state=stateActive;                      // Состояние нейрона
    int SpikeCount=0;                   // Счетчик аксонный спайков, в т.ч. пейсмейкеров
    int PaceMakerCount=0;               // Счетчик пейстмейкеров
    int DendritCount=0;                 // Счетчик дендритных спайков
    int FireStep=0;                     // Шаг моделирования = срабатывание
    MDNet parent=null;                                          // Ссылка на сеть
    VectorX<MDCatch> dendrit = new VectorX();                   // Ловушки
    VectorLinkedId<MDNeuron> contacts = new VectorLinkedId();   // Диапазон контактов
    //--------------------------------------------------------------------------
    public int createPaceMakerInterval(int val){       // Переопределяется в ПК
        return (int)(parent.PaceMaker0/2*Math.random()*(255-val)/255);
        }
    public float getPax(){
        return Pax[paxIdx];
        }
    public VectorLinkedId<MDNeuron> getContacts() {
        return contacts;
        }
    public void setNextState() {
        state = nextState;
        }
    public boolean isFired(){                   // Генерирует спайк
        return state == stateSpike;
        }
    public void init(){
        Pax = new float[parent.NaxDelay];
        for(int i=dendrit.size()-1;i>=0;i--)
            dendrit.get(i).setParent(this);
        }
    public void hardReset(){
        for(int i=0;i<dendrit.size();i++)
            dendrit.get(i).hardReset(parent);
        }
    public void softReset(CBNet back){          // Исходное состояние нейрона
        SpikeCount=0;
        PaceMakerCount=0;
        DendritCount=0;
        FireStep=-1;
        state = stateActive;
        for(int i=Pax.length-1;i>=0;i--)
            Pax[i] = parent.P0; 
        paxIdx=0;
        int val=back.getInputActivity(this);
        paceMaker = createPaceMakerInterval(val);
        for(int i=0;i<dendrit.size();i++)
            dendrit.get(i).softReset(parent);
        }
    public void onFireReset(CBNeuron back){    // Действие при срабатывании
        for(int i=Pax.length-1;i>=0;i--)
            Pax[i] = parent.P0; 
        paxIdx=0;
        int val=back.getInputActivity(this);
        paceMaker = createPaceMakerInterval(val);
        for(int i=0;i<dendrit.size();i++)
            dendrit.get(i).onFireReset(parent);
        }
    // Цикл работы нейрона
    public void oneStep(int step, boolean isTeach,CBNeuron back){         
        if (state == stateSpike){               // спайк -> восстановление
            nextState = stateRelax;
            relaxCount = parent.Nrelax;
            onFireReset(back);                  // Сброс после спайка !!!!!!!!!!
            back.onStateRelax(this);
            return;
            }
        if (state == stateRelax){
            if (relaxCount-- == 0){
                nextState = stateActive;            // восстановление - накопление
                back.onStateActive(this);
                }
            return;
            }
        //--------------- Накопление мембранного потенциала --------------------
        float sum=0;
        for(int i=dendrit.size()-1;i>=0;i--){
            MDCatch ch = dendrit.get(i);
            sum += ch.oneStep(step);            // Вызывать всегда, т.к. это ПОВЕДЕНИЕ
            if (ch.isVirtual())
                continue;                       // Виртуальные ловушки - пропустить
            if (ch.isFired()){                  
                DendritCount++;
                back.onDendritSpike(this, ch);  // Дендритный спайк
                if (isTeach){                   // Обучение
                    ch.increareKcatch();
                    int oldImageId = ch.getTeachImageId();
                    int newImageId = back.getTeachImageId();
                    if (oldImageId != newImageId){  // Запоминание id-а обучающего изображения
                        if (oldImageId!=0 && newImageId!=0)
                            back.onEvent(this, ch, "Замещение образа "+oldImageId+"->"+newImageId);
                        if (newImageId!=0)
                            ch.TeachImageId = newImageId;
                        }
                    }
                //-- Не вызывает аксонного спайка, вместо него сброс чувствительности
                ch.onFireReset(parent);           
                //fire();
                return;
                }
            }
        int ii = paxIdx++;                          // Следующий = он же самый старый
        if (paxIdx == Pax.length)
            paxIdx=0;
        Pax[paxIdx] = Pax[ii] - sum*parent.KsensSyn;// Мембранный поненциял синапса
        if (Pax[paxIdx] < parent.Uax){              // Задержанный суммарный потенциал
            back.onAxonSpike(this);                 // Аксоный спайк
            fire(step);
            }
        paceMaker--;
        if (paceMaker<=0){                          // Спонтанная активность
            PaceMakerCount++;
            back.onPaceMakerSpike(this);
            fire(step);
            }
        }
    private void fire(int step){
        SpikeCount++;
        nextState = stateSpike;
        FireStep = step;
        for(int i=dendrit.size()-1;i>=0;i--){       // Чувствительность ловушек обратно пропорц. разнице
            MDCatch ch = dendrit.get(i);
            if(FireStep==-1 || ch.FireStep==-1)
                continue;
            int dd = FireStep - ch.FireStep;
            if (dd>100)
                continue;                          //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            ch.Kcatch += parent.DKcatch*5*(100-dd)/100;
            }
        }
    //--------------------------------------------------------------------------
    public MDNeuron() {
        }
    public MDNeuron(MDNet parent) {
        this.parent = parent;
        }
    public void setParent(MDNet parent) {
        this.parent = parent;
        }
    public MDNet getNet(){ return parent; }
    @Override
    public void load(DataInputStream out) throws IOException {
        super.load(out);
        contactsIds=contacts.loadLinks(out);                 // Загрузить ССЫЛКИ по Id               
        dendrit.load(out);
        }
    public void setLinks(VectorId<MDNeuron> src){
        contacts.setLinks(contactsIds, src);
        for(int i=dendrit.size()-1;i>=0;i--)
            dendrit.get(i).setLinks(src);
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        contacts.save(out);
        dendrit.save(out);
        }  
    public void createRandomCatches(int count,int size){
        while(count--!=0){
            MDCatch out = new MDCatch(this);        // Кол-во синапсов = равномерное
            out.setStable();
            int nsyn = 1+(int)(Math.random()*size);
            while (nsyn--!=0){                      // Совпадение вх. нейронов не проверяется
                int idx = (int)(contacts.size()*Math.random());
                out.add(new MDSynapse(contacts.get(idx)));
                }
            dendrit.add(out);
            }
        }
    public void showStatistic(CBLog log){
        log.toLog("["+dendrit.size()+"] "+SpikeCount+"("+PaceMakerCount+") "+DendritCount+"\n");
        for(int i=0;i<dendrit.size();i++){
            dendrit.get(i).showStatistic(log);
            }
        }
    public int removeUnusedCatches(){
        int sum=0;
        for(int i=dendrit.size()-1;i>=0;i--){
            MDCatch ch = dendrit.get(i);
            if (ch.SpikeCount==0){
                dendrit.remove(i);
                sum++;
                }
            }
        return sum;
        }
    public void paint(CBNetState back){
        back.onNeuron(this);
        for(int i=0;i<dendrit.size(); i++)
            back.onCatch(this, dendrit.get(i));
        }
    public void calcCatchCounts(int cnt[]){
        for(int i=0;i<dendrit.size(); i++){
            MDCatch ch = dendrit.get(i);
            int id = ch.TeachImageId;
            if (id<cnt.length)
                cnt[id]++;
            }
        }
    public void calcSpikeCounts(int cnt[]){
        for(int i=0;i<dendrit.size(); i++){
            MDCatch ch = dendrit.get(i);
            int id = ch.TeachImageId;
            if (id<cnt.length)
                cnt[id]+=SpikeCount;
            }
        }
}
