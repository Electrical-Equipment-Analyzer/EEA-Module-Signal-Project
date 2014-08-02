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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Element;

import org.dom4j.Document;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;

/**
 *
 * @author Leo
 */
public class IepeHistoryObject implements IepeProject.Child, Runnable, Serializable, Lookup.Provider {

    private Lookup lkp;
    private Document doc;
    private Element conf;
    private Element pattern;
    private long interval;

    public IepeHistoryObject(IepeProject project) {
        this.lkp = new ProxyLookup(new Lookup[]{Lookups.singleton(project), Lookups.singleton(this)});
        doc = project.getDoc();

        Element root = doc.getRootElement();
        for (Iterator i = root.elementIterator("VALUE"); i.hasNext();) {
            Element foo = (Element) i.next();
            System.out.print("車牌號碼:" + foo.elementText("NO"));
            System.out.println("車主地址:" + foo.elementText("ADDR"));
        }

        conf = root.element("history");
        pattern = conf.element("pattern");
        System.out.println("----------------------------");
        System.out.println("item : " + pattern.getText());

        interval = 60000;
    }

    public Element getConf() {
        return conf;
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    public String getDisplayName() {
        return "History";
    }

    private String pattern(int channel) {
        return new SimpleDateFormat(pattern.getText()).format(Calendar.getInstance().getTime()).concat(".iepe")
                .replaceAll("channel", String.valueOf(channel));
    }

    @Override
    public void run() {
        IepeProject context = lkp.lookup(IepeProject.class);
        FileObject projectDirectory = context.getProjectDirectory();
        try {
            projectDirectory.createFolder("Record");
        } catch (IOException ex) {
            Logger.getLogger(IepeHistoryObject.class.getName()).log(Level.INFO, null, ex);
        }
        FileObject fileObject = projectDirectory.getFileObject("Record");

        try {
            IEPEInput.VoltageArrayOutout[] stream = new IEPEInput.VoltageArrayOutout[4];
            try {
                do {
                    try {
                        for (int i = 0; i < stream.length; i++) {
                            stream[i] = context.getIepe().replaceStream(i, stream[i], new IEPEInput.IepeOutputStream(
                                    fileObject.createAndOpen(pattern(i))));
                        }
                        Thread.sleep(this.interval);
                    } catch (IOException ex) {
                        Logger.getLogger(IepeHistoryObject.class.getName()).log(Level.INFO, null, ex);
                    }
                } while (!Thread.interrupted());
            } catch (InterruptedException ex) {
            }
            for (int i = 0; i < stream.length; i++) {
                context.getIepe().removeStream(i, stream[i]);
                stream[i].close();
            }
        } catch (IOException ex) {
            Logger.getLogger(IepeHistoryObject.class.getName()).log(Level.INFO, null, ex);
        }
    }

    TopComponent tc;

    public org.openide.nodes.Node createNodeDelegate() {
        return new AbstractNode(new ChannelChildren(lkp.lookup(IepeProject.class).getProjectDirectory().getFileObject("Record")), lkp) {

            @Override
            public Action getPreferredAction() {
                return new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (tc == null) {
                            tc = MultiViews.createMultiView("application/iepe-history", IepeHistoryObject.this);
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
                return "History";
            }

        };
    }

    public class ChannelChildren extends Children.Keys<FileObject> {

        private FileObject folder;

        public ChannelChildren(FileObject folder) {
            this.folder = folder;
        }

        @Override
        protected Node[] createNodes(FileObject key) {
            try {
                return new Node[]{DataObject.find(key).getNodeDelegate()};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            java.util.Map<String, FileObject> list = new TreeMap<String, FileObject>();
            FileObject[] children = folder.getChildren();
            for (FileObject fileObject : children) {
                list.put(fileObject.getName(), fileObject);
            }
            setKeys(list.values());
        }
    }
}
