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
package tw.edu.sju.ee.eea.module.iepe.project.object;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.channel.Channel;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;

/**
 *
 * @author Leo
 */
public class IepeRealtimeObject implements IepeProject.Child, Serializable, Lookup.Provider {

    private Lookup lkp;

    public IepeRealtimeObject(Project project) {
        this.lkp = Lookups.fixed(project, this, new IepeNavigatorHint());
    }

//    @Override
//    public IepeCursor getCursor() {
//        return null;
//    }
//
//    @Override
//    public InputStream getInputStream() {
//        return null;
//    }
//
//    @Override
    public String getDisplayName() {
        return "Real-time";
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    TopComponent tc;

    @Override
    public Node createNodeDelegate() {
        return new AbstractNode(Children.LEAF, lkp) {

            @Override
            public Action getPreferredAction() {
                return new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (tc == null) {
                            tc = MultiViews.createMultiView("application/iepe-realtime", IepeRealtimeObject.this);
                        }
                        tc.open();
                        tc.requestActive();
                    }
                };
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
                return IepeRealtimeObject.this.getDisplayName();
            }

        };
    }

    private static final class IepeNavigatorHint implements NavigatorLookupHint {

        @Override
        public String getContentType() {
            return "application/iepe-realtime"; // NOI18N
        }

    }
}
