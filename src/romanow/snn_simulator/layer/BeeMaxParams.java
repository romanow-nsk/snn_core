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
public class BeeMaxParams {
    public float dxVal = 3f;              // Смещение в сторону увеличения сигнала
    public float kBack = 0.1f;           // Коэффициент возвращения на место
    public float kVal = 0.1f;             // Инертность отслеживания value
    public int dSize = 50;                // Часть диапазона, куда можно уходить    
    public int dUp = 10;                 // Интервал, где искать возрастание
    public int cntUp = 3;                 
    public BeeMaxParams(float dxVal, float kBack, float kVal, int dSize, int dUp, int cntUp){
        this.cntUp = cntUp;
        this.dSize =dSize;
        this.dUp = dUp;
        this.dxVal = dxVal;
        this.kBack = kBack;
        this.kVal = kVal;
        }
}
