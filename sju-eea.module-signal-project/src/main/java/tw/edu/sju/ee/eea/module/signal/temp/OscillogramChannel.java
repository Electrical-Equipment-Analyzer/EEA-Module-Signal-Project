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
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.openide.util.Exceptions;
import tw.edu.sju.ee.eea.core.math.SineSimulator;
import tw.edu.sju.ee.eea.module.signal.io.Channel;
import tw.edu.sju.ee.eea.module.signal.oscillogram.FourierTransformerRenderer;
import tw.edu.sju.ee.eea.module.signal.oscillogram.SignalRenderer;
import tw.edu.sju.ee.eea.module.signal.oscillogram.ZoomRenderer;
import tw.edu.sju.ee.eea.utils.io.ValueInput;

/**
 *
 * @author D10307009
 */
public class OscillogramChannel implements Channel {

    private XYChart.Series<Number, Number> series = new LineChart.Series<Number, Number>();
    private ConcurrentLinkedQueue<XYChart.Data> queue = new ConcurrentLinkedQueue<XYChart.Data>();
    private String device;
    private int channel;
    private Color color;
    private SignalRenderer renderer;
    int samplerate = 16384;
    private ValueInput vi;

    public OscillogramChannel(String device, int channel, SignalRenderer renderer, ValueInput vi) {
        this.device = device;
        this.channel = channel;
        this.renderer = renderer;
        this.setName(device + "/" + channel);
//        SineSimulator s = new SineSimulator(samplerate, f[getChannel()], 5);
//        vi = new SineInputStream(s);
        this.vi = vi;
    }

    private static final int[] f = {100, 200, 300};

    public void update(double t) {
        renderer.renderer(queue, t, vi, samplerate);
        try {
            vi.skip(vi.available());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
