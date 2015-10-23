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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.ws.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.Presenter;
import tw.edu.sju.ee.eea.module.signal.device.DeviceChannel;
import tw.edu.sju.ee.eea.module.signal.device.SignalDeviceObject;

/**
 *
 * @author D10307009
 */
@ActionID(category = "OSC", id = "dd")
@ActionRegistration(displayName = "uig")
public class AddChannelAction extends CookieAction implements Presenter.Popup {

    private static final Class PROP_KEY = DeviceChannel.class;
    private SignalOscillogramObject context = null;

    @Override
    public JMenuItem getPopupPresenter() {
        System.out.println(context);
        JMenu root = new JMenu("Add Channel");
        root.setIcon(ImageUtilities.loadImageIcon("net/dalhaug/icons/Arrow.png", false));
        for (SignalDeviceObject device : context.getProject().getDeviceList()) {
            JMenu menu = new JMenu(device.getName());
            for (DeviceChannel channel : device.getChannels()) {
                JMenuItem item = new JMenuItem(channel.getName());
                //Create a client property on the JMenuItem:
                item.putClientProperty(SignalOscillogramObject.class, context);
                item.putClientProperty(DeviceChannel.class, channel);
                item.addActionListener(this);
                menu.add(item);
            }
            root.add(menu);
        }
        return root;
    }

    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes[0].getLookup().lookup(SignalOscillogramObject.class) != null) {
            context = nodes[0].getLookup().lookup(SignalOscillogramObject.class);
            return true;
        }
        return false;
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[]{SignalOscillogramObject.class};
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) ev.getSource();
            //Retrieve from the client property on the JMenuItem:
            SignalOscillogramObject object = (SignalOscillogramObject) item.getClientProperty(SignalOscillogramObject.class);
            DeviceChannel channel = (DeviceChannel) item.getClientProperty(DeviceChannel.class);
            System.out.println(channel);
            object.add(channel);
        }
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        System.out.println(Arrays.toString(activatedNodes));
        System.out.println("ppppppppppp");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
