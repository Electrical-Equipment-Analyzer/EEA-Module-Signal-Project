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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Calendar;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.time.Millisecond;
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
import tw.edu.sju.ee.eea.module.iepe.file.IepeCursorEvent;
import tw.edu.sju.ee.eea.module.iepe.file.IepeCursorListener;
import tw.edu.sju.ee.eea.module.iepe.file.IepeDataInfo;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;
import tw.edu.sju.ee.eea.util.iepe.io.SampledStream;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;

@MultiViewElement.Registration(
        displayName = "#LBL_Iepe_VISUAL",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/iepe-realtime",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "IepeVisual",
        position = 2000
)
@Messages("LBL_Iepe_VISUAL=Visual")
public final class IepeRealtimeVisualElement extends JPanel implements MultiViewElement {

    private class IepeVisualToolBar extends JToolBar {

        private JButton head;
        private JButton tail;
        private JButton zoomIn;
        private JButton zoomOut;

        public IepeVisualToolBar() {
            this.setFloatable(false);
            this.addSeparator();
            head = new JButton(new javax.swing.ImageIcon(getClass().getResource("/tw/edu/sju/ee/eea/module/iepe/file/iepe_visual_cursor_head.png")));
            head.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
//                    info.getCursor().setTime(0);
                }
            });
            this.add(head);
            tail = new JButton(new javax.swing.ImageIcon(getClass().getResource("/tw/edu/sju/ee/eea/module/iepe/file/iepe_visual_cursor_tail.png")));
            tail.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
//                    info.getCursor().setTime(total);
                }
            });
            this.add(tail);
            this.addSeparator();
            zoomIn = new JButton(new javax.swing.ImageIcon(getClass().getResource("/tw/edu/sju/ee/eea/module/iepe/file/iepe_visual_cursor_zoomIn.png")));
            zoomIn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    double tmp = cursor.getValue();
                    index += (int) ((tmp - index) / 2);
                    length /= 2;
                    scrollLength();
                }
            });
            this.add(zoomIn);
            zoomOut = new JButton(new javax.swing.ImageIcon(getClass().getResource("/tw/edu/sju/ee/eea/module/iepe/file/iepe_visual_cursor_zoomOut.png")));
            zoomOut.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    double tmp = cursor.getValue();
                    index -= (int) (tmp - index);
                    length *= 2;
                    scrollLength();
                }
            });
            this.add(zoomOut);

        }

    }

//    private IepeDataInfo info;
    private Lookup lkp;
    private IepeRealtimeObject rt;
    private JToolBar toolbar = new IepeVisualToolBar();
    private transient MultiViewElementCallback callback;
    private ValueMarker cursor;
    private boolean chartMouseClicked;
    private boolean chartScroll;
    private int index;
    private int length;
    private int total = 62500;

    private PipedOutputStream po;
    private PipedInputStream pi;

    public IepeRealtimeVisualElement(Lookup lkp) {
        this.lkp = lkp;
        this.rt = lkp.lookup(IepeRealtimeObject.class);
        assert rt != null;

        index = 0;
        length = 10000;

        cursor = new ValueMarker(0);
        cursor.setPaint(Color.black);
        initComponents();
        scrollBar.setMaximum(total);
        scrollLength();
        ((ChartPanel) chartPanel).addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                chartMouseClicked = true;
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
            }
        });
        scrollBar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (chartScroll) {
                    index = e.getAdjustable().getValue();
                    length = e.getAdjustable().getVisibleAmount();
                    repaintChart();
                }
            }
        });
//        info.getCursor().addIepeCursorListener(new IepeCursorListener() {
//
//            @Override
//            public void cursorMoved(IepeCursorEvent e) {
//                cursor.setValue(e.getTime());
//                double tmp = cursor.getValue() - index;
//                if (tmp < 0 || tmp > length) {
//                    index = (int) (cursor.getValue() - (length * 0.05));
//                    scrollIndex();
//                }
//            }
//        });
//        IepeWriter iepeWriter = new IepeWriter() {
//
//            @Override
//            public void write(double[] data) {
//                double tmp = 0;
//                double max = series.getMaxX();
//                for (int i = 0; i < data.length; i++) {
//                    tmp = Math.max(tmp, data[i]);
//                    if (i % 16 == 0) {
//                        series.add(max++, (i % 32 == 0 ? -tmp : tmp));
//                    }
//                }
//            }
//        };
//        rt.setScreen(iepeWriter);

        try {
            pi = new PipedInputStream(19200);
            po = new PipedOutputStream(pi);
            rt.setScreen(po);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                long t = Calendar.getInstance().getTimeInMillis();
                IepeInputStream vi = new IepeInputStream(pi);
                SampledStream sr = new SampledStream(vi, 1600);
                while (true) {
                    try {
                        series.add(t, sr.readSampled());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    t += 100;
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
                }
            }

        };
        t.start();
    }

    private XYSeries series;

    private XYSeriesCollection createSampledSeriesCollection(String name) {
        series = new XYSeries(name);
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);
        return collection;
    }

    public JFreeChart createChart() {
        SampledChart sampledChart = new SampledChart("PlotTitle");
        sampledChart.addData(0, createSampledSeriesCollection("Ch_0"));
        ValueAxis axis = sampledChart.getXYPlot().getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        return sampledChart;
    }

    private void scrollLength() {
        length = (length > total ? total : length);
        chartScroll = false;
        scrollBar.setVisibleAmount(length);
        scrollIndex();
    }

    private void scrollIndex() {
        index = (index > (total - length) ? total - length : index);
        index = (index < 0 ? 0 : index);
        chartScroll = false;
        scrollBar.setValue(index);
        repaintChart();
    }

    private void repaintChart() {
        JFreeChart chart = ((ChartPanel) chartPanel).getChart();
        chart = null;
        ((ChartPanel) chartPanel).setChart(createChart());
        chartScroll = true;
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

        scrollBar = new javax.swing.JScrollBar();
        chartPanel = new ChartPanel(createChart());

        scrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JScrollBar scrollBar;
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
        return lkp;
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
        callback.getTopComponent().setDisplayName(rt.getDisplayName());
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

}
