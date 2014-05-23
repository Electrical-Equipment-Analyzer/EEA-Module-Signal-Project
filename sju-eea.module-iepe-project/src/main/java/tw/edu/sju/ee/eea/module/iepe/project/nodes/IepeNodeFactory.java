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
package tw.edu.sju.ee.eea.module.iepe.project.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeAnalyzerObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeHistoryObject;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;

/**
 *
 * @author Leo
 */
@NodeFactory.Registration(projectType = "edu-sju-iepe", position = 10)
public class IepeNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project p) {
        assert p != null;
        return new IepesNodeList(p);
    }

    private class IepesNodeList implements NodeList<Node> {

        Project project;

        public IepesNodeList(Project project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            List<Node> list = new ArrayList<Node>();
//            list.add(new IepeRealtimeObject(project).createNodeDelegate());
//            list.add(new IepeHistoryObject(project).createNodeDelegate());
//            list.add(new IepeAnalyzerObject(project).createNodeDelegate());
            return list;
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }

    }
}
