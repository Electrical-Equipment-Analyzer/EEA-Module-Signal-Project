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
package tw.edu.sju.ee.eea.module.signal.io;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import tw.edu.sju.ee.eea.module.signal.device.SignalDeviceObject;
import tw.edu.sju.ee.eea.module.signal.temp.OscillogramChannel;

/**
 *
 * @author D10307009
 */
public class ChannelNode extends AbstractNode {
    
    private Channel channel;
    
    public ChannelNode(Channel channel) {
        super(Children.LEAF);
        this.channel = channel;
        
        setName(channel.getName());
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }
    
    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        if (channel instanceof OscillogramChannel) {
            actions.add(new AbstractAction("Delete") {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });
        }
        return actions.toArray(new AbstractAction[actions.size()]);
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
                    public void setValue(String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        channel.setName(name);
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
                    public void setValue(Color color) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        channel.setColor(color);
                    }
                });
        Sheet sheet = super.createSheet();
        sheet.put(set);
        return sheet;
    }
}
