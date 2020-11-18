/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import romanow.snn_simulator.I_BinaryStream;
import romanow.snn_simulator.data.VectorId;

/**
 *
 * @author romanow
 */
public class MDSynapse implements I_BinaryStream{
    int     SpikeCount=0;           // Счетчик вх. спайков
    float  Ksens=0;                // Чувствительность синапса
    MDNeuron input=null;            // Вх. нейрон синапса
    private int inputId=0;
    public MDSynapse(){}
    public MDSynapse(MDNeuron inp){
        input = inp;
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(SpikeCount);
        out.writeInt(input.getId());
        }
    public MDNeuron getInput() {
        return input;
        }
    @Override
    public void load(DataInputStream out) throws IOException {
        SpikeCount = out.readInt();
        inputId = out.readInt();
        }
    public void setLinks(VectorId<MDNeuron> src){
        input = src.getById(inputId);
        }
    public void hardReset(MDNet parent){}
    public void softReset(MDNet parent){        // Исходное состояние ловушки
        SpikeCount = 0;
        Ksens = parent.Ksens0;
        }
    public void onFireReset(MDNet parent){   // Исходное состояние ловушки при срабатывании
        Ksens = parent.Ksens0;        
        }
    public void showStatistic(CBLog log){
        log.toLog(""+input.getId()+"("+SpikeCount+") ");
        }

}
