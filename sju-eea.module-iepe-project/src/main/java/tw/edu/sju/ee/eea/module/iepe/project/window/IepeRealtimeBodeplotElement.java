/*
 * Copyright (C) 2014 Leo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package tw.edu.sju.ee.eea.module.iepe.project.window;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.file.IepeBodeplotElement;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledManager;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledSeries;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;
import tw.edu.sju.ee.eea.ui.workspace.plot.BodePlot;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;
import tw.edu.sju.ee.eea.util.iepe.io.SampledStream;

@MultiViewElement.Registration(
        displayName = "#LBL_Iepe_BodePlot",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/iepe-realtime",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "IepeVisual",
        position = 3000
)
@Messages("LBL_Iepe_BodePlot=BodePlot")
public final class IepeRealtimeBodeplotElement extends JPanel implements MultiViewElement, Runnable {

    private IepeRealtimeObject rt;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    private SampledManager manager;

    public IepeRealtimeBodeplotElement(Lookup lkp) {
        this.rt = lkp.lookup(IepeRealtimeObject.class);
        assert rt != null;
        toolbar.setFloatable(false);

        manager = rt.getList().createSampledManager(
                lkp.lookup(IepeProject.class).getIepe(),
                BodePlot.creatrRenderer(),
                Process.class
        );

        initComponents();

        Thread t = new Thread(this);
        t.start();
    }

    public static class Process extends SampledSeries implements Runnable {

        private static final FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        private double[] value = new double[1024 * 4];
        private Thread thread;

        public Process(Comparable key) throws IOException {
            super(key);
            this.thread = new Thread(this);
            this.thread.start();
        }

        private void process() {
            try {
                for (int i = 0; i < value.length; i++) {
                    value[i] = stream.readValue();
                }
                synchronized (this) {
                    this.notify();
                }
            } catch (IOException ex) {
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                this.clear();
                Complex[] transform = fft.transform(value, TransformType.FORWARD);
                int max = transform.length / 2 + 1;
                for (int i = 1; i < max; i++) {
                    double f = i * 16000.0 / transform.length;
                    this.add(f, transform[i].abs());
                }
            }
        }

    }

    @Override
    public void run() {
        while (true) {
            Iterator<Process> iterator = manager.getCollection().getSeries().iterator();
            while (iterator.hasNext()) {
                Process next = iterator.next();
                next.process();
            }
        }
    }

    private JFreeChart createChart() {
        BodePlot bodePlot = new BodePlot("FFT PlotTitle");
        bodePlot.createAxisY(0, "Magnitude(Voltage)");
        bodePlot.addData(0, manager.getCollection(), manager.getRenderer());
        bodePlot.getXYPlot().getRangeAxis().setRange(0, 500);
        bodePlot.getXYPlot().getDomainAxis().setRange(0.5, 10000);
        return bodePlot;
    }

    @Override
    public String getName() {
        return "IepeVisualElement";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chartPanel = new ChartPanel(createChart());

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public JComponent getVisualRepresentation() {
        System.out.println("getVisual");
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return rt.getLookup();
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
        System.out.println("Show");
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

}
