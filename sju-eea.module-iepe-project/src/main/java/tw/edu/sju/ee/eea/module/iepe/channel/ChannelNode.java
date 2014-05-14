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
package tw.edu.sju.ee.eea.module.iepe.channel;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Leo
 */
public class ChannelNode extends AbstractNode {

    private Channel channel;

    /**
     * Creates a new instance of InstrumentNode
     */
    public ChannelNode(Channel channel) {
        super(Children.LEAF, Lookups.singleton(channel));
        this.channel = channel;
        setDisplayName(channel.getName());
//            setIconBaseWithExtension("org/netbeans/myfirstexplorer/marilyn.gif");
    }

    @Override
    public boolean canCut() {
        return true;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{};
    }

    @NbBundle.Messages({
        "LBL_CH_title=Properties",
        "LBL_CH_device=Device",
        "DCT_CH_device=IEPE Device Path",
        "LBL_CH_channel=Channel",
        "DCT_CH_channel=Input Channel",
        "LBL_CH_name=Name",
        "DCT_CH_name=Channel Name",
        "LBL_CH_color=Color",
        "DCT_CH_color=Show Color"
    })
    @Override
    protected Sheet createSheet() {
        Sheet.Set set = new Sheet.Set();
        set.setName(Bundle.LBL_CH_title());
        set.put(new PropertySupport.ReadOnly(
                "CH_device",
                String.class,
                Bundle.LBL_CH_device(),
                Bundle.DCT_CH_device()) {
                    @Override
                    public Object getValue() throws IllegalAccessException, InvocationTargetException {
                        return channel.getDevice();
                    }
                });
        set.put(new PropertySupport.ReadOnly(
                "CH_channel",
                String.class,
                Bundle.LBL_CH_channel(),
                Bundle.DCT_CH_channel()) {
                    @Override
                    public Object getValue() throws IllegalAccessException, InvocationTargetException {
                        return channel.getChannel();
                    }
                });
        set.put(new PropertySupport.ReadWrite<String>(
                "CH_name",
                String.class,
                Bundle.LBL_CH_name(),
                Bundle.DCT_CH_name()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return channel.getName();
                    }

                    @Override
                    public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        channel.setName(t);
                    }
                });
        set.put(new PropertySupport.ReadWrite<Color>(
                "CH_color",
                Color.class,
                Bundle.LBL_CH_color(),
                Bundle.DCT_CH_color()) {
                    @Override
                    public Color getValue() throws IllegalAccessException, InvocationTargetException {
                        return channel.getColor();
                    }

                    @Override
                    public void setValue(Color t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        channel.setColor(t);
                    }
                });
        Sheet sheet = super.createSheet();
        sheet.put(set);
        return sheet;
    }

}
