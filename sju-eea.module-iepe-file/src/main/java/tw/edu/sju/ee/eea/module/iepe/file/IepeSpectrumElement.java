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
package tw.edu.sju.ee.eea.module.iepe.file;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import tw.edu.sju.ee.eea.ui.workspace.plot.BodePlot;
import tw.edu.sju.ee.eea.utils.io.ValueInputStream;

@MultiViewElement.Registration(
        displayName = "#LBL_Iepe_Spectrum",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/iepe",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "IepeVisual",
        position = 3000
)
@Messages("LBL_Iepe_Spectrum=Spectrum")
public final class IepeSpectrumElement extends JPanel implements MultiViewElement, Runnable {

    private IepeDataInfo info;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    public IepeSpectrumElement(Lookup lkp) {
        info = lkp.lookup(IepeDataInfo.class);
        assert info != null;
        toolbar.setFloatable(false);
        initComponents();
        new Thread(this).start();
        info.getCursor().addIepeCursorListener(new IepeCursorListener() {

            @Override
            public void cursorMoved(IepeCursorEvent e) {
                synchronized (IepeSpectrumElement.this) {
                    IepeSpectrumElement.this.notify();
                }
            }
        });
    }

    @Override
    public String getName() {
        return "IepeVisualElement";
    }

    @Override
    public void run() {
        System.out.println("start");
        while (true) {
            try {
                synchronized (IepeSpectrumElement.this) {
                    IepeSpectrumElement.this.wait();
                }
            } catch (InterruptedException ex) {
            }
            ((ChartPanel) chartPanel).setChart(createChart());
        }
    }

    private JFreeChart createChart() {
        XYSeries series = new XYSeries("Ch_0");

        try {
            ValueInputStream vi = new ValueInputStream(info.getInputStream());
            vi.skip(info.getCursor().getIndex() / 8);
            double[] value = new double[1024 * 16];
            for (int i = 0; i < value.length; i++) {
                value[i] = vi.readValue();
            }

            FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] data = ComplexUtils.convertToComplex(value);
            Complex[] transform = fft.transform(data, TransformType.FORWARD);
            int max = transform.length / 2 + 1;
            for (int i = 1; i < max; i++) {
                double f = (double) i * info.getSamplerate() / transform.length;
                series.add(f, transform[i].abs() / value.length * 2);
            }

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
        }

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);

        BodePlot bodePlot = new BodePlot("Spectrum");
        bodePlot.addData(0, "Magnitude(dB)", collection);
        bodePlot.getXYPlot().getRangeAxis().setRange(0, 500);
        bodePlot.getXYPlot().getDomainAxis().setRange(0.5, 10000);
        return bodePlot;
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
        return info.getLookup();
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
