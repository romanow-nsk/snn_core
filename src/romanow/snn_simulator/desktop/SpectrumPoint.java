package romanow.snn_simulator.desktop;

import java.awt.*;

public class SpectrumPoint {
    public final int subTone;
    public final int time;
    public final Color color;
    public SpectrumPoint(int subTone, int time, Color color) {
        this.subTone = subTone;
        this.time = time;
        this.color = color;
    }
}
