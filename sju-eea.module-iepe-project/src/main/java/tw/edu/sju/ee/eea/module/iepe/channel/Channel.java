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
import org.jfree.data.xy.XYSeries;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput.IepeStream;
import tw.edu.sju.ee.eea.util.iepe.io.SampledStream;

/**
 *
 * @author Leo
 */
public class Channel {

    private String device;
    private int channel;

    private IepeStream stream;
    private XYSeries series;

    private SampledStream sampled;

    public Channel(String device, int channel) throws IOException {
        this.device = device;
        this.channel = channel;
        
        this.series = new XYSeries(device + "/" + channel);
        this.stream = new IepeStream();

        sampled = new SampledStream(stream, 1600);
    }


    public void setName(String name) {
        this.series.setKey(name);
    }

    public String getDevice() {
        return device;
    }

    public int getChannel() {
        return channel;
    }

    public String getName() {
        return this.series.getKey().toString();
    }

    public IepeStream getStream() {
        return stream;
    }

    public XYSeries getSeries() {
        return series;
    }

    public void proc(long time) throws IOException {
        series.add(time, sampled.readSampled());
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
                return series.getKey().toString();
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
                            public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                Channel.this.setName(t);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Color>(
                        "CH_color",
                        Color.class,
                        Bundle.LBL_CH_color(),
                        Bundle.DCT_CH_color()) {
                            @Override
                            public Color getValue() throws IllegalAccessException, InvocationTargetException {
                                return (Color) renderer.getPaint(Channel.this);
                            }

                            @Override
                            public void setValue(Color color) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                renderer.setPaint(Channel.this, color);
                            }
                        });
                Sheet sheet = super.createSheet();
                sheet.put(set);
                return sheet;
            }
        };
    }

    interface Renderer {

        public void setPaint(Channel channel, Paint paint);

        public Paint getPaint(Channel channel);
    }

}
