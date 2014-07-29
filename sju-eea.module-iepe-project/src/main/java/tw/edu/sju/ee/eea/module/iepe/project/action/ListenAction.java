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
import javax.swing.JOptionPane;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.IEPEPlayer;

@ActionID(
        category = "IEPE",
        id = "tw.edu.sju.ee.eea.module.iepe.project.object.ListenAction"
)
@ActionRegistration(
        displayName = "#CTL_ListenAction"
)
@ActionReference(path = "Menu/Analyzers", position = 400, separatorBefore = 350)
@Messages("CTL_ListenAction=Listen")
public final class ListenAction implements ActionListener {
    
    private final IepeProject context;
    private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
    private ProgressHandle progr;
    
    public ListenAction(IepeProject context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            // TODO use context
            String showInputDialog = JOptionPane.showInputDialog(null, "Channel :", "Select Channel to Listen", JOptionPane.INFORMATION_MESSAGE);
            final int channel = Integer.parseInt(showInputDialog);
            IEPEPlayer player = new IEPEPlayer(16000, 16, 1, 2, 16000);
            final IEPEInput.Stream stream = context.getIepe().addStream(channel, player.getOutputStream());
            RequestProcessor.Task task = RP.create(player);
            progr = ProgressHandleFactory.createHandle("Play task", task);
            task.addTaskListener(new TaskListener() {
                public void taskFinished(org.openide.util.Task task) {
                    System.out.println("fin");
                    context.getIepe().removeStream(channel, stream);
                    progr.finish();
                }
            });
            progr.start();
            task.schedule(0);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
