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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
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
import org.omg.CORBA.portable.ValueOutputStream;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.SourceChannel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelsConfigure;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProjectProperties;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.ui.workspace.plot.BodePlot;
import tw.edu.sju.ee.eea.utils.io.function.FourierTransformerOutputStreeam;
import tw.edu.sju.ee.eea.utils.io.function.FrequencyOutput;
import tw.edu.sju.ee.eea.utils.io.ValueOutput;
import tw.edu.sju.ee.eea.utils.io.tools.EEAInput;

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
        IepeProject project = lkp.lookup(IepeProject.class);
        properties = project.getProperties();
        toolbar.setFloatable(false);

        ChannelList list = rt.getChannelList();
        EEAInput[] iepe = lkp.lookup(IepeProject.class).getInput();
        list.addConfigure(this);
        channels = new FrequencyChannel[list.size()];
        for (int i = 0; i < channels.length; i++) {
            Channel channel = list.get(i);
            channels[i] = new FrequencyChannel(channel.getName(), properties.device().getSampleRate(), 128);
            iepe[channel.getDevice()].getIOChannel(channel.getChannel()).addStream(channels[i]);
        }

        initComponents();
        initfx();

    }

    private class FrequencyChannel extends XYSeries implements ValueOutput, FrequencyOutput {

        private FourierTransformerOutputStreeam stream;
//        private DataOutputStream d;
        private int length;

        public FrequencyChannel(Comparable key, int samplerate, int length) {
            super(key);
            this.length = length;
            stream = new FourierTransformerOutputStreeam(this, samplerate, length);
//            d = = new DataOutputStream(stream);
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
        public void writeValue(double value) throws IOException {
            stream.writeValue(value);
        }

        @Override
        public void write(int b) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(byte[] b) throws IOException {
            stream.writeValue(WIDTH);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
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

        chart.getXYPlot().getRangeAxis().setRange(0, 1);
        chart.getXYPlot().getDomainAxis().setRange(0.5, 20000);
        return chart;
    }

    private  void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
        
        //-- Prepare Executor Services
        executor = Executors.newCachedThreadPool();
        addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }
    
    private static final int MAX_DATA_POINTS = 50;

    private XYChart.Series series;
    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> dataQ = new ConcurrentLinkedQueue<Number>();
    private ExecutorService executor;
    private AddToQueue addToQueue;
    private Timeline timeline2;
    private NumberAxis xAxis;

    private  Scene createScene() {
        
        xAxis = new NumberAxis(0,MAX_DATA_POINTS,MAX_DATA_POINTS/10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);

        //-- Chart
        final LineChart<Number, Number> sc = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(XYChart.Series<Number, Number> series, int itemIndex, XYChart.Data<Number, Number> item) {}
        };
        sc.setAnimated(false);
        sc.setId("liveAreaChart");
        sc.setTitle("Animated Area Chart");

        //-- Chart Series
        series = new LineChart.Series<Number, Number>();
        series.setName("Area Chart Series");
        sc.getData().add(series);

        return new Scene(sc);
    }

    private class AddToQueue implements Runnable {
        public void run() {
            try {
                // add a item of random data to queue
                dataQ.add(Math.random());
                Thread.sleep(50);
                executor.execute(this);
            } catch (InterruptedException ex) {
                Logger.getLogger(IepeRealtimeSpectrumElement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataQ.isEmpty()) break;
            series.getData().add(new AreaChart.Data(xSeriesData++, dataQ.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        // update 
        xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData-1);
    }

    private void initfx() {
        createChart();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(jFXPanel1);

            }
        });
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

        jFXPanel1 = new javafx.embed.swing.JFXPanel();

        setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jFXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jFXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javafx.embed.swing.JFXPanel jFXPanel1;
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
