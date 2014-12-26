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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.Format;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
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
import tw.edu.sju.ee.eea.core.math.MetricPrefixFormat;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelsConfigure;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProjectProperties;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;
import tw.edu.sju.ee.eea.utils.io.tools.EEAInput;
import tw.edu.sju.ee.eea.utils.io.ValueOutput;
import tw.edu.sju.ee.eea.core.math.SpinnerPreferredNumberModel;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeFunctionObject;
import tw.edu.sju.ee.eea.utils.io.ValueInput;
import tw.edu.sju.ee.eea.utils.io.ValueInputStream;
import tw.edu.sju.ee.eea.utils.io.ValueOutputStream;
import tw.edu.sju.ee.eea.utils.io.function.PlotInput;
import tw.edu.sju.ee.eea.utils.io.function.RevolutionsInputStream;
import tw.edu.sju.ee.eea.utils.io.function.RootMeanSquareInputStream;

@MultiViewElement.Registration(
        displayName = "#LBL_Oscilloscope_Function",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/function",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "FunctionVisual",
        position = 2000
)
@Messages("LBL_Oscilloscope_Function=Function Oscillogram")
public final class IepeFunctionElement extends JPanel implements MultiViewElement, ChannelsConfigure, Runnable {

    private class IepeVisualToolBar extends JToolBar {

        private JButton head;
        private JButton tail;
        private JButton zoomIn;
        private JButton zoomOut;

        private JLabel _label_horizontal;
        private Spinner _spinner_horizontal;
//        private JLabel _label_vertical;
//        private Spinner _spinner_vertical;

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
//                    double tmp = cursor.getValue();
//                    index += (int) ((tmp - index) / 2);
//                    length /= 2;
//                    scrollLength();
                }
            });
            this.add(zoomIn);
            zoomOut = new JButton(new javax.swing.ImageIcon(getClass().getResource("/tw/edu/sju/ee/eea/module/iepe/file/iepe_visual_cursor_zoomOut.png")));
            zoomOut.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
//                    double tmp = cursor.getValue();
//                    index -= (int) (tmp - index);
//                    length *= 2;
//                    scrollLength();
                }
            });
            this.add(zoomOut);

            //##################################################################
            this.addSeparator();
            _label_horizontal = new JLabel("horizontal");
            _label_horizontal.setBorder(new EmptyBorder(0, 10, 0, 10));
            _spinner_horizontal = new Spinner(new SpinnerPreferredNumberModel(0.000000001, 1000000));
            _spinner_horizontal.setFormat(new MetricPrefixFormat("0.###"));
            _spinner_horizontal.setWidth(60);
            _spinner_horizontal.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    t = (Double) _spinner_horizontal.getValue();
                    axis.setRange(0, t * 1000 * 1000);
                    for (int i = 0; i < channels.length; i++) {
                        channels[i].setLength(t);
                    }
                }
            });
            this.add(_label_horizontal);
            this.add(_spinner_horizontal);

//            _label_vertical = new JLabel("vertical");
//            _label_vertical.setBorder(new EmptyBorder(0, 10, 0, 10));
//            _spinner_vertical = new Spinner(new SpinnerPreferredNumberModel(0.000000001, 1000000));
//            _spinner_vertical.setFormat(new MetricPrefixFormat("0.###"));
//            _spinner_vertical.setWidth(60);
//            _spinner_vertical.addChangeListener(new ChangeListener() {
//
//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    System.out.println(_spinner_vertical.getValue());
////                    axis.setFixedAutoRange((Integer)_spinner_horizontal.getValue());
//                }
//            });
//            this.add(_label_vertical);
//            this.add(_spinner_vertical);
            //##################################################################
            this.addSeparator();

        }

        @Override
        public void setEnabled(boolean b) {
            this.head.setEnabled(b);
            this.tail.setEnabled(b);
            this.zoomIn.setEnabled(b);
            this.zoomOut.setEnabled(b);
        }

    }

    private IepeProjectProperties properties;
    private Lookup lkp;
    private IepeFunctionObject rt;
    private JToolBar toolbar = new IepeVisualToolBar();
    private transient MultiViewElementCallback callback;

    public IepeFunctionElement(Lookup lkp) {
        this.lkp = lkp;
        this.rt = lkp.lookup(IepeFunctionObject.class);
        assert rt != null;
        properties = lkp.lookup(IepeProject.class).getProperties();

        ChannelList list = lkp.lookup(IepeProject.class).getList();
        EEAInput[] iepe = lkp.lookup(IepeProject.class).getIepe();
        list.addConfigure(this);
        channels = new VoltageChannel[list.size()];
        for (int i = 0; i < channels.length; i++) {
            Channel channel = list.get(i);
            channels[i] = new VoltageChannel(channel.getName(), properties.device().getSampleRate());
            iepe[channel.getDevice()].getIOChannel(channel.getChannel()).addStream(channels[i]);
        }

        initComponents();
        toolbar.setEnabled(false);
        new Thread(this).start();
    }
    private double t = 1;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            for (int i = 0; i < channels.length; i++) {
                channels[i].update(t);
            }
            channels[0].setNotify(true);
            channels[0].setNotify(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class VoltageChannel extends XYSeries implements ValueOutput {

        private ValueOutputStream pipeOut;
        private ValueInputStream pipeIn;

        private PlotInput in;

        public VoltageChannel(Comparable key, int samplerate) {
            super(key);
            try {
                PipedInputStream pipe = new PipedInputStream((int) (properties.device().getSampleRate() * 32));
                pipeIn = new ValueInputStream(pipe);
                pipeOut = new ValueOutputStream(new PipedOutputStream(pipe));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            in = new RevolutionsInputStream(pipeIn, properties.device().getSampleRate(), 1000000);
            in.setLength(1);
            in.setInterval(100);
        }

        public void setLength(double t) {
            in.setLength(t);
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

        public double rms(double[] nums) {
            double ms = 0;
            for (int i = 0; i < nums.length; i++) {
                ms += nums[i] * nums[i];
            }
            ms /= nums.length;
            return Math.sqrt(ms);
        }

        public void update(double t) {
            int target = 1000;
            int unit = 1000000;
            this.clear();
            try {
                XYDataItem p;
                while ((p = in.readPlot()) != null) {
                    add(p);
                }
                while (pipeIn.available() > properties.device().getSampleRate()) {
                    pipeIn.skip(properties.device().getSampleRate());
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public void writeValue(double value) throws IOException {
            pipeOut.writeValue(value);
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

    private XYItemRenderer renderer;
    private VoltageChannel[] channels;

    private ValueAxis axis;

    private JFreeChart createChart() {

        SampledChart sampledChart = new SampledChart("Function Oscillogram");
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        renderer = SampledChart.creatrRenderer();

        sampledChart.getXYPlot().setDataset(0, xySeriesCollection);
        sampledChart.getXYPlot().mapDatasetToRangeAxis(0, 0);
        sampledChart.getXYPlot().setRenderer(0, renderer);

        for (int i = 0; i < channels.length; i++) {
            xySeriesCollection.addSeries(channels[i]);
        }

        axis = sampledChart.getXYPlot().getDomainAxis();
        axis.setAutoRange(true);
//        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis.setRange(0, t * 1000 * 1000);

        return sampledChart;
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

    private static class Spinner extends JSpinner {

        public Spinner(SpinnerNumberModel model) {
            super(model);
        }

        public void setWidth(int width) {
            Dimension maximumSize = getMaximumSize();
            maximumSize.width = width;
            setMaximumSize(maximumSize);
        }

        public void setFormat(Format format) {
            NumberFormatter formatter = (NumberFormatter) ((DefaultFormatterFactory) (((JSpinner.DefaultEditor) getEditor()).getTextField()).getFormatterFactory()).getDefaultFormatter();
            formatter.setFormat(format);
        }

    }

}
