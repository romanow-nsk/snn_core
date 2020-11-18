/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package romanow.snn_simulator.statistic;

/**
 *
 * @author romanow
 */
public class StatCell{
    public int count=0;            // отсчеты (общие)
    public float sum=0;            // сумма
    public float sum2=0;           // сумма квадратов
    public StatCell(){}            // Конструктор без параметров ОБЯЗАТЕЛЬНО
    public String toString(){
        return " "+two(getMid())+"/"+two(getDisp())+" cnt="+count;
        }
    private String two(double dd){
        return ""+((int)(dd*10))/10.;
        }
    public double getMid(){
        return count==0 ?  0 : sum/count;
        }
    public double getDisp(){
        double mid = getMid();
        return count==0 ? 0 : Math.sqrt(sum2/count - mid*mid);
        }
    public void addValue(double vv){
        count++;
        sum+=vv;
        sum2+=vv*vv;
        }
    public boolean isEmpty(){ return count==0; }
    public int getCount(){ return  count; }
    public int getOrigCount(){ return count; }
    public void add(StatCell vv){
        count+=vv.count;
        sum+=vv.sum;
        sum2+=vv.sum2;
        }
    public void sub(StatCell vv){
        count-=vv.count;
        sum-=vv.sum;
        sum2-=vv.sum2;
        }
    public void shift(double val){   // "Сдвинуть" статистику на val
        sum2 += val*val*count + 2*val*sum;
        sum += val*count;
        }
}
