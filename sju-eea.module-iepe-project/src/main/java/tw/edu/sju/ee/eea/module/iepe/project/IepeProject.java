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
package tw.edu.sju.ee.eea.module.iepe.project;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
//import org.w3c.dom.Document;
//import org.xml.sax.SAXException;
import tw.edu.sju.ee.eea.jni.mps.MPS140801IEPE;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeAnalyzerObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeHistoryObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;

/**
 *
 * @author Leo
 */
public class IepeProject implements Project {

    private final FileObject projectDirectory;
    private final ProjectState state;
    private Lookup lkp;
    private Child[] chield;
    private final IEPEInput iepe;
//    private File confFile;
//    private Document doc;
    private IepeProjectProperties properties;
    private ChannelList list;

    public IepeProject(FileObject projectDirectory, ProjectState state) {
        this.projectDirectory = projectDirectory;
        this.state = state;
        properties = new IepeProjectProperties(
                new File(projectDirectory.getFileObject(IepeProjectFactory.PROJECT_FILE).getPath()));

        iepe = new IEPEInput(new MPS140801IEPE(0, properties.device().getSampleRate()), new int[]{1}, 512);

//        confFile = new File(projectDirectory.getFileObject(IepeProjectFactory.PROJECT_FILE).getPath());
//        try {
//            doc = new SAXReader().read(confFile);
//        } catch (DocumentException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        list = new ChannelList();
        try {
            list.add(new Channel("USB", 0));
            list.add(new Channel("USB", 1));
            list.add(new Channel("USB", 2));
            list.add(new Channel("USB", 3));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public ChannelList getList() {
        return list;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDirectory;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                new Info(),
                new IepeProjectLogicalView(),
                new IepeCustomizerProvider(this)
            });
        }
        return lkp;
    }

    public Document getDoc() {
        return properties.doc();
    }

    public IepeProjectProperties getProperties() {
        return properties;
    }

    public void save() {
        properties.write();
    }

    public IEPEInput getIepe() {
        return iepe;
    }

    private final class Info implements ProjectInformation {

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png"));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        @Override
        public Project getProject() {
            return IepeProject.this;
        }
    }

    public class IepeProjectLogicalView implements LogicalViewProvider, Lookup.Provider {

        private Lookup lkp;

        public IepeProjectLogicalView() {
            if (chield == null) {
                chield = new Child[]{new IepeRealtimeObject(IepeProject.this),
                    new IepeHistoryObject(IepeProject.this),
                    new IepeAnalyzerObject(IepeProject.this)
                };
            }
            lkp = Lookups.fixed(chield);
        }

        @Override
        public Node createLogicalView() {
            try {
                //Obtain the project directory's node:
                FileObject projectDirectory = IepeProject.this.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
                //Decorate the project directory's node:
                return new ProjectNode(nodeOfProjectFolder);
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
                //Fallback-the directory couldn't be created -
                //read-only filesystem or something evil happened
                return new AbstractNode(Children.LEAF);
            }
        }

        @Override
        public Node findPath(Node root, Object target) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Lookup getLookup() {
            return lkp;
        }

        private final class ProjectNode extends FilterNode {

            public ProjectNode(Node node) throws DataObjectNotFoundException {
                super(node, new ProjectChildren(),
                        new ProxyLookup(new Lookup[]{Lookups.singleton(IepeProject.this), node.getLookup()}));
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[]{
                    CommonProjectActions.closeProjectAction(),
                    CommonProjectActions.customizeProjectAction()
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
                return IepeProject.this.getProjectDirectory().getName();
            }

        }

        public class ProjectChildren extends Children.Keys<Child> {

            private ProjectChildren() {
            }

            @Override
            protected Node[] createNodes(Child key) {
                return new Node[]{key.createNodeDelegate()};
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(chield);
            }

        }

    }

    public interface Child {

        public Node createNodeDelegate();
    }
}
