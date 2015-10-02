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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.dom4j.Document;
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
import tw.edu.sju.ee.eea.jni.mps.MPS140801;
import tw.edu.sju.ee.eea.module.iepe.channel.SourceChannel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeAnalyzerObject;
import tw.edu.sju.ee.eea.module.signal.oscillogram.SignalOscillogramObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeHistoryObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.module.signal.SignalProjectLogicalView;
import tw.edu.sju.ee.eea.module.signal.device.SignalDeviceObject;
import tw.edu.sju.ee.eea.module.signal.oscillogram.FourierTransformerRenderer;
import tw.edu.sju.ee.eea.module.signal.temp.VNodeList;
import tw.edu.sju.ee.eea.module.signal.oscillogram.ZoomRenderer;
import tw.edu.sju.ee.eea.module.temp.EmulatorDevice;
import tw.edu.sju.ee.eea.module.temp.MDESDevice;
import tw.edu.sju.ee.eea.module.temp.SerialDevice;
import tw.edu.sju.ee.eea.module.temp.TCPDevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAInput;

/**
 *
 * @author Leo
 */
public class IepeProject implements Project {

    private final FileObject projectDirectory;
    private final ProjectState state;
    private Lookup lkp;
    private Child[] chield;
    private final EEAInput[] input;
    private IepeProjectProperties properties;
//    private ChannelList list;

    private ArrayList<Node> deviceList = new ArrayList<Node>();
    private ArrayList<Node> oscillogramList = new ArrayList<Node>();

    public IepeProject(FileObject projectDirectory, ProjectState state) {
        this.projectDirectory = projectDirectory;
        this.state = state;
        properties = new IepeProjectProperties(
                new File(projectDirectory.getFileObject(IepeProjectFactory.PROJECT_FILE).getPath()));
//        List<IEPEDevice> devices = new ArrayList();
        input = new EEAInput[(int) Math.ceil(properties.device().getChannels() / 8.0)];
        for (int i = 0; i < Math.ceil(properties.device().getChannels() / 8.0); i++) {
//            devices.add(new MPS140801IEPE(i, properties.device().getSampleRate()));
//            input[i] = new EEAInput(new EmulatorDevice(), new int[]{1});
            input[i] = new EEAInput(new MPS140801(i, properties.device().getSampleRate()), new int[]{1});
//            input[i] = new EEAInput(new SerialDevice(properties.device().getDeviceName()), new int[]{1});
//            input[i] = new EEAInput(new MDESDevice(), new int[]{1});
        }
//        list = new ChannelList(lkp);
//        try {
//            for (int i = 0; i < properties.device().getChannels(); i++) {
//                int device = i / 8;
//                int channel = i % 8;
//                list.add(new SourceChannel(null, device, channel, lkp));
//            }
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }

        deviceList.add(new SignalDeviceObject(this));
        deviceList.add(new TestNode("tt"));

        oscillogramList.add(new SignalOscillogramObject(IepeProject.this, "TimeDomain", new ZoomRenderer()));
        oscillogramList.add(new SignalOscillogramObject(IepeProject.this, "FrequencyDomain", new FourierTransformerRenderer()));
        oscillogramList.add(new TestNode("TimeMes"));

        r = new IepeRealtimeObject(IepeProject.this);
        h = new IepeHistoryObject(IepeProject.this);
        a = new IepeAnalyzerObject(IepeProject.this);
    }

    public static class TestNode extends AbstractNode {

        public TestNode(String name) {
            super(Children.LEAF);
            setDisplayName(name);
            setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
        }

    }

    public List<Node> getDeviceList() {
        return deviceList;
    }

    public List<Node> getOscillogramList() {
        return oscillogramList;
    }

    public EEAInput[] getInput() {
        return input;
    }

//    public ChannelList getList() {
//        return list;
//    }
    @Override
    public FileObject getProjectDirectory() {
        return projectDirectory;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                new Info(),
                //                new IepeProjectLogicalView(),
                new SignalProjectLogicalView(this),
                new IepeCustomizerProvider(this)
            });
        }
        return lkp;
    }

    public IepeProjectProperties getProperties() {
        return properties;
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

    public IepeRealtimeObject r;
    public IepeHistoryObject h;
    public IepeAnalyzerObject a;

    public class IepeProjectLogicalView implements LogicalViewProvider, Lookup.Provider {

        private Lookup lkp;

        public IepeProjectLogicalView() {
            if (chield == null) {
                chield = new Child[]{
                    new IepeRealtimeObject(IepeProject.this),
                    //                    new IepeFunctionObject(IepeProject.this),
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
