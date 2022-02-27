package urender.demo;

import javax.swing.UIManager;
import urender.demo.debug.RTLI;
import urender.demo.perf.PerfMonitorWindow;

public class UDemoWindow extends javax.swing.JFrame {

	private PerfMonitorWindow perfMonitor = new PerfMonitorWindow();
	private RTLI realtimeLightInspector = new RTLI();
	
	/**
	 * Creates new form UDemoWindow
	 */
	public UDemoWindow() {
		initComponents();
		setLocationByPlatform(true);
		perfMonitor.setLocationByPlatform(true);
		perfMonitor.setVisible(true);
		perfMonitor.setAlwaysOnTop(true);
		realtimeLightInspector.setLocationByPlatform(true);
		realtimeLightInspector.setVisible(true);
		surface.bindPerfMonitor(perfMonitor);
		realtimeLightInspector.loadScene(surface.rootScene);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        surface = new urender.demo.GLJPanelDummy();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Demo");
        setLocationByPlatform(true);

        javax.swing.GroupLayout surfaceLayout = new javax.swing.GroupLayout(surface);
        surface.setLayout(surfaceLayout);
        surfaceLayout.setHorizontalGroup(
            surfaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 511, Short.MAX_VALUE)
        );
        surfaceLayout.setVerticalGroup(
            surfaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 338, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(surface, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(surface, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(UDemoWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new UDemoWindow().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private urender.demo.GLJPanelDummy surface;
    // End of variables declaration//GEN-END:variables
}
