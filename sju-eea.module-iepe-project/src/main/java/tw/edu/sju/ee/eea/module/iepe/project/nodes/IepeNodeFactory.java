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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import tw.edu.sju.ee.eea.module.iepe.file.IepeDataObject;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
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
        return new TextsNodeList(p);
    }

    private class TextsNodeList implements NodeList<Node> {

        Project project;

        public TextsNodeList(Project project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject textsFolder = project.getProjectDirectory().getFileObject("texts");
            List<Node> result = new ArrayList<Node>();
            if (textsFolder != null) {
//                Node node = new FilterNode(Node.EMPTY, Children.LEAF, new ProxyLookup(new Lookup[]{Lookups.singleton(project)}));
//                node.setName("aaa");
//                result.add(node);
                result.add(new IepeRealtimeObject(project).createNodeDelegate());
                for (FileObject textsFolderFile : textsFolder.getChildren()) {
                    try {
                        result.add(DataObject.find(textsFolderFile).getNodeDelegate());
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            return result;
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
