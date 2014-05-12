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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
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
import tw.edu.sju.ee.eea.jni.mps.MPS140801IEPE;
import tw.edu.sju.ee.eea.util.iepe.IEPEException;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.IEPEPlayer;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;

/**
 *
 * @author Leo
 */
public class IepeProject implements Project {

    private final FileObject projectDirectory;
    private final ProjectState state;
    private Lookup lkp;
    private final IEPEInput iepe;
//    private OutputStream realtime;
//    private OutputStream player;

    public IepeProject(FileObject projectDirectory, ProjectState state) {
        this.projectDirectory = projectDirectory;
        this.state = state;

        iepe = new IEPEInput(new MPS140801IEPE(0, 16000), new int[]{1}, 512);

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

//    public void setRealtime(OutputStream realtime) {
//        this.realtime = realtime;
//    }
//
//    public void setPlayer(OutputStream player) {
//        this.player = player;
//    }
    public IEPEInput getIepe() {
        return iepe;
    }

    
    
    
//    public void run() {
//        try {
//            IEPEInput.IepeStream iepeStream = new IEPEInput.IepeStream();
//            iepe.addStream(1, iepeStream);
//            iepe.start();
//            while (!Thread.interrupted()) {
//                try {
//                    while (iepeStream.available() < 128) {
//                        Thread.yield();
//                    }
//                    byte[] buffer = new byte[128];
//                    iepeStream.read(buffer);
//                    this.realtime.write(buffer);
//                    try {
//                        this.player.write(buffer);
//                    } catch (NullPointerException ex) {
//                    }
////                System.out.println(Arrays.toString(buffer));
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//            iepe.stop();
//            System.out.println("stop");
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        } finally {
//        }
//    }

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

    public class IepeProjectLogicalView implements LogicalViewProvider {

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

        private final class ProjectNode extends FilterNode {

            public ProjectNode(Node node) throws DataObjectNotFoundException {
                super(node, NodeFactorySupport.createCompositeChildren(
                        IepeProject.this,
                        "Projects/edu-sju-iepe/Nodes"),
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

    }
}
