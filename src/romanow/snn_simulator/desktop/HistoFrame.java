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
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import romanow.snn_simulator.statistic.Histogramm;


/**
 *
 * @author romanow
 */
public class HistoFrame extends javax.swing.JFrame {
    private Vector<Histogramm> data = new Vector();
    private Color backColor = new Color(240,240,240);
    private Color foreColor = Color.BLACK;
    ScatterChart<Number, Number> lineChart;
    private void runFX(final Runnable code){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                code.run();
                }
            });
        }
    public HistoFrame() {
        initComponents();
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
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Интервал");
        lineChart =  new ScatterChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Распределение");
        Scene scene = new Scene(lineChart, 1100, 600);
        fxPanel.setScene(scene);
        }
    //--------------------------------------------------------------------------
    private String put6(double dd){ 
        String ss = ""+dd;
        if (ss.length()<4)
            return ss;
        return ss.substring(0,4);
        }    
    //--------------------------------------------------------------------------
    public void paintOne(Histogramm stat){
        XYChart.Series series = new XYChart.Series();
        final double data[] = stat.getCounts();
        series.setName(stat.getTitle());
        ObservableList dd = series.getData();
        for(int j=0;j<data.length;j++){
            XYChart.Data<Double, Double> item = new XYChart.Data<Double, Double>(stat.getX(j), data[j]);
            dd.add(item);
            }
        lineChart.getData().add(series);
        //----------- Node появляются только после добавления серии !!!!!!!!!!!!
        for(int ii=0;ii<dd.size();ii++){
            XYChart.Data<Double, Double> item = (XYChart.Data<Double, Double>)dd.get(ii);
            final Node node = item.getNode(); 
            final double vv = data[ii];
            //------------------ Наведение мыши ----------------------------
            String ss = put6(vv);
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

    public void addHisto(final Histogramm histo){
        data.add(histo);
        createStatList(); 
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                paintOne(histo);
                }
            });
        }

    public void createStatList(){
        StatList.removeAll();
        StatList.add("...");
        for(int i=data.size()-1;i>=0;i--)
            StatList.add(data.get(i).getTitle());
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

        addWindowListener(new java.awt.event.WindowAdapter() {
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
        this.hide();
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
                for(int i=0;i<sz;i++)
                lineChart.getData().remove(0); 
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
        final Histogramm zz = data.remove(idx2);
        data.add(zz);
        createStatList();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Series<Number,Number> xx = lineChart.getData().remove(idx2); 
                paintOne(zz);
                }
            });
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       final int idx = StatList.getSelectedIndex();
        if (idx==0)
            return;
        final int idx2 = StatList.getItemCount() - idx - 1;
        final Histogramm zz = data.remove(idx2);
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
                    Histogramm zz = data.get(i);
                    paintOne(zz);
                    }
                }
            });
        }
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(HistoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HistoFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice StatList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    // End of variables declaration//GEN-END:variables
}
