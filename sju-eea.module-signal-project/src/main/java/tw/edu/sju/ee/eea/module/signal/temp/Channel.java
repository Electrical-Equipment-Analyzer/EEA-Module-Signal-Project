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
package tw.edu.sju.ee.eea.module.signal.temp;

import java.awt.Color;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import tw.edu.sju.ee.eea.core.math.SineSimulator;
import tw.edu.sju.ee.eea.module.signal.oscillogram.SineInputStream;
import tw.edu.sju.ee.eea.module.signal.oscillogram.ZoomInputStream;

/**
 *
 * @author D10307009
 */
public class Channel {

    private XYChart.Series<Number, Number> series = new LineChart.Series<Number, Number>();
    private ConcurrentLinkedQueue<XYChart.Data> queue = new ConcurrentLinkedQueue<XYChart.Data>();
    private String device;
    private int channel;
//    private String name;
    private Color color;

    public Channel(String device, int channel) {
        this.device = device;
        this.channel = channel;
        this.setName(device + "/" + channel);
        
    }

    public void update(double t) {
        int samplerate = 10000;
        SineSimulator s = new SineSimulator(samplerate, 100 * getChannel() + 100, 5);
        SineInputStream si = new SineInputStream(s);
        ZoomInputStream zi = new ZoomInputStream(si, samplerate);
        zi.update(queue, t);
    }

    public XYChart.Series<Number, Number> getSeries() {
        return series;
    }

    public ConcurrentLinkedQueue<XYChart.Data> getQueue() {
        return queue;
    }

    public String getDevice() {
        return device;
    }

    public int getChannel() {
        return channel;
    }

    public String getName() {
        return this.series.getName();
    }

    public Color getColor() {
        return color;
    }

    public void setName(String name) {
        this.series.setName(name);
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
