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
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author Leo
 */
public class IepeRealtimeNode extends AbstractNode implements Serializable, Lookup.Provider {

    public IepeRealtimeNode(Children children) {
        super(Children.LEAF, Lookups.singleton(children));
    }

    @Override
    public Action[] getActions(boolean arg0) {
        Action[] nodeActions = new Action[1];
        nodeActions[0] = new OpenFarmDetailsAction();
        return nodeActions;
    }

    private final class OpenFarmDetailsAction extends AbstractAction {

        public OpenFarmDetailsAction() {
            super("Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TopComponent tc = MultiViews.createMultiView("application/iepe-realtime", IepeRealtimeNode.this);
            tc.open();
            tc.requestActive();
        }

    }
}
