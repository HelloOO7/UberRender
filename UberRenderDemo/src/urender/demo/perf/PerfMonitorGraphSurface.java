package urender.demo.perf;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PerfMonitorGraphSurface extends javax.swing.JPanel {

	private String graphName;
	private Color graphColor = Color.BLUE;

	private List<Integer> values = new ArrayList<>();

	/**
	 * Creates new form PerfMonitorGraphiSurface
	 */
	public PerfMonitorGraphSurface() {
		initComponents();
	}

	public void setGraphName(String name) {
		this.graphName = name;
	}

	public void setGraphColor(Color color) {
		this.graphColor = color;
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
		g.setColor(graphColor);
		if (graphName != null) {
			g.drawString(graphName, (getWidth() - g.getFontMetrics().stringWidth(graphName)) >> 1, 2 + g.getFont().getSize());
		}

		for (int v : values) {
			if (v < minimum) {
				minimum = v;
			}
			if (v > maximum) {
				maximum = v;
			}
		}
		
		int valueRange = maximum - minimum;
		int valueStepPixels = g.getFont().getSize() * 2;
		int valueStepUnits = (int) Math.ceil(valueStepPixels * (valueRange / (float) getHeight()));
		
		if (valueStepUnits == 0) {
			valueStepUnits = 1;
		}
		
		for (int labelValue = minimum; labelValue <= maximum; labelValue += valueStepUnits) {
			g.drawString(String.valueOf(labelValue), 1, getDrawValue(labelValue, minimum, maximum));
		}

		for (int i = 10; i < values.size(); i++) {
			int lastValue = values.get(i - 1);
			int nowValue = values.get(i);

			g.drawLine(i - 1, getDrawValue(lastValue, minimum, maximum), i, getDrawValue(nowValue, minimum, maximum));
		}
	}

	private int getDrawValue(int v, int min, int max) {
		return getHeight() - 1 - (int) ((v - min) / (float) (max - min) * (getHeight() - 1));
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this
	 * code. The content of this method is always regenerated by the Form Editor.
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
