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

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import tw.edu.sju.ee.eea.module.iepe.cookie.PlayStreamCookie;
import tw.edu.sju.ee.eea.ui.workspace.plot.BodePlot;
import tw.edu.sju.ee.eea.util.iepe.VoltageInputStream;

@Messages({
    "LBL_Iepe_LOADER=Files of Iepe"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Iepe_LOADER",
        mimeType = "application/iepe",
        extension = {"iepe"}
)
@DataObject.Registration(
        mimeType = "application/iepe",
        iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
        displayName = "#LBL_Iepe_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/application/iepe/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class IepeDataObject extends MultiDataObject implements PlayStreamCookie {

//    private long index;
//    private ValueMarker cursor;
    private IepeCursor cursor;
    int data_bytes = 8;
    private InputStream stream;
//    private ChartPanel bodePlot;

    public IepeDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("application/iepe", true);
        stream = getPrimaryFile().getInputStream();
        cursor = new IepeCursor();
        cursor.addIepeCursorListener(new IepeCursorListener() {

            @Override
            public void cursorMoved(IepeCursorEvent e) {
                if (e.getType() == IepeCursorEvent.MOVE) {
                    try {
                        stream = getPrimaryFile().getInputStream();
                        stream.skip(e.getIndex());
                    } catch (FileNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
//        init();
//        this.bodePlot = new ChartPanel(createChart());
//        Thread thread = new Thread() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    JFreeChart chart = bodePlot.getChart();
//                    chart = null;
//                    bodePlot.setChart(createChart());
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                }
//            }
//
//        };
//        thread.start();
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    IepeCursor getCursor() {
        return cursor;
    }

//    private void init() {
//        cursor = new ValueMarker(0);
//        cursor.setPaint(Color.black);
//    }
//    public ChartPanel getBodeplotPanel() {
//        return bodePlot;
//    }
//    protected ValueMarker getCursor() {
//        return cursor;
//    }
//    protected void setCursor(double value) {
//        cursor.setValue(value);
//        pos = (long) (value * 16 * 8);
//    }
//    void setIndex(long index) {
//        this.index = index;
//        try {
//            stream = getPrimaryFile().getInputStream();
//            stream.skip(index);
//        } catch (FileNotFoundException ex) {
//            Exceptions.printStackTrace(ex);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }
//    long getIndex() {
//        return this.index;
//    }
    @Override
    public void initStream() throws FileNotFoundException, IOException {
    }

    @Override
    public int readStream(byte[] b) throws IOException {
        cursor.increase(b.length);
        return stream.read(b);
    }

//    @MultiViewElement.Registration(
//            displayName = "#LBL_Iepe_EDITOR",
//            iconBase = "tw/edu/sju/ee/eea/module/iepe/file/iepe.png",
//            mimeType = "application/iepe",
//            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
//            preferredID = "Iepe",
//            position = 1000
//    )
//    @Messages("LBL_Iepe_EDITOR=Source")
//    public static MultiViewEditorElement createEditor(Lookup lkp) {
//        return new MultiViewEditorElement(lkp);
//    }
}
