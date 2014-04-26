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

    private long pos;
    private ValueMarker cursor;
    private InputStream stream;
    private ChartPanel bodePlot;

    public IepeDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("application/iepe", true);
        init();
        this.bodePlot = new ChartPanel(createChart());
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    bodePlot.setChart(createChart());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        };
        thread.start();
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    private void init() {
        cursor = new ValueMarker(0);
        cursor.setPaint(Color.black);
    }

    private JFreeChart createChart() {
        XYSeries series = new XYSeries("Ch_0");

        try {
            VoltageInputStream vi = new VoltageInputStream(getPrimaryFile().getInputStream());
            vi.skip(pos / 8);
            double[] value = new double[1024 * 16];
            for (int i = 0; i < value.length; i++) {
                value[i] = vi.readVoltage();
            }

//        double fre = cursor.getValue() /5;
//        System.out.println(fre);
//            double[] buf = new double[1024];
//            for (int x = 0; x < buf.length; x++) {
//            buf[x] = Math.sin(x / 16000.0 * 2 * Math.PI * fre) * 5;
//            }
            FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] data = ComplexUtils.convertToComplex(value);
            Complex[] transform = fft.transform(data, TransformType.FORWARD);
            int max = transform.length / 2 + 1;
            for (int i = 1; i < max; i++) {
                double f = i * 16000.0 / transform.length;
                series.add(f, transform[i].abs());
            }

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
        }

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);

        BodePlot bodePlot = new BodePlot("Ti");
        bodePlot.addData(0, "Magnitude(dB)", collection);
        bodePlot.getXYPlot().getRangeAxis().setRange(0, 500);
        bodePlot.getXYPlot().getDomainAxis().setRange(0.5, 10000);
        return bodePlot;
    }

    public ChartPanel getBodeplotPanel() {
        return bodePlot;
    }

    protected ValueMarker getCursor() {
        return cursor;
    }

    protected void setCursor(double value) {
        cursor.setValue(value);
        pos = (long) (value * 16 * 8);
    }

    @Override
    public void initStream() throws FileNotFoundException, IOException {
        pos = (long) (cursor.getValue() * 16 * 8);
        stream = getPrimaryFile().getInputStream();
        stream.skip(pos);
    }

    @Override
    public int readStream(byte[] b) throws IOException {
        pos += b.length;
        setCursor(pos / 16 / 8);
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
