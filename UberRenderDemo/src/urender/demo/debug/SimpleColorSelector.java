/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urender.demo.debug;

import java.awt.Color;
import javax.swing.JColorChooser;
import org.joml.Vector3f;

public class SimpleColorSelector extends javax.swing.JPanel {

	private Vector3f color = new Vector3f();
	
	private ColorSelectionCallback callback = null;
	
	public SimpleColorSelector() {
		initComponents();
	}
	
	public void attachColor(Vector3f col){
		if (col == null) {
			col = new Vector3f();
		}
		this.color = col;
		colorPreview.setBackground(getAlphadColor(col));
	}
	
	public Vector3f getColor(){
		return color;
	}

	public void setCallback(ColorSelectionCallback cb){
		callback = cb;
	}
	
	private static Color getAlphadColor(Vector3f c) {
		return toColor(c);
	}
	
	private static Color toColor(Vector3f c) {
		return new Color(c.x, c.y, c.z, 1.0f);
	}
	
	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        colorPreview = new javax.swing.JLabel();
        btnSetColor = new javax.swing.JButton();

        colorPreview.setBackground(new java.awt.Color(255, 255, 255));
        colorPreview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        colorPreview.setOpaque(true);

        btnSetColor.setText("Set");
        btnSetColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetColorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(colorPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSetColor))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(colorPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSetColor, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSetColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetColorActionPerformed
        if (color != null) {
            Color c = JColorChooser.showDialog(null, "Pick a Color", toColor(color));
            if (c != null) {
                color.set(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
				colorPreview.setBackground(getAlphadColor(color));
				if (callback != null){
					callback.colorSelected(color);
				}
            }
        }
    }//GEN-LAST:event_btnSetColorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSetColor;
    private javax.swing.JLabel colorPreview;
    // End of variables declaration//GEN-END:variables
	
	public static interface ColorSelectionCallback {
		public void colorSelected(Vector3f color);
	}
}