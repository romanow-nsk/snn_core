/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import java.awt.Color;
import java.util.Vector;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.fft.FFTParams;
import romanow.snn_simulator.statistic.StatCell;

/**
 *
 * @author romanow
 */
public class LineGraphicFrame extends LayerWindow{
    private class Graphic{
        String title;
        float vals[];
        Graphic(String title, float vals[]){
            this.title = title;
            this.vals = vals;
            }
        }
    private void addOrReplace(Graphic gg){
        for(int i=0;i<data.size();i++){
            if (data.get(i).title.compareTo(gg.title)==0){
                data.remove(i);
                final int ii=i;
                Platform.runLater(new Runnable(){
                    public void run(){
                        lineChart.getData().remove(ii); 
                        }
                    });
                break;
                }
            }
        data.add(gg);
        }
    private Vector<Graphic> data = new Vector();
    private FFTParams params;
    private LayerWindowCallBack back=null;
    private Color backColor = new Color(240,240,240);
    private Color foreColor = Color.BLACK;
    LineChart<?, Number> lineChart;
    private int subToneCount=1;
    public LineGraphicFrame(){
        super("Статистика");
        }
    public LineGraphicFrame(LayerWindowCallBack back, FFTParams params0) {
        super("Статистика");
        params = params0;
        subToneCount = params.subToneCount();
        initComponents();
        this.back = back;
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel);
        setBounds(100, 100, 1150, 700);
        fxPanel.setBounds(10, 50, 1100, 600);
        setVisible(true);
        StatList.add("...");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
                }
            });
        }

    private void initFX(JFXPanel fxPanel){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis xxAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Тон");
        if (params.logFreqMode()){
            lineChart =  new LineChart<String, Number>(xAxis, yAxis);
            }
        else{
            lineChart =  new LineChart<Number, Number>(xxAxis, yAxis);
            }
        lineChart.setTitle("Статистика слоев");
        Scene scene = new Scene(lineChart, 1100, 600);
        fxPanel.setScene(scene);
        }
    //--------------------------------------------------------------------------
    private String put6(double dd){
        return GBL.afterP(dd, 2);
        }    
    //--------------------------------------------------------------------------
    public void paintOne(Graphic stat){
        if (params.logFreqMode())
            paintOneLog(stat);
        else
            paintOneLinear(stat);
        }
    public void paintOneLog(Graphic stat){
        XYChart.Series series = new XYChart.Series();
        float data[] = stat.vals;
        series.setName(stat.title);
        ObservableList dd = series.getData();
        //------------- TODO 
        int sz = subToneCount * FFT.Octaves * 12;
        if (data.length!=sz)            // ПОКА НЕСОВПАДАЮЩИЕ ВЫБРАСЫВАТЬ
            return;
        int subToneCount = data.length/FFT.Octaves/12;
        for(int j=0;j<data.length;j++){   // Подпись значений факторов j-ой ячейки             
            String ss="";
            if (j%subToneCount!=0)
                ss=""+j;
            else
                ss=FFT.getFullNoteNameByIndex(j/subToneCount);
            XYChart.Data<String, Float> item = new XYChart.Data<String, Float>(ss, data[j]);
            dd.add(item);
            }
        lineChart.getData().add(series);
        //----------- Node появляются только после добавления серии !!!!!!!!!!!!
        for(int ii=0;ii<data.length;ii++){
            XYChart.Data<String, Float> item = (XYChart.Data<String, Float>)dd.get(ii);
            final Node node = item.getNode(); 
            final float vv = data[ii];
            //------------------ Наведение мыши ----------------------------
            String ss = FFT.getFullNoteNameByIndex(ii/subToneCount)+
                "/"+ii%subToneCount+"="+String.format("%6.4f",vv);
            Tooltip.install(node, new Tooltip(ss));
            node.setOnMouseEntered(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    node.getStyleClass().add("onHover");
                    }
                });
            //Удаление класса после покидание мыши
            node.setOnMouseExited(new EventHandler<Event>() {
         	    @Override
          	    public void handle(Event event) {
                node.getStyleClass().remove("onHover");
                }});
                //--------------------------------------------------------------
            }
        }

    public void paintOneLinear(Graphic stat) {
        XYChart.Series series = new XYChart.Series();
        float data[] = stat.vals;
        series.setName(stat.title);
        ObservableList dd = series.getData();
        for (int j = 2; j < data.length/2; j++) {               // Первое тупо отсечь !!!!!!!!!!!!!!!!!!!!!
            int ff = (int)((j+1.0)*FFT.sizeHZ)/data.length;
            XYChart.Data<Integer, Double> item = new XYChart.Data<Integer, Double>(ff, (double)data[j]);
            dd.add(item);
            }
        lineChart.getData().add(series);
        //----------- Node появляются только после добавления серии !!!!!!!!!!!!
        for (int ii = 0; ii < dd.size(); ii++) {
            XYChart.Data<Integer, Double> item = (XYChart.Data<Integer, Double>) dd.get(ii);
            final Node node = item.getNode();
            final float vv = data[ii];
            final String x0 = ""+(int)((ii+1.0)*FFT.sizeHZ/data.length);
            //------------------ Наведение мыши ----------------------------
            Tooltip.install(node, new Tooltip(x0 +"=>"+ String.format("%6.4f",vv)));
            node.setOnMouseEntered(new EventHandler<Event>() {
                @Override
                public void handle(javafx.event.Event event) {
                    node.getStyleClass().add("onHover");
                    }
                });
            //Удаление класса после покидание мыши
            node.setOnMouseExited(new EventHandler<javafx.event.Event>() {
                @Override
                public void handle(Event event) {
                    node.getStyleClass().remove("onHover");
                }
            });
            //--------------------------------------------------------------
            }
        }

        public void addStatistic(final Graphic stat){
        addOrReplace(stat);
        createStatList();
        }

    public void createStatList(){
        StatList.removeAll();
        StatList.add("...");
        for(int i=data.size()-1;i>=0;i--)
            StatList.add(data.get(i).title);
        }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        StatList = new java.awt.Choice();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        StatList.setBackground(new java.awt.Color(204, 204, 204));
        getContentPane().add(StatList);
        StatList.setBounds(140, 10, 460, 30);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/remove.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(20, 10, 30, 30);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/clear.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(660, 10, 30, 30);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/up.PNG"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(100, 10, 30, 30);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/down.PNG"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(60, 10, 30, 30);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/refresh.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(610, 10, 40, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (back!=null)
            back.release(this);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int idx = StatList.getSelectedIndex();
        if (idx==0)
            return;
        final int idx2 = StatList.getItemCount() - idx - 1;
        data.remove(idx2);
        createStatList();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lineChart.getData().remove(idx2); 
                }
            });
    }//GEN-LAST:event_jButton1ActionPerformed

    public void clearAll(){
        final int sz = data.size();
        StatList.removeAll();
        StatList.add("...");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lineChart.getData().clear();
                }
            });
        }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        clearAll();
        data.removeAllElements();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        final int idx = StatList.getSelectedIndex();
        if (idx==0)
            return;
        final int idx2 = StatList.getItemCount() - idx - 1;
        final Graphic zz = data.remove(idx2);
        data.add(zz);
        createStatList();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Series<?,Number> xx = lineChart.getData().remove(idx2);
                paintOne(zz);
                //lineChart.getData().add(xx); - ТАК НЕ БЕРЕТ ПОВТОРНО
                }
            });
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       final int idx = StatList.getSelectedIndex();
        if (idx==0)
            return;
        final int idx2 = StatList.getItemCount() - idx - 1;
        final Graphic zz = data.remove(idx2);
        data.insertElementAt(zz,0);
        repaint();
    }//GEN-LAST:event_jButton4ActionPerformed
    
    public void repaint(){
        clearAll();
        createStatList(); 
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<data.size();i++){
                    Graphic zz = data.get(i);
                    paintOne(zz);
                    }
                }
            });
        }
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        clearAll();
        repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice StatList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    // End of variables declaration//GEN-END:variables

    @Override
    public void paint(float[] spikes, int subToneCount) {
        GBL.notSupport();
        }

    @Override
    public void paint(float[] spikes, FFTParams params){
        GBL.notSupport();
        }

    @Override
    public void paint(FFT fft) {
        GBL.notSupport();
        }
    @Override
    public void paint(float[] vals, String title) {
        addStatistic(new Graphic(title,vals));
        }
    @Override
    public void reset() {
        }
    @Override
    public void setCallBack(LayerWindowCallBack back) {
        this.back = back;
        }
}
