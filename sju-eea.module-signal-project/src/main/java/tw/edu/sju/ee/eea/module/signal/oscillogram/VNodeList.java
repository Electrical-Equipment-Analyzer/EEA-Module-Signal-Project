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
package tw.edu.sju.ee.eea.module.signal.oscillogram;

import java.util.ArrayList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import tw.edu.sju.ee.eea.module.signal.oscillogram.VNodeList.TestNode;

/**
 *
 * @author D10307009
 */
public class VNodeList extends ArrayList<Node> {

    public VNodeList(String... n) {
        for (int i = 0; i < n.length; i++) {
            add(new TestNode(n[i]));
        }
    }

    public static class TestNode extends AbstractNode {

        public TestNode(String name) {
            super(Children.LEAF);
            setDisplayName(name);
            setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
        }

    }

}
