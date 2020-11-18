/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package romanow.snn_simulator.data;

import java.util.HashMap;

// Вектор с быстрым поиском по Id
public class VectorId<T extends ObjectId> extends VectorX<T>{
    HashMap map = new java.util.HashMap<Integer,T>();
    public void  createMap(){
        map.clear();
        for(int ii=0;ii<size();ii++){
            T a = this.get(ii);
            map.put(new Integer(a.getId()),a);
            }
        }
    public T getById(int id){
        return (T)map.get(new Integer(id));
        }
    private int indexTable[]=null;
    /* ---------- Обычный поиск
    public void  createMap(){
        int maxid = get(0).getId();
        for(int ii=1;ii<size();ii++){
            int idd = this.get(ii).getId();
            if (idd > maxid)
                maxid = idd;
            }
        indexTable = new int[maxid+1];
        for(int ii=0;ii<size();ii++)
            indexTable[ii]=0;
        for(int ii=0;ii<size();ii++){
            indexTable[get(ii).getId()]=ii;
            }
        }
    public T getById(int id){
        return get(indexTable[id]);
        }
    */
    public void createIds(){
        for(int ii=0;ii<size();ii++){
            get(ii).setId(ii);
            }        
        }
}
