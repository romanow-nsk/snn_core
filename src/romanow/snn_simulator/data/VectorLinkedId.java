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

// Вектор с сохранение ссылок на объекты с Id
public class VectorLinkedId<T extends ObjectId> extends VectorX<T>{
    @Override
    public void save(DataOutputStream out) throws IOException {
        int sz = size();
        out.writeInt(sz);
        if (sz==0) return;
        for (int i=0;i<sz;i++)
            out.writeInt(get(i).getId());
        }
    public Vector<Integer> loadLinks(DataInputStream out) throws IOException {
        Vector<Integer> xx = new Vector();
        clear();
        int sz = out.readInt();
        while (sz-- !=0){
            int id = out.readInt();
            xx.add(new Integer(id));
            }
        return xx;
        }
    public void setLinks(Vector<Integer> ids, VectorId<T> src){
        clear();
        for(int i=0;i<ids.size();i++)
            add(src.getById(ids.get(i).intValue()));
        }
}
