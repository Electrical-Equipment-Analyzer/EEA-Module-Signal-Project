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
package tw.edu.sju.ee.eea.module.iepe.project.object;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProjectProperties;
import tw.edu.sju.ee.eea.module.iepe.project.data.AnalyzerRule;
import tw.edu.sju.ee.eea.module.iepe.project.data.Pattern;
import tw.edu.sju.ee.eea.module.iepe.project.data.Warning;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;

/**
 *
 * @author Leo
 */
public class IepeAnalyzerObject implements IepeProject.Child, Serializable, Lookup.Provider, Runnable {

    private Lookup lkp;
    private IepeProjectProperties properties;
    private IEPEInput iepe;
    private ChannelList list;

    public IepeAnalyzerObject(IepeProject project) {
        this.lkp = new ProxyLookup(new Lookup[]{Lookups.singleton(project), Lookups.singleton(this)});
        properties = project.getProperties();
        list = lkp.lookup(IepeProject.class).getList();
        iepe = lkp.lookup(IepeProject.class).getIepe();
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    public String getDisplayName() {
        return "Analyzer";
    }

    @Override
    public void run() {
        IepeProject project = lkp.lookup(IepeProject.class);

        IEPEInput.IepePipeStream[] stream = new IEPEInput.IepePipeStream[list.size()];
        for (int i = 0; i < stream.length; i++) {
            Channel channel = list.get(i);
            try {
                stream[i] = (IEPEInput.IepePipeStream) iepe.addStream(channel.getDevice(), channel.getChannel(), new IEPEInput.IepePipeStream());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        InputOutput io = IOProvider.getDefault().getIO(this.getDisplayName(), false);
        do {
            try {
                double[][] channels = new double[stream.length][1024];
                for (int i = 0; i < channels[0].length; i++) {
                    for (int j = 0; j < channels.length; j++) {
                        channels[j][i] = stream[j].readValue();
                    }
                }
                Pattern pattern = new Pattern(properties.device().getSampleRate(), 1024, channels);
                List<Warning> list = pattern.rules(properties.rules());
                for (Warning warning : list) {
                    warning.print(io);
                }
            } catch (IOException ex) {
            }
        } while (!Thread.interrupted());

        for (int i = 0; i < stream.length; i++) {
            Channel channel = list.get(i);
            project.getIepe().removeStream(channel.getDevice(), channel.getChannel(), stream[i]);
        }

    }

    TopComponent tc;

    @Override
    public Node createNodeDelegate() {
        return new AbstractNode(new AnalyzerChildren(), lkp) {

//            @Override
//            public Action getPreferredAction() {
//                return new AbstractAction() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        if (tc == null) {
//                            tc = MultiViews.createMultiView("application/iepe-analyzer", IepeAnalyzerObject.this);
//                        }
//                        tc.open();
//                        tc.requestActive();
//                    }
//                };
//            }
            @Override
            public Action[] getActions(boolean popup) {
                return new Action[]{new AbstractAction("Add Rule") {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AnalyzerRule analyzerRule = properties.createRule();
                        setChildren(new AnalyzerChildren());
                    }
                }};
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return "Analyzer";
            }

        };
    }

    public class AnalyzerChildren extends Children.Keys<AnalyzerRule> {

        public AnalyzerChildren() {
        }

        @Override
        protected Node[] createNodes(AnalyzerRule key) {
            return new Node[]{key.createNodeDelegate()};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(properties.rules());
        }

    }
}
