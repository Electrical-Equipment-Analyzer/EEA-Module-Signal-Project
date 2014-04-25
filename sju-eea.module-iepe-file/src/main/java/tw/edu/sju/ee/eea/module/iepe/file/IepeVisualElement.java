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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.io.VoltageInputStream;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;
import tw.edu.sju.ee.eea.ui.workspace.plot.BodePlot;

@MultiViewElement.Registration(
        displayName = "#LBL_Iepe_VISUAL",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/iepe",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "IepeVisual",
        position = 2000
)
@Messages("LBL_Iepe_VISUAL=Visual")
public final class IepeVisualElement extends JPanel implements MultiViewElement {

    private IepeDataObject obj;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    public IepeVisualElement(Lookup lkp) {
        obj = lkp.lookup(IepeDataObject.class);
        assert obj != null;
        initComponents();
    }

    public JFreeChart createChart() {
//        HashMap<Double,Complex> data = new HashMap();
//        data.put(0d, Complex.valueOf(1));
//        data.put(1d, Complex.valueOf(1));

        XYSeries series = new XYSeries("Ch_0");

        try {
            VoltageInputStream vi = new VoltageInputStream(obj.getPrimaryFile().getInputStream());
            for (int i = 0; i < 100000; i++) {
                double value = vi.readVoltage();
                vi.skip(8 * 15);
                series.add(i, value);
            }

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
        }

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);

        SampledChart sampledChart = new SampledChart("PlotTitle");
        sampledChart.addData(0, collection);

        final ValueMarker mark = new ValueMarker(500);
        mark.setPaint(Color.black);
        sampledChart.addMarker(mark);
        sampledChart.addProgressListener(new ChartProgressListener() {

            @Override
            public void chartProgress(ChartProgressEvent event) {
                if (event.getType() == ChartProgressEvent.DRAWING_FINISHED) {
                    double domainCrosshairValue = event.getChart().getXYPlot().getDomainCrosshairValue();
                    if (mark.getValue() != domainCrosshairValue) {
                        mark.setValue(domainCrosshairValue);
                    }
                }
            }
        });

        return sampledChart;
    }

    /**
     * Creates a chart.
     *
     * @param dataset a dataset.
     *
     * @return A chart.
     */
    private static JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Legal & General Unit Trust Prices", // title
                "Date", // x-axis label
                "Price Per Unit", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

        return chart;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private XYDataset createDataset() {
        TimeSeries s1 = new TimeSeries("L&G European Index Trust");
        try {
            VoltageInputStream vi = new VoltageInputStream(obj.getPrimaryFile().getInputStream());

            for (int i = 0; i < 10000; i++) {
                double value = vi.readVoltage();

                s1.add(new FixedMillisecond(i), value);
            }

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

//        s1.add(new Month(2, 2001), 181.8);
//        s1.add(new Month(3, 2001), 167.3);
//        s1.add(new Month(4, 2001), 153.8);
//        s1.add(new Month(5, 2001), 167.6);
//        s1.add(new Month(6, 2001), 158.8);
//        s1.add(new Month(7, 2001), 148.3);
//        s1.add(new Month(8, 2001), 153.9);
//        s1.add(new Month(9, 2001), 142.7);
//        s1.add(new Month(10, 2001), 123.2);
//        s1.add(new Month(11, 2001), 131.8);
//        s1.add(new Month(12, 2001), 139.6);
//        s1.add(new Month(1, 2002), 142.9);
//        s1.add(new Month(2, 2002), 138.7);
//        s1.add(new Month(3, 2002), 137.3);
//        s1.add(new Month(4, 2002), 143.9);
//        s1.add(new Month(5, 2002), 139.8);
//        s1.add(new Month(6, 2002), 137.0);
//        s1.add(new Month(7, 2002), 132.8);
        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);

        return dataset;

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
        return obj.getLookup();
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
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
