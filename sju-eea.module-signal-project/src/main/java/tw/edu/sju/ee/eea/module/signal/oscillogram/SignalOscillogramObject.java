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
package tw.edu.sju.ee.eea.module.signal.oscillogram;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.channel.FunctionChannel;
import tw.edu.sju.ee.eea.module.iepe.channel.ListManager;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProjectProperties;

/**
 *
 * @author Leo
 */
//@NbBundle.Messages("MIME_TYPE_APPLICATION_OSCILLOGRAM=application/oscillogram")
public class SignalOscillogramObject extends AbstractNode implements ListManager, NavigatorLookupHint, Serializable {

    
    public static final String MIMETYPE = "application/oscillogram";
    private IepeProject project;
//    private Lookup lkp;
    private ChannelList list;
    TopComponent tc;

    public SignalOscillogramObject(Project project) {
        super(Children.LEAF);
//        super(Children.LEAF, Lookups.fixed(project, new IepeNavigatorHint()));

        this.project = (IepeProject) project;

        Lookup lkp = Lookups.fixed(project, this);
        IepeProjectProperties properties = this.project.getProperties();
        list = new ChannelList(lkp);
        try {
            for (int i = 0; i < properties.device().getChannels(); i++) {
                int device = i / 8;
                int channel = i % 8;
                list.add(new FunctionChannel(null, device, channel, lkp));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        setDisplayName("FFF");
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }

    public IepeProject getProject() {
        return project;
    }

    @Override
    public ChannelList getChannelList() {
        return list;
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tc == null) {
                    tc = MultiViews.createMultiView(MIMETYPE, SignalOscillogramObject.this);
                }
                tc.open();
                tc.requestActive();
            }
        };
    }

    @Override
    public String getContentType() {
        return MIMETYPE; // NOI18N
    }
}
