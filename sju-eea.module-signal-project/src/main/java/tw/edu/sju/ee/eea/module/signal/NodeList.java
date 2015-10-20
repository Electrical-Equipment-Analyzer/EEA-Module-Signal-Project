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

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Node;

/**
 *
 * @author D10307009
 */
public class NodeList<N extends Node> extends ArrayList<N> {

    private List<ChangeListener> listener = new ArrayList<ChangeListener>();

    public boolean addChangeListener(ChangeListener listener) {
        return this.listener.add(listener);
    }

    public boolean removeChangeListener(ChangeListener listener) {
        return this.listener.remove(listener);
    }

    private void changed(Object source) {
        for (ChangeListener listener : this.listener) {
            listener.stateChanged(new ChangeEvent(source));
        }
    }

    @Override
    public boolean add(N e) {
        boolean add = super.add(e); //To change body of generated methods, choose Tools | Templates.
        changed(e);
        return add;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o); //To change body of generated methods, choose Tools | Templates.
        changed(o);
        return remove;
    }

}
