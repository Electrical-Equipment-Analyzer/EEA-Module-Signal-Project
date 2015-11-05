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

import tw.edu.sju.ee.eea.module.signal.temp.SineInputStream;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.core.math.SineSimulator;
//import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.signal.device.DeviceChannel;
import tw.edu.sju.ee.eea.module.signal.device.SignalDeviceObject;
import tw.edu.sju.ee.eea.module.signal.temp.OscillogramChannel;
import tw.edu.sju.ee.eea.module.signal.io.ChannelList;
import tw.edu.sju.ee.eea.module.temp.ListenerList;
import tw.edu.sju.ee.eea.utils.io.ChannelInputStream;
import tw.edu.sju.ee.eea.utils.io.tools.DeviceInfo;

/**
 *
 * @author Leo
 */
public class SignalOscillogramObject extends AbstractNode implements ChannelList<OscillogramChannel>, NavigatorLookupHint, Serializable {

    public static final String MIMETYPE = "application/oscillogram";
    private IepeProject project;
//    private Lookup lkp;
//    private ChannelList list;
    private ListenerList<OscillogramChannel> channels = new ListenerList<OscillogramChannel>();
    TopComponent tc;
    private SignalRenderer renderer;

    public SignalOscillogramObject(Project project, String name, SignalRenderer renderer) {
        super(Children.LEAF);
        System.out.println("==========fffff");
        Collection<? extends Object> lookupAll = super.getLookup().lookupAll(Object.class);
        System.out.println(Arrays.toString(lookupAll.toArray()));
        this.project = (IepeProject) project;
        this.renderer = renderer;
        setName(name);
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }

    private void addDefault() {
        try {
            for (int i = 0; i < 3; i++) {
                ChannelInputStream ci = new ChannelInputStream(163840, i);
//            ((SignalDeviceObject) project.getDeviceList().get(0)).getChannels().get(0).getInput().addStream(c0);
                project.getDeviceList().get(0).getChannels().get(i).getInput().addStream(ci);
                channels.add(new OscillogramChannel(new DeviceInfo() {

                    @Override
                    public int getSamplerate() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String getDeviceName() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                }, i, renderer, ci));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
//        channels.add(new OscillogramChannel("A", channels.size(), renderer,
//                new SineInputStream(new SineSimulator(16384, 100, 5))));
//        channels.add(new OscillogramChannel("A", channels.size(), renderer,
//                new SineInputStream(new SineSimulator(16384, 200, 5))));
//        channels.add(new OscillogramChannel("A", channels.size(), renderer,
//                new SineInputStream(new SineSimulator(16384, 300, 5))));
    }

    public void add(DeviceChannel channel) {
        ChannelInputStream ci = null;
        try {
            ci = new ChannelInputStream(163840, 0);
            channel.getInput().addStream(ci);
            channels.add(new OscillogramChannel(channel.getInfo(), channel.getChannel(), renderer, ci));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public IepeProject getProject() {
        return project;
    }

    @Override
    public ListenerList<OscillogramChannel> getChannels() {
        return channels;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> oscActions = Utilities.actionsForPath("Actions/OSC");
        return oscActions.toArray(new Action[oscActions.size()]);
//        List<Action> actions = new ArrayList<Action>();
//        actions.add(new AbstractAction("Add Default") {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addDefault();
//            }
//        });
////        actions.add(new Pre);
//        return actions.toArray(new AbstractAction[actions.size()]);
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
