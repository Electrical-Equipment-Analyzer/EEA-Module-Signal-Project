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

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.signal.oscillogram.VNodeList;

/**
 *
 * @author D10307009
 */
@NodeFactory.Registration(projectType = "edu-ntust-ee305-eea-SignalProject", position = 100)
@NbBundle.Messages("LBL_Si_AnalysisHistoryAction=Analysis History")
public class SignalNodeFactory implements NodeFactory {

    public static final String REGISTERED_NODE_LOCATION
            = "Projects/edu-ntust-ee305-eea-SignalProject/Nodes";

    @Override
    public NodeList<?> createNodes(Project project) {
        if (project instanceof IepeProject) {
            IepeProject prj = (IepeProject) project;
            SignalNode n1 = new SignalNode(Children.create(new SignalChildFactory(
                    new VNodeList("PXIslot1/NI-5105", "PXIslot2/NI-5105")), false), "Device");

//            VNodeList oscillogramList = new VNodeList("TimeDomain", "FrequencyDomain", "TimeMes");
//            oscillogramList.add(prj.f);
            SignalNode n2 = new SignalNode(Children.create(
                    new SignalChildFactory(prj.getOscillogramList()), false), "Oscillogram");

            return NodeFactorySupport.fixedNodeList(n1, n2);
        }
        return null;
    }

    public class SignalNode extends AbstractNode {

        public SignalNode(Children children, String name) {
            super(children);
            setDisplayName(name);
            setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
        }

    }

}
