/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import romanow.snn_simulator.I_BinaryStream;

// объект с генерируемым идентификатором (при сохранении)
public class ObjectId implements I_BinaryStream{
    private int id=-1;
    public ObjectId(){}
    public ObjectId(int id){
        this.id = id;
        }
    public int getId() {
        return id;
        }
    public void setId(int id) {
        this.id = id;
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(id);
        }
    @Override
    public void load(DataInputStream out) throws IOException {
        id = out.readInt();
    }
}
