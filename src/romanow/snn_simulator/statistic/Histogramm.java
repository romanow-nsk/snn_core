/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.statistic;

import romanow.snn_simulator.GBL;


/**
 *
 * @author romanow
 */
public class Histogramm {
    private StatCell cell = new StatCell();
    private String title="";
    private double min=0,max=0,dx=0;
    private int sz=0,total=0;
    private double counts[]=new double[0];
    public double getMid(){ return cell.getMid(); }
    public double getDisp(){ return cell.getDisp(); }
    public Histogramm(String title,int sz, double vals[],double min, double max){
        this(sz);
        create(vals,true,max,true,min);
        this.title = title+"("+GBL.afterP(getMid(),2)+
            "/"+GBL.afterP(getDisp(),2)+")";
        }
    public Histogramm(String title,int sz, double vals[],double max){
        this(sz);
        this.title = title;
        create(vals,true,max,false,0);
        }
    public Histogramm(String title,int sz, double vals[]){
        this(sz);
        this.title = title;
        create(vals);
        }
    public Histogramm(int sz, double vals[]){
        this(sz);
        create(vals);
        }
    public void addTitle(String ss){
        title = ss + " "+ title;
        }
    public Histogramm(int sz){
        this.sz = sz;
        counts = new double[sz];
        for(int i=0;i<sz;i++)
            counts[i]=0;
        }
    public double getMin() {
        return min;
        }
    public double getMax() {
        return max;
        }
    public double getDx() {
        return dx;
        }
    public int getSz() {
        return sz;
        }
    public double[] getCounts() {
        return counts;
        }
    public double getX(int idx){
        return min+idx*dx;
        }
    public String getTitle() {
        return title;
        }
    public void create(double vals[]){
        create(vals,false,0,false,0);
        }
    public void create(double vals[],double max){
        create(vals,true,max,false,0);
        }
    public void create(double vals[],
        boolean isMax, double max0, boolean isMin, double min0){
        max=min=vals[0];
        if (isMax) max = max0;
        if (isMin) min = min0;
        total = vals.length;
        for(int i=1;i<vals.length;i++){
            if (!isMax && vals[i]>max)
                max = vals[i];
            if (!isMin && vals[i]<min)
                min = vals[i];
            }
        dx = (max-min)/sz;
        for(int i=0;i<vals.length;i++){
            double val = vals[i];
            cell.addValue(val);
            int idx = (int)((val-min)/dx);
            if (val<min){
                total--;
                continue;
                }
            if (val>max){
                total--;
                continue;
                }
            counts[idx]++;
            }
        for(int i=0;i<counts.length;i++)
            counts[i] = 100*counts[i]/total;    // Нормализация в процентах
        }
}
