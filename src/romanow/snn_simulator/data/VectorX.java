/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package romanow.snn_simulator.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import romanow.snn_simulator.I_BinaryStream;


public class VectorX<T extends I_BinaryStream>  extends Vector<T> implements I_BinaryStream{
    public VectorX(){
        clear();
        }
    public Vector<T> copy(){
        Vector<T> xx = new java.util.Vector();
        for(int ii=0;ii<size();ii++)
            xx.add(get(ii));
        return xx;
        }
    public void add(VectorX<T> two){
        for(int ii=0;ii<two.size();ii++)
            add(two.get(ii));
        }
    public boolean isIndex(int ii){
        return ii >=0 && ii<size();
        }
    public T remove(int ii){ 
        if (isIndex(ii)) 
            return super.remove(ii);
        return null;
        }
    public T get(int ii){ 
        return isIndex(ii) ? super.get(ii) : null;
        }
    public void foreach(FunDo ff){  
        for(int ii=0;ii<size();ii++)
            ff.doIt(get(ii));
        }
    public T firstThat(FunTest ff){
        for(int ii=0; ii<size();ii++){
            T vv = get(ii);
            if (ff.test(vv))
                return vv;
            }
        return null;
        }
    public T firstLike(T in,FunLike ff){
        for(int ii=0; ii<size();ii++){
            T vv = get(ii);
            if (ff.like(in,vv))
                return vv;
            }
        return null;
        }
    int inear(T obj, FunCmp diff){
        if (size()==0)
            return -1;
        int vmin = 0;
        int zz = diff.cmp(obj,get(0));
        for(int ii=1;ii<size();ii++){
            int rr = diff.cmp(obj,get(ii));
            if (rr < zz){
                vmin = ii;
                zz = rr;
                }
            }
        return vmin;
        }
    public T vnear(T obj, FunCmp diff){
        int ii=inear(obj,diff);
        return ii==-1 ? null: get(ii);
        }
    int ifar(T obj, FunCmp diff){
        if (size()==0)
            return -1;
        int vmin = 0;
        int zz = diff.cmp(obj,get(0));
        for(int ii=1;ii<size();ii++){
            int rr = diff.cmp(obj,get(ii));
            if (rr > zz){
                vmin = ii;
                zz = rr;
                }
            }
        return vmin;
        }
    public T vfar(T obj, FunCmp diff){
        int ii=ifar(obj,diff);
        return ii==-1 ? null: get(ii);
        }
    int imin(FunCmp cmp){
        if (size()==0)
            return -1;
        int vmin= 0;
        for(int ii=1; ii< size();ii++){
            if (cmp.cmp(get(ii), get(vmin)) < 0)
                vmin = ii;
            }
        return vmin;
        }
    T vmin(FunCmp cmp){
        int ii=imin(cmp);
        return ii == -1 ?  null : get(ii);
        }
    int imax(FunCmp cmp){
        if (size()==0)
            return -1;
        int vmin= 0;
        for(int ii=1; ii< size();ii++){
            if (cmp.cmp(get(ii), get(vmin)) > 0)
                vmin = ii;
            }
        return vmin;
        }
    T vmax(FunCmp cmp){
        int ii=imax(cmp);
        return ii == -1 ?  null : get(ii);
        }
    public void remove(FunTest ff){
        int sz = size();
        int ii = 0;
        while (ii < sz){
            T vv = get(ii);
            if (ff.test(vv)){
                super.remove(ii);
                sz--;
                }
            else ii++;
            }
        }
    // Сохраняется тип первого объекта = т.е. при сохранении только ОДНОТИПНЫЕ вектора              
    @Override
    public void save(DataOutputStream out) throws IOException {
        int sz = size();
        out.writeInt(sz);
        if (sz==0) return;
        out.writeUTF(get(0).getClass().getName());
        for (int i=0;i<sz;i++)
            get(i).save(out);
        }
    @Override
    public void load(DataInputStream out) throws IOException {
        clear();
        int sz = out.readInt();
        if (sz==0)
            return;
        String ss = out.readUTF();
        Class proto = null;
        try {
            proto = Class.forName(ss);
            } catch (ClassNotFoundException ex) {
                throw new IOException("class "+ss+" not found");
            }
        while(sz--!=0){
            I_BinaryStream oo = null;
            try {
                oo = (I_BinaryStream)proto.newInstance();
                } catch (Throwable ee) {
                    throw new IOException("object "+ss+" not created");
                    }
            oo.load(out);
            add((T)oo);
            }
        }
}
