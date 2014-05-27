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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeHistoryObject;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledManager;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledSeries;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;
import tw.edu.sju.ee.eea.util.iepe.io.SampledStream;

@MultiViewElement.Registration(
        displayName = "#LBL_IEPE_VISUAL",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        mimeType = "application/iepe-history",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "IepeVisual",
        position = 2000
)
@Messages("LBL_IEPE_VISUAL=Visual")
public final class IepeHistoryVisualElement extends JPanel implements MultiViewElement {

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

        }

        @Override
        public void setEnabled(boolean b) {
            this.head.setEnabled(b);
            this.tail.setEnabled(b);
            this.zoomIn.setEnabled(b);
            this.zoomOut.setEnabled(b);
        }

    }

    private Lookup lkp;
    private IepeHistoryObject object;
    private JToolBar toolbar = new IepeVisualToolBar();
    private transient MultiViewElementCallback callback;

    private Date time;
    private SampledManager manager;

    public IepeHistoryVisualElement(Lookup lkp) {
        this.lkp = lkp;
        this.object = lkp.lookup(IepeHistoryObject.class);
        assert object != null;
        try {
            this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-05-27 15:15");
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

        manager = lkp.lookup(IepeProject.class).getList().createSampledManager(
                SampledChart.creatrRenderer(),
                Process.class,
                this
        );

        initComponents();
        toolbar.setEnabled(false);

        Iterator<Process> iterator = manager.getCollection().getSeries().iterator();
        while (iterator.hasNext()) {
            Process next = iterator.next();
            next.process();
        }
    }

    private InputStream getStream(int channel) throws FileNotFoundException {
//        new SimpleDateFormat(object.getConf().getText()).format(time);
        String format = "2014-05-27_0315_channel".concat(".iepe")
                .replaceAll("channel", String.valueOf(channel));
        System.out.println(format);
        FileObject fileObject = lkp.lookup(IepeProject.class).getProjectDirectory().getFileObject("Record").getFileObject(format);
        return fileObject.getInputStream();
    }

    public static class Process extends SampledSeries<IepeHistoryVisualElement> {

        private SampledStream sampled;

        public Process(Comparable key, int channel) throws IOException {
            super(key, channel);
        }

        @Override
        public void configure(IepeHistoryVisualElement conf) {
            try {
                sampled = new SampledStream(new IepeInputStream(conf.getStream(getChannel())), 1600);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private void process() {
            int i = 0;
            try {
                while (sampled.available() > 0) {
                    this.add(i, sampled.readSampled());
                    i += 100;
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    private JFreeChart createChart() {
        SampledChart sampledChart = new SampledChart("PlotTitle");
        sampledChart.addData(0, manager.getCollection(), manager.getRenderer());
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
        callback.getTopComponent().setDisplayName(object.getDisplayName());
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

}
