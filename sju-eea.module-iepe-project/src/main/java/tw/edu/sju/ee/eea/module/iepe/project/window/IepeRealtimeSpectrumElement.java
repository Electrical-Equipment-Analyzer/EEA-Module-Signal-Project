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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelsConfigure;
import tw.edu.sju.ee.eea.module.iepe.io.FourierTransformerOutputStreeam;
import tw.edu.sju.ee.eea.module.iepe.io.FrequencyOutput;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProjectProperties;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.ui.workspace.plot.BodePlot;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;

@MultiViewElement.Registration(
        displayName = "#LBL_IEPE_Realtime_Spectrum",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/iepe-realtime",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "IepeVisual",
        position = 3000
)
@Messages("LBL_IEPE_Realtime_Spectrum=Spectrum")
public final class IepeRealtimeSpectrumElement extends JPanel implements MultiViewElement, ChannelsConfigure {

    private IepeProjectProperties properties;
    private IepeRealtimeObject rt;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    public IepeRealtimeSpectrumElement(Lookup lkp) {
        this.rt = lkp.lookup(IepeRealtimeObject.class);
        assert rt != null;
        properties = lkp.lookup(IepeProject.class).getProperties();
        toolbar.setFloatable(false);

        ChannelList list = lkp.lookup(IepeProject.class).getList();
        IEPEInput iepe = lkp.lookup(IepeProject.class).getIepe();
        list.addConfigure(this);
        channels = new FrequencyChannel[list.size()];
        for (int i = 0; i < channels.length; i++) {
            Channel channel = list.get(i);
            channels[i] = new FrequencyChannel(channel.getName(), 4096);
            iepe.addStream(channel.getChannel(), channels[i]);
        }

        initComponents();

    }

    private class FrequencyChannel extends XYSeries implements IEPEInput.VoltageArrayOutout, FrequencyOutput {

        private FourierTransformerOutputStreeam stream;
        private int length;

        public FrequencyChannel(Comparable key, int length) {
            super(key);
            this.length = length;
            stream = new FourierTransformerOutputStreeam(this, length);
        }

        @Override
        public Number getX(int index) {
            try {
                return super.getX(index);
            } catch (IndexOutOfBoundsException ex) {
            } catch (NullPointerException ex) {
            }
            return null;
        }

        @Override
        public Number getY(int index) {
            try {
                return super.getY(index);
            } catch (IndexOutOfBoundsException ex) {
            } catch (NullPointerException ex) {
            }
            return null;
        }

        @Override
        public void writeFrequency(double frequencyBase, double[] value) throws IOException {
            this.clear();
            for (int i = 0; i < value.length; i++) {
                this.add(new XYDataItem(i * frequencyBase, value[i] / this.length * 2), false);
            }
        }

//        private double frequencyBase;
//        private double[] value;
//        @Override
//        public void run() {
//            while (!Thread.interrupted()) {
//                try {
//                    Thread.sleep(1000);
//                    synchronized (this) {
//                        this.wait();
//                    }
//                } catch (InterruptedException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//                this.clear();
//                for (int i = 0; i < value.length; i++) {
//                    this.add(i, value[i]);
//                }
//            }
//        }
        @Override
        public void writeVoltageArray(double[] data) throws IOException {
            for (double d : data) {
                stream.writeValue(d);
            }
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void flush() throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    @Override
    public void setChannelName(int channel, String name) {
        channels[channel].setKey(name);
    }

    @Override
    public void setChannelColor(int channel, Color color) {
        renderer.setSeriesPaint(channel, color);
    }

    @Override
    public Color getChannelColor(int channel) {
        return (Color) renderer.getSeriesPaint(channel);
    }

    XYSeriesCollection xySeriesCollection;
    private XYItemRenderer renderer;
    private FrequencyChannel[] channels;

    private JFreeChart createChart() {
        BodePlot chart = new BodePlot("Spectrum");
        chart.createAxisY(0, "Magnitude(Voltage)");

        xySeriesCollection = new XYSeriesCollection();
        renderer = BodePlot.creatrRenderer();

        chart.getXYPlot().setDataset(0, xySeriesCollection);
        chart.getXYPlot().mapDatasetToRangeAxis(0, 0);
        chart.getXYPlot().setRenderer(0, renderer);

        for (int i = 0; i < channels.length; i++) {
            xySeriesCollection.addSeries(channels[i]);
        }

        chart.getXYPlot().getRangeAxis().setRange(0, 10);
        chart.getXYPlot().getDomainAxis().setRange(0.5, 20000);
        return chart;
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
        return rt.getLookup();
    }

    @Override
    public void componentOpened() {
        Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.INFO, "open");
    }

    @Override
    public void componentClosed() {
        Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.INFO, "close");
    }

    @Override
    public void componentShowing() {
        Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.INFO, "Show");
    }

    @Override
    public void componentHidden() {
        Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.INFO, "hide");
    }

    @Override
    public void componentActivated() {
        Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.INFO, "active");
    }

    @Override
    public void componentDeactivated() {
        Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.INFO, "deact");
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
