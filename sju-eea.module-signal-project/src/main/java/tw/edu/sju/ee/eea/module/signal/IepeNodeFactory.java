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
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeAnalyzerObject;
import tw.edu.sju.ee.eea.module.signal.oscillogram.SignalOscillogramObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeHistoryObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;

/**
 *
 * @author D10307009
 */
@NodeFactory.Registration(projectType = "edu-ntust-ee305-eea-SignalProject", position = 200)
public class IepeNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project prjct) {
        if (prjct instanceof IepeProject) {
            IepeProject p = (IepeProject) prjct;
            return NodeFactorySupport.fixedNodeList(
//                    p.r.createNodeDelegate(),
//                    p.f.createNodeDelegate(),
                    p.h.createNodeDelegate(),
                    p.a.createNodeDelegate()
            );
        }
        AbstractNode nd = new AbstractNode(Children.LEAF);
        nd.setDisplayName("Hello World!");
        return NodeFactorySupport.fixedNodeList(nd);
    }

}
