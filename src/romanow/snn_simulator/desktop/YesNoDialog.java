package romanow.snn_simulator.desktop;

public class YesNoDialog extends javax.swing.JDialog {
    YesNoListener back;
    public YesNoDialog(java.awt.Frame parent, String title, YesNoListener lsn) {
        super(parent, true);
        initComponents();
        mes.setText(title);
        back=lsn;
        setBounds(500, 300, 350, 150);
        setVisible(true);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button1 = new javax.swing.JButton();
        button2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(null);

        button1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/remove.png"))); // NOI18N
        button1.setBorder(null);
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });
        getContentPane().add(button1);
        button1.setBounds(290, 80, 30, 30);

        button2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/add.png"))); // NOI18N
        button2.setBorder(null);
        button2.setBorderPainted(false);
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        getContentPane().add(button2);
        button2.setBounds(10, 80, 30, 33);

        mes.setEditable(false);
        mes.setBackground(new java.awt.Color(240, 240, 240));
        mes.setColumns(20);
        mes.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        mes.setLineWrap(true);
        mes.setRows(4);
        mes.setText("-----");
        mes.setToolTipText("");
        mes.setWrapStyleWord(true);
        mes.setBorder(null);
        jScrollPane1.setViewportView(mes);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 10, 310, 70);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

    }//GEN-LAST:event_formWindowClosed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        back.onYes();
        dispose();
    }//GEN-LAST:event_button2ActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        back.onNo();
        dispose();
    }//GEN-LAST:event_button1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button1;
    private javax.swing.JButton button2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mes;
    // End of variables declaration//GEN-END:variables
}
