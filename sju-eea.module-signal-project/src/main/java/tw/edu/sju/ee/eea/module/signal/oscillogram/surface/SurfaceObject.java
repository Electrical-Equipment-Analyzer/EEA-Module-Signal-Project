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
package tw.edu.sju.ee.eea.module.signal.oscillogram.surface;

import tw.edu.sju.ee.eea.module.signal.temp.SineInputStream;
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
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.core.math.SineSimulator;
//import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.signal.device.SignalDeviceObject;
import tw.edu.sju.ee.eea.module.signal.temp.OscillogramChannel;
import tw.edu.sju.ee.eea.module.signal.io.ChannelList;
import tw.edu.sju.ee.eea.module.signal.oscillogram.SignalRenderer;
import tw.edu.sju.ee.eea.utils.io.ChannelInputStream;

/**
 *
 * @author Leo
 */
public class SurfaceObject extends AbstractNode implements ChannelList<OscillogramChannel>, NavigatorLookupHint, Serializable {

    public static final String MIMETYPE = "application/soscillogram";
    private IepeProject project;
//    private Lookup lkp;
//    private ChannelList list;
    private ArrayList<OscillogramChannel> channels = new ArrayList<OscillogramChannel>();
    TopComponent tc;
    private SignalRenderer renderer;

    public SurfaceObject(Project project, String name, SignalRenderer renderer) {
        super(Children.LEAF);
        this.project = (IepeProject) project;
        this.renderer = renderer;
        setName(name);
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }

    private void addDefault() {
    }

    public IepeProject getProject() {
        return project;
    }

    @Override
    public List<OscillogramChannel> getChannels() {
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
                    tc = MultiViews.createMultiView(MIMETYPE, SurfaceObject.this);
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
