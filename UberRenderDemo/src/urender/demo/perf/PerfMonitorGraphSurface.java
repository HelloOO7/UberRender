package urender.demo.perf;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PerfMonitorGraphSurface extends javax.swing.JPanel {

	private List<Integer> values = new ArrayList<>();

	/**
	 * Creates new form PerfMonitorGraphiSurface
	 */
	public PerfMonitorGraphSurface() {
		initComponents();
	}

	public void update(int newValue) {
		values.add(newValue);
		int xDim = getWidth();

		while (values.size() > xDim) {
			values.remove(0);
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		int minimum = Integer.MAX_VALUE;
		int maximum = Integer.MIN_VALUE;
		g.clearRect(0, 0, getWidth(), getHeight());

		for (int v : values) {
			if (v < minimum) {
				minimum = v;
			}
			if (v > maximum) {
				maximum = v;
			}
		}
		
		g.setColor(Color.BLUE);
		
		for (int i = 1; i < values.size(); i++) {
			int lastValue = values.get(i - 1);
			int nowValue = values.get(i);
			
			g.drawLine(i - 1, getDrawValue(lastValue, minimum, maximum), i, getDrawValue(nowValue, minimum, maximum));
		}
	}
	
	private int getDrawValue(int v, int min, int max) {
		return getHeight() - 1 - (int) ((v - min) / (float)(max - min) * getHeight());
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 304, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 193, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
