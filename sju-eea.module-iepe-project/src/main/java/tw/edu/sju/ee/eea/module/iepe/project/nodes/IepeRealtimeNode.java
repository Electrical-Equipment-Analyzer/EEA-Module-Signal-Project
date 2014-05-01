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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.file.IepeCursor;
import tw.edu.sju.ee.eea.module.iepe.file.IepeDataInfo;

/**
 *
 * @author Leo
 */
public class IepeRealtimeNode extends AbstractNode {

    private IepeCursor cursor;

    public IepeRealtimeNode(Children children) {
        super(Children.LEAF, Lookups.singleton(children));
        cursor = new IepeCursor();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
            new OpenFarmDetailsAction()
        };
    }

    @Override
    public Action getPreferredAction() {
        return new OpenFarmDetailsAction();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getDisplayName() {
        return "Real-time";
    }

    private class AA implements IepeDataInfo, Serializable, Lookup.Provider {

        @Override
        public IepeCursor getCursor() {
            return cursor;
        }

        @Override
        public InputStream getInputStream() {
            try {
                return new FileInputStream(new File("C:\\Users\\Leo\\Documents\\rec.iepe"));
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }

        @Override
        public String getDisplayName() {
        return "Real-time";
        }

        @Override
        public Lookup getLookup() {
//            ProxyLookup proxyLookup = new ProxyLookup(new Lookup[]{Lookups.singleton(this), IepeRealtimeNode.this.getLookup()});
            return Lookups.singleton(this);
        }
    }

    private final class OpenFarmDetailsAction extends AbstractAction {

        public OpenFarmDetailsAction() {
            super("Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TopComponent tc = MultiViews.createMultiView("application/iepe", new AA());
            tc.open();
            tc.requestActive();
        }
        
    }
}
