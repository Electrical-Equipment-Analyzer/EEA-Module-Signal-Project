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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.loaders.DataNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.jni.mps.MPS140801IEPE;
import tw.edu.sju.ee.eea.module.iepe.file.IepeCursor;
import tw.edu.sju.ee.eea.module.iepe.file.IepeDataInfo;
import tw.edu.sju.ee.eea.module.iepe.project.window.IepeNavigatorPanel;
import tw.edu.sju.ee.eea.util.iepe.IEPEException;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.IEPEPlayer;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;

/**
 *
 * @author Leo
 */
public class IepeRealtimeObject implements Runnable, IepeDataInfo, Serializable, Lookup.Provider {

    private Lookup lkp;
    private IEPEInput iepe;
    private OutputStream screen;
    private IEPEPlayer player;

    public IepeRealtimeObject(Project project) {
        this.lkp = Lookups.fixed(project, this, new IepeNavigatorHint());
        iepe = new IEPEInput(new MPS140801IEPE(0, 16000), new int[]{1}, 512);
        
    }

    public void setScreen(OutputStream screen) {
        this.screen = screen;
        try {
            player = new IEPEPlayer();
            player.startPlay();
            iepe.startIEPE();
            new Thread(this).start();
        } catch (IEPEException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void run() {
        IepeInputStream iepeStreams = iepe.getIepeStreams(0);
        OutputStream po = player.getOutputStream();
        while (true) {
            try {
                while (iepeStreams.available() < 128) {
                    Thread.yield();
                }
                byte[] buffer = new byte[128];
                iepeStreams.read(buffer);
                screen.write(buffer);
                po.write(buffer);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public IepeCursor getCursor() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Real-time";
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    TopComponent tc;

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
                return "Real-time";
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
