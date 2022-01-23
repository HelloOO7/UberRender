package urender.demo.editor.uniform;

import javax.swing.text.NumberFormatter;
import org.joml.Vector4f;
import urender.engine.shader.UUniformVector4;

public class Vector4UniformEditor extends javax.swing.JPanel implements IUniformEditor<Vector4f, UUniformVector4> {

	public Vector4UniformEditor() {
		initComponents();
		((NumberFormatter) xField.getFormatter()).setValueClass(Float.class);
		((NumberFormatter) yField.getFormatter()).setValueClass(Float.class);
		((NumberFormatter) zField.getFormatter()).setValueClass(Float.class);
		((NumberFormatter) wField.getFormatter()).setValueClass(Float.class);
	}

	@Override
	public void load(UUniformVector4 uniform) {
		Vector4f vec = uniform.get();
		xField.setValue(vec.x);
		yField.setValue(vec.y);
		zField.setValue(vec.z);
		wField.setValue(vec.w);
	}

	@Override
	public void save(UUniformVector4 uniform) {
		Vector4f vec = uniform.get();
		vec.x = (Float) xField.getValue();
		vec.y = (Float) yField.getValue();
		vec.z = (Float) zField.getValue();
		vec.w = (Float) wField.getValue();
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xValueLabel = new javax.swing.JLabel();
        xField = new javax.swing.JFormattedTextField();
        yValueLabel = new javax.swing.JLabel();
        yField = new javax.swing.JFormattedTextField();
        zValueLabel = new javax.swing.JLabel();
        zField = new javax.swing.JFormattedTextField();
        wValueLabel = new javax.swing.JLabel();
        wField = new javax.swing.JFormattedTextField();

        xValueLabel.setForeground(new java.awt.Color(255, 0, 0));
        xValueLabel.setText("X");

        xField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));

        yValueLabel.setForeground(new java.awt.Color(0, 153, 0));
        yValueLabel.setText("Y");

        yField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));

        zValueLabel.setForeground(new java.awt.Color(0, 0, 204));
        zValueLabel.setText("Z");

        zField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));

        wValueLabel.setForeground(new java.awt.Color(204, 153, 0));
        wValueLabel.setText("W");

        wField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yValueLabel)
                    .addComponent(zValueLabel)
                    .addComponent(wValueLabel)
                    .addComponent(xValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xValueLabel)
                    .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yValueLabel)
                    .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zValueLabel)
                    .addComponent(zField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wValueLabel)
                    .addComponent(wField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField wField;
    private javax.swing.JLabel wValueLabel;
    private javax.swing.JFormattedTextField xField;
    private javax.swing.JLabel xValueLabel;
    private javax.swing.JFormattedTextField yField;
    private javax.swing.JLabel yValueLabel;
    private javax.swing.JFormattedTextField zField;
    private javax.swing.JLabel zValueLabel;
    // End of variables declaration//GEN-END:variables
}
