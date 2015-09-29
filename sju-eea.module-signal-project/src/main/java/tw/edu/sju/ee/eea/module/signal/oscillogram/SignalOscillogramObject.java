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
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
//import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.channel.FunctionChannel;
import tw.edu.sju.ee.eea.module.iepe.channel.ListManager;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProjectProperties;
import tw.edu.sju.ee.eea.module.signal.temp.Channel;
import tw.edu.sju.ee.eea.module.signal.io.ChannelList;

/**
 *
 * @author Leo
 */
public class SignalOscillogramObject extends AbstractNode implements ChannelList, NavigatorLookupHint, Serializable {

    
    public static final String MIMETYPE = "application/oscillogram";
    private IepeProject project;
//    private Lookup lkp;
//    private ChannelList list;
    private ArrayList<Channel> channels = new ArrayList<Channel>();
    TopComponent tc;

    public SignalOscillogramObject(Project project, String name) {
        super(Children.LEAF);
        this.project = (IepeProject) project;
        
        setName(name);
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }
    
    private void addDefault() {
        channels.add(new Channel("A", channels.size()));
        channels.add(new Channel("A", channels.size()));
        channels.add(new Channel("A", channels.size()));
    }

    public IepeProject getProject() {
        return project;
    }

    @Override
    public List<Channel> getChannels() {
        return channels;
    }

    @Override
    public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.add(new AbstractAction("Add Default") {

                @Override
                public void actionPerformed(ActionEvent e) {
                    addDefault();
                }
            });
            return actions.toArray(new AbstractAction[actions.size()]);
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
