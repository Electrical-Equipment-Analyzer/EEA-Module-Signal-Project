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
package tw.edu.sju.ee.eea.module.temp;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author D10307009
 */
public class ListenerList<E> extends ArrayList<E> {

    private List<ListDataListener> listener = new ArrayList<ListDataListener>();

    public boolean addChangeListener(ListDataListener listener) {
        return this.listener.add(listener);
    }

    public boolean removeChangeListener(ListDataListener listener) {
        return this.listener.remove(listener);
    }

    @Override
    public boolean add(E e) {
        boolean add = super.add(e); //To change body of generated methods, choose Tools | Templates.
        for (ListDataListener listener : this.listener) {
            listener.intervalAdded(new ListDataEvent(e, ListDataEvent.INTERVAL_ADDED, 0, 0));
        }
        return add;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o); //To change body of generated methods, choose Tools | Templates.
        for (ListDataListener listener : this.listener) {
            listener.intervalRemoved(new ListDataEvent(o, ListDataEvent.INTERVAL_ADDED, 0, 0));
        }
        return remove;
    }

}
