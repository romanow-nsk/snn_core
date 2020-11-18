/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

/**
 *
 * @author romanow
 */
public class BeeMax {
    private float fIdx=0;               // Индекс местоположения пчелы
    private int idx0=0;                 // Начальный индекс местоположения пчелы
    private float nextIdx=0;            // Местоположение следующего шага
    private float value=0;              // Собственный максимум
    public BeeMax(int idx){
        fIdx = idx;
        idx0 = idx;
        }
    public float getValue(){
        return value;
        }
    public void step(float spikes[],BeeMaxParams layer){
        int cIdx = (int)fIdx;           // Текущий индекс
        int left = idx0-layer.dSize;
        if (left <0)
            left = 0;
        int right = idx0+layer.dSize;
        if (right>=spikes.length)
            right = spikes.length-1;
        nextIdx = fIdx;
        boolean toUp=false;
        float delta = spikes[cIdx]-value;
        value += delta * layer.kVal;
        int cnt=0;
        for(int i=cIdx+1;i<spikes.length-1 && i<cIdx+layer.dUp;i++)
            if (spikes[i]>spikes[cIdx])
                cnt++;
        int cnt2=0;
        for(int i=cIdx-1; i>=0 && i>=cIdx-layer.dUp;i--)
            if (spikes[i]>spikes[cIdx])
                cnt2++;
        if (cnt > 3){
            nextIdx += layer.dxVal; // Движение в сторону роста сигнала
            toUp=true;
            }
        if (cnt2 > 3){
            nextIdx -= layer.dxVal;
            toUp=true;
            }
        if (delta > 0 || toUp){
            }
        else{
            nextIdx -= (cIdx-idx0)*layer.kBack;
            }
        if (nextIdx<left)
            nextIdx=left;
        if (nextIdx>right)
            nextIdx=right;
        }
    public void synch(){
        fIdx = nextIdx;
        }
    public int getIdx(){
        return (int)fIdx;
        }
}
