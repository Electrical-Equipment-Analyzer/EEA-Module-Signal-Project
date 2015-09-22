/*
 * Copyright (C) 2015 D10307009
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
package tw.edu.sju.ee.eea.module.signal;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;

/**
 *
 * @author D10307009
 */
public class SignalProjectLogicalView implements LogicalViewProvider {

    private final IepeProject project;

    public SignalProjectLogicalView(IepeProject project) {
        this.project = project;
    }

    @Override
    public Node createLogicalView() {
        return new RootNode(project);
    }

    private static final class RootNode extends AbstractNode {
        
        public static final String DEMO_PROJECT_ICON_PATH =
                "tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png";

        public static final String REGISTERED_NODE_LOCATION =
                "Projects/edu-ntust-ee305-eea-SignalProject/Nodes";

        final IepeProject project;

        public RootNode(IepeProject project) {
            super(NodeFactorySupport.createCompositeChildren
                    (project, REGISTERED_NODE_LOCATION));
            this.project = project;
            setIconBaseWithExtension(DEMO_PROJECT_ICON_PATH);
        }

        @Override
        public Action[] getActions(boolean arg0) {
            Action[] nodeActions = new Action[7];
            nodeActions[0] = CommonProjectActions.newFileAction();
            nodeActions[1] = CommonProjectActions.copyProjectAction();
            nodeActions[2] = CommonProjectActions.deleteProjectAction();
            nodeActions[5] = CommonProjectActions.setAsMainProjectAction();
            nodeActions[6] = CommonProjectActions.closeProjectAction();
            return nodeActions;
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(DEMO_PROJECT_ICON_PATH);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getDisplayName() {
            return project.getProjectDirectory().getName();
        }
    }

    @Override
    public Node findPath(Node root, Object target) {
        //leave unimplemented for now
        return null;
    }
    
}
