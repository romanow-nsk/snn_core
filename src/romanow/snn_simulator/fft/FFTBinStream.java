package romanow.snn_simulator.fft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface FFTBinStream {
    public void load(DataInputStream in, int formatVersion) throws IOException;
    public void save(DataOutputStream out) throws IOException;
}
