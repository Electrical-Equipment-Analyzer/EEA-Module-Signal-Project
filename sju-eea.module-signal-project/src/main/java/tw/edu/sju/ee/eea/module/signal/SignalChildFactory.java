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

import tw.edu.sju.ee.eea.module.temp.ListenerList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author D10307009
 */
public class SignalChildFactory<N extends Node> extends ChildFactory<Node> implements ListDataListener {

    private ListenerList<N> list;

    SignalChildFactory(ListenerList<N> list) {
        this.list = list;
        list.addChangeListener(this);
    }

    @Override
    protected Node createNodeForKey(Node key) {
        return key;
    }

    @Override
    protected boolean createKeys(List<Node> list) {
        System.out.println("ck");
        for (Node node : this.list) {
            list.add(node);
        }
        return true;
    }

//    public void u() {
//        refresh(true);
//    }
    @Override
    public void intervalAdded(ListDataEvent e) {

        refresh(true);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        refresh(true);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        refresh(true);
    }

}
