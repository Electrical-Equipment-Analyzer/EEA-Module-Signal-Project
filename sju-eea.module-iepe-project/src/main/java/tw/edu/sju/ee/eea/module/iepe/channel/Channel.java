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
import java.io.IOException;
import java.io.PipedInputStream;
import org.jfree.data.xy.XYSeries;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput.IepeStream;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;
import tw.edu.sju.ee.eea.util.iepe.io.SampledStream;

/**
 *
 * @author Leo
 */
public class Channel {

    private String device;
    private int channel;
//    private String name;
    private Color color;
    
    private IepeStream stream;
    private XYSeries series;
    
    
        private SampledStream sampled;

    public Channel(String device, int channel) throws IOException {
        this.device = device;
        this.channel = channel;
//        this.name = device + "/" + channel;
        this.series = new XYSeries(device + "/" + channel);
        this.stream = new IepeStream();
        this.color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        
        sampled = new SampledStream(stream, 1600);
    }

    /**
     * Creates a new instance of Category
     */
    public Channel(String name) {
        this.series = new XYSeries(name);
    }

    public void setName(String name) {
        this.series.setKey(name);
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
        return this.series.getKey().toString();
    }

    public Color getColor() {
        return color;
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

}
