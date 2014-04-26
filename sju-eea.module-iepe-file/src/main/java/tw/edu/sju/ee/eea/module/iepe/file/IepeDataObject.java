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
import org.jfree.chart.plot.ValueMarker;
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

    protected ValueMarker cursor;
    private long pos;
    private InputStream stream;

    public IepeDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("application/iepe", true);
        init();
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    private void init() {
        cursor = new ValueMarker(0);
        cursor.setPaint(Color.black);
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
        cursor.setValue(pos / 16 / 8);
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
