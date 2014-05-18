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

import org.openide.util.NbBundle;
import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledSeries;

/**
 *
 * @author Leo
 */
public class Channel {

    private Lookup lkp;
    private String device;
    private int channel;
    private String name;
    private Color color;

    public Channel(String device, int channel) throws IOException {
        this.device = device;
        this.channel = channel;
        setName(device + "/" + channel);
        this.lkp = lkp;
    }

    public void setName(String name) {
        this.name = name;

    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getDevice() {
        return device;
    }

    public int getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    SampledSeries createSampledSeries(Class<? extends SampledSeries> c) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        SampledSeries sampledSeries = c.getConstructor(Comparable.class).newInstance(name);
        return sampledSeries;
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
    public Node createNodeDelegate(final Renderer renderer) {
        return new AbstractNode(Children.LEAF) {

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
                return Channel.this.getName();
            }

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
                                return Channel.this.getDevice();
                            }
                        });
                set.put(new PropertySupport.ReadOnly(
                        "CH_channel",
                        String.class,
                        Bundle.LBL_CH_channel(),
                        Bundle.DCT_CH_channel()) {
                            @Override
                            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                                return Channel.this.getChannel();
                            }
                        });
                set.put(new PropertySupport.ReadWrite<String>(
                        "CH_name",
                        String.class,
                        Bundle.LBL_CH_name(),
                        Bundle.DCT_CH_name()) {
                            @Override
                            public String getValue() throws IllegalAccessException, InvocationTargetException {
                                return Channel.this.getName();
                            }

                            @Override
                            public void setValue(String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                Channel.this.setName(name);
                                renderer.set(Channel.this, Comparable.class, name);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Color>(
                        "CH_color",
                        Color.class,
                        Bundle.LBL_CH_color(),
                        Bundle.DCT_CH_color()) {
                            @Override
                            public Color getValue() throws IllegalAccessException, InvocationTargetException {
                                Color color = Channel.this.getColor();
                                if (color == null) {
                                    color = renderer.getColor(Channel.this);
                                }
                                return color;
                            }

                            @Override
                            public void setValue(Color color) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                Channel.this.setColor(color);
                                renderer.set(Channel.this, Paint.class, color);
                            }
                        });
                Sheet sheet = super.createSheet();
                sheet.put(set);
                return sheet;
            }
        };
    }

    interface Renderer {

        public void set(Channel channel, Class c, Object o);

        public Color getColor(Channel channel);
    }

}
