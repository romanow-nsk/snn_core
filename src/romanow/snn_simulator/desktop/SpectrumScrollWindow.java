/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import romanow.snn_simulator.GBL;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.fft.FFTArray;
import romanow.snn_simulator.fft.FFTParams;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author romanow
 */
public class SpectrumScrollWindow extends javax.swing.JFrame {
    private FFT fft;
    private boolean white;
    private int dy=1;
    private int dySize=0;
    private int maxScroll;
    private ArrayList<SpectrumPoint> points = new ArrayList<>();

    public SpectrumScrollWindow(FFT data, boolean white0) {
        initComponents();
        setTitle("Спектрограмма");
        fft = data;
        white = white0;
        setBounds(100,100,1200,700);
        dySize = SpectrumPanel.getHeight()/dy;
        Scroll.setMinimum(0);
        maxScroll = fft.getLogSpectrumList().length-dySize;
        Scroll.setMaximum(maxScroll);
        Scroll.setValue(maxScroll);
        Bright.setValue(10);
        Bright.setMinimum(0);
        Bright.setMaximum(100);
        setVisible(true);
        }
    public void addPoint(SpectrumPoint point){
        points.add(point);
        }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        repaint();
        }
    public void repaint() {
        if (fft.getParams().logFreqMode()){
            FFTArray list[] = fft.getLogSpectrumList();
            int dx = SpectrumPanel.getWidth()/fft.getToneIndexes().length;     // Размер пикселя по XX,YY
            int dy=2;
            int hh = list.length*dy;
            int ww = SpectrumPanel.getWidth();
            int bb = maxScroll-Scroll.getValue();
            double vv = (fft.getStepMS()*bb)/1000;
            int min=(int)(vv/60);
            Mes.setText("Отсчет: "+bb+String.format(" %-2d:%-2d:%-3d сек.",min,(int)(vv-min*60),(int)((vv-(int)vv)*1000)));
            Graphics gg = SpectrumPanel.getGraphics();
            gg.setColor(white ? Color.white : Color.black);
            gg.fillRect(0, 0, ww-1, hh-1);
            for(int cStep=0;cStep<dySize && bb+cStep<list.length;cStep++){
                float spikes[] = list[bb+cStep].getOriginal();
                int xx = 5;
                int subToneCount = fft.getParams().subToneCount();
                for(int i=0;i<spikes.length;i+=subToneCount, xx+=dx*subToneCount){
                    gg.setColor(i%(12*subToneCount)==0 ? Color.red : Color.gray);
                    gg.drawLine(xx, 0, xx, hh-1);
                    }
                xx=6;
                int yy = cStep*dy;
                gg.setColor(white ? Color.white : Color.black);
                gg.fillRect(0, yy, ww-1, dy);
                float maxVal=0;
                for(int i=0;i<spikes.length;i++){
                    if (spikes[i] > maxVal)
                        maxVal = spikes[i];
                    }
                float bright = (float) (Bright.getValue()/10.);
                for(int i=0;i<spikes.length;i++,xx+=dx){
                    int val = (int) (bright*255*spikes[i]/GBL.FireON);
                    if (val <0)
                        val=0;
                    if (val >255)
                        val=255;
                    if (white)
                        val = 255-val;
                    gg.setColor(new Color(val,val,val));
                    gg.fillRect(xx,yy, dx, dy);
                    }
                }
            for(SpectrumPoint point : points){
                if (point.time<bb || point.time>bb+dySize)
                    continue;
                int yy = dy * (point.time-bb);
                int xx = dx * (point.subTone+1)+dx/2;
                gg.setColor(point.color);
                gg.fillRect(xx,yy, dx, dy);
                }
            }
        }
    /*
    public void paint(float spikes[]){          // Линейный спектр
        Graphics gg = getGraphics();
        if (cStep==0){
            gg.setColor(white ? Color.white : Color.black);
            gg.fillRect(0, 0, ww-1, hh-1);
        }
        int dd=10;
        gg.setColor(Color.red);
        for(int i=0;i<spikes.length;i+=dd){
            int xx = i/10*11;
            gg.setColor(i%100==0 ? Color.red : Color.gray);
            gg.drawLine(xx, 0, xx, hh-1);
        }
        int yy = (cStep%nSteps)*stepDy;
        gg.setColor(white ? Color.white : Color.black);
        gg.fillRect(0, yy, ww-1, stepDy);
        maxVal=0;
        for(int i=0;i<spikes.length;i++){
            if (spikes[i] > maxVal)
                maxVal = spikes[i];
        }
        for(int i=0;i<spikes.length;i++){
            int val = (int) (bright*255*spikes[i]/ GBL.FireON);
            if (val <0)
                val=0;
            if (val >255)
                val=255;
            if (white)
                val = 255-val;
            gg.setColor(new Color(val,val,val));
            int xx = i/10*11+i%10+1;
            gg.fillRect(xx,yy, 1, stepDy);
        }
        gg.setColor(Color.red);
        gg.drawLine(0, yy+stepDy, ww-1, yy+stepDy);
        cStep++;
        }
    */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SpectrumPanel = new javax.swing.JPanel();
        Bright = new javax.swing.JSlider();
        Scroll = new javax.swing.JSlider();
        Mes = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        SpectrumPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout SpectrumPanelLayout = new javax.swing.GroupLayout(SpectrumPanel);
        SpectrumPanel.setLayout(SpectrumPanelLayout);
        SpectrumPanelLayout.setHorizontalGroup(
            SpectrumPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1118, Short.MAX_VALUE)
        );
        SpectrumPanelLayout.setVerticalGroup(
            SpectrumPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
        );

        getContentPane().add(SpectrumPanel);
        SpectrumPanel.setBounds(40, 30, 1120, 610);

        Bright.setOrientation(javax.swing.JSlider.VERTICAL);
        Bright.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                BrightStateChanged(evt);
            }
        });
        Bright.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                BrightMouseReleased(evt);
            }
        });
        Bright.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                BrightCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(Bright);
        Bright.setBounds(10, 430, 30, 170);

        Scroll.setOrientation(javax.swing.JSlider.VERTICAL);
        Scroll.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ScrollStateChanged(evt);
            }
        });
        Scroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ScrollMouseReleased(evt);
            }
        });
        Scroll.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                ScrollCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(Scroll);
        Scroll.setBounds(10, 20, 30, 310);

        Mes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MesActionPerformed(evt);
            }
        });
        getContentPane().add(Mes);
        Mes.setBounds(40, 0, 780, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BrightCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_BrightCaretPositionChanged

    }//GEN-LAST:event_BrightCaretPositionChanged

    private void ScrollCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_ScrollCaretPositionChanged

    }//GEN-LAST:event_ScrollCaretPositionChanged

    private void MesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MesActionPerformed

    private void ScrollStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ScrollStateChanged

    }//GEN-LAST:event_ScrollStateChanged

    private void BrightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_BrightStateChanged

    }//GEN-LAST:event_BrightStateChanged

    private void ScrollMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ScrollMouseReleased
        repaint();
    }//GEN-LAST:event_ScrollMouseReleased

    private void BrightMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BrightMouseReleased
        repaint();
    }//GEN-LAST:event_BrightMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SpectrumScrollWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SpectrumScrollWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SpectrumScrollWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpectrumScrollWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        final FFT fft = new FFT();
        FFTParams params = new FFTParams().W(1024*16).subToneCount(2).procOver(90).
                FFTWindowReduce(false).p_Cohleogram(false).compressMode(true).compressGrade(2).kAmpl(1).
                winMode(FFT.WinModeSine);
        String path = FFTView.getInputFileName("Волна","*.wav",null);
        fft.waveLoad(params,path);
        //fft.waveLoad(params,"../Waves/WhenIm64.wav");
        System.out.println(fft);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SpectrumScrollWindow xx = new SpectrumScrollWindow(fft,false);
                FFTArray list[] = fft.getLogSpectrumList();
                for(int i=0;i<list.length;i++){
                    FFTArray array = list[i];
                    int mx=0;
                    for(int j=0;j<array.size();j++){
                        if (array.get(j) > array.get(mx))
                            mx = j;
                        }
                    xx.addPoint(new SpectrumPoint(mx,i,Color.GREEN));
                }
            xx.repaint();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider Bright;
    private javax.swing.JTextField Mes;
    private javax.swing.JSlider Scroll;
    private javax.swing.JPanel SpectrumPanel;
    // End of variables declaration//GEN-END:variables
}
