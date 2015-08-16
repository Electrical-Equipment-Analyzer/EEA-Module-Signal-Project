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
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.netbeans.api.project.Project;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.ui.io.SeriesOutputStream;
import tw.edu.sju.ee.eea.utils.io.ChannelInputStream;
import tw.edu.sju.ee.eea.utils.io.ValueInputStream;

/**
 *
 * @author Leo
 */
public class SourceChannel extends Channel implements  Lookup.Provider {

    private Lookup lkp;
    private ValueInputStream in;
    private int device;
    private int channel;
    private String name;
    private Color color;

    public SourceChannel(ValueInputStream in, int device, int channel, Lookup lkp) throws IOException {
        this.in = in;
        this.device = device;
        this.channel = channel;
        setName("USB" + device + "/" + channel);
        this.lkp = lkp;
    }

    public void setName(String name) {
        this.name = name;

    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDevice() {
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

    @Override
    public Lookup getLookup() {
        return lkp;
    }
    
    
//    class VoltageChannel {

        public  ChannelInputStream inputStream;
        public  SeriesOutputStream series;
        double samplerate = 32000;
        
        
//        private ValueOutputStream pipeIn;
//        private ValueInputStream pipeOut;

//        public VoltageChannel(Comparable key, int samplerate) {
//                //            super(key);
////            try {
////                PipedInputStream pipe = new PipedInputStream((int) (properties.device().getSampleRate() * 32));
////                pipeOut = new ValueInputStream(pipe);
////                pipeIn = new ValueOutputStream(new PipedOutputStream(pipe));
////            } catch (IOException ex) {
////                Exceptions.printStackTrace(ex);
////            }
//            
//            try {
//                channel = new ChannelInputStream((int) (properties.device().getSampleRate() * 32));
//                series = new SeriesOutputStream(key);
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }

        public void update(double t) {
            int target = 1000;
            int unit = 1000000;
            series.clear();
            try {
                double input = t * samplerate;
                double rate = input / target;

//                double value = 0;
//                for (int i = 0; i < target; i++) {
//                        value = channel.readValue();
//                        channel.skip((int) Math.ceil(input / target)-1);
//                    
//                        int position = (int) (i * t * unit / target);
//                        series.writeXY(position, value);
////                        System.out.println(position);
//                }
                
                int index = 0;
                double count = 0;
                double value = 0;
                while (index < target && count < input) {
                    if (count <= (index * rate)) {
                        value = inputStream.readValue();
                        count++;
                        while (count < (index * rate)) {
                            int skip = (int) Math.ceil((index * rate) - count);
                            inputStream.skip(skip);
                            count += skip;
                        }
                    }
                    if (index <= (count / rate)) {
                        int position = (int) (index * t * unit / target);
                        series.writeXY(position, value);
                        index = (int) Math.ceil(count / rate);
                    }
                }
                while (inputStream.available() > samplerate) {
                    inputStream.skip((long) samplerate);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

//        @Override
//        public void writeValue(double value) throws IOException {
//            channel.writeValue(value);
//        }

//    }
    
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
    public Node createNodeDelegate(final Channel.Renderer renderer) {
        return new AbstractNode(Children.LEAF, lkp) {

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
                return SourceChannel.this.getName();
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
                                return SourceChannel.this.getDevice();
                            }
                        });
                set.put(new PropertySupport.ReadOnly(
                        "CH_channel",
                        String.class,
                        Bundle.LBL_CH_channel(),
                        Bundle.DCT_CH_channel()) {
                            @Override
                            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                                return SourceChannel.this.getChannel();
                            }
                        });
                set.put(new PropertySupport.ReadWrite<String>(
                        "CH_name",
                        String.class,
                        Bundle.LBL_CH_name(),
                        Bundle.DCT_CH_name()) {
                            @Override
                            public String getValue() throws IllegalAccessException, InvocationTargetException {
                                return SourceChannel.this.getName();
                            }

                            @Override
                            public void setValue(String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                SourceChannel.this.setName(name);
                                renderer.setName(SourceChannel.this, name);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Color>(
                        "CH_color",
                        Color.class,
                        Bundle.LBL_CH_color(),
                        Bundle.DCT_CH_color()) {
                            @Override
                            public Color getValue() throws IllegalAccessException, InvocationTargetException {
                                Color color = SourceChannel.this.getColor();
                                if (color == null) {
                                    color = renderer.getColor(SourceChannel.this);
                                }
                                return color;
                            }

                            @Override
                            public void setValue(Color color) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                SourceChannel.this.setColor(color);
                                renderer.setColor(SourceChannel.this, color);
                            }
                        });
                Sheet sheet = super.createSheet();
                sheet.put(set);
                return sheet;
            }
        };
    }

}
