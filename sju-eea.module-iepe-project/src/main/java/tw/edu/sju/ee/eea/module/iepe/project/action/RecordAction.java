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
package tw.edu.sju.ee.eea.module.iepe.project.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;

@ActionID(
        category = "IEPE",
        id = "tw.edu.sju.ee.eea.module.iepe.project.action.RecordAction"
)
@ActionRegistration(
        displayName = "#CTL_RecordAction"
)
@ActionReference(path = "Menu/Analyzers", position = 600)
@Messages("CTL_RecordAction=RecordIEPE")
public final class RecordAction implements ActionListener, Runnable {

    private final IepeProject context;
    private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
    private ProgressHandle progr;

    public RecordAction(IepeProject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
        RequestProcessor.Task task = RP.create(this);
        progr = ProgressHandleFactory.createHandle("Record task", task);
        task.addTaskListener(new TaskListener() {
            public void taskFinished(org.openide.util.Task task) {
                System.out.println("record task finished");
                progr.finish();
            }
        });
        progr.start();
        task.schedule(0);
    }

    @Override
    public void run() {
        FileObject projectDirectory = context.getProjectDirectory();
        try {
            projectDirectory.createFolder("Record");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        FileObject fileObject = projectDirectory.getFileObject("Record");
        try {
            FileObject createData = fileObject.createData("aa.iepe");
            OutputStream outputStream = createData.getOutputStream();
//            IEPEInput.IepeStream iepeStream = new IEPEInput.IepeStream();
            IEPEInput.Stream stream = context.getIepe().addStream(0, outputStream);
            while (!Thread.interrupted()) {
                
            }
            context.getIepe().removeStream(0, stream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
