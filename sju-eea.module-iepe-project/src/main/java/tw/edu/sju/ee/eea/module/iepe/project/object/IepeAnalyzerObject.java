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
import org.dom4j.Document;
import org.dom4j.Element;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.data.AnalyzerRule;
import tw.edu.sju.ee.eea.module.iepe.project.data.Pattern;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;

/**
 *
 * @author Leo
 */
public class IepeAnalyzerObject implements IepeProject.Child, Serializable, Lookup.Provider, Runnable {

    private Lookup lkp;
    private Element conf;
    private AnalyzerRule[] rules;

    public IepeAnalyzerObject(IepeProject project) {
        this.lkp = new ProxyLookup(new Lookup[]{Lookups.singleton(project), Lookups.singleton(this)});
        Document doc = project.getDoc();
        Element root = doc.getRootElement();
        conf = root.element("analyzer");
        List elements = getConf().elements();
        rules = new AnalyzerRule[elements.size()];
        for (int i = 0; i < rules.length; i++) {
            rules[i] = new AnalyzerRule((Element) elements.get(i));
        }
    }

    public Element getConf() {
        return conf;
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

        IEPEInput.IepeStream[] stream = new IEPEInput.IepeStream[4];
        for (int i = 0; i < stream.length; i++) {
            try {
                stream[i] = (IEPEInput.IepeStream) project.getIepe().addStream(i, new IEPEInput.IepeStream());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        do {
            double[][] channels = new double[stream.length][1024];
            for (int i = 0; i < channels[0].length; i++) {
                for (int j = 0; j < channels.length; j++) {
                    try {
                        channels[j][i] = stream[j].readValue();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            Pattern pattern = new Pattern(16000, 1024, channels);
            pattern.rules(rules);
        } while (!Thread.interrupted());

        for (int i = 0; i < stream.length; i++) {
            project.getIepe().removeStream(i, stream[i]);
        }

    }

    TopComponent tc;

    @Override
    public Node createNodeDelegate() {
        return new AbstractNode(new AnalyzerChildren(), lkp) {

            @Override
            public Action getPreferredAction() {
                return new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (tc == null) {
                            tc = MultiViews.createMultiView("application/iepe-analyzer", IepeAnalyzerObject.this);
                        }
                        tc.open();
                        tc.requestActive();
                    }
                };
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

    public class AnalyzerChildren extends Children.Keys<Element> {

        public AnalyzerChildren() {
        }

        @Override
        protected Node[] createNodes(Element key) {
            return new Node[]{new AnalyzerRule(key).createNodeDelegate()};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(getConf().elements());
        }

    }
}
