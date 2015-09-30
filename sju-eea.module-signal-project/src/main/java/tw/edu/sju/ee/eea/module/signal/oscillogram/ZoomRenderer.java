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

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.scene.chart.XYChart;
import org.openide.util.Exceptions;
import tw.edu.sju.ee.eea.utils.io.ValueInput;

/**
 *
 * @author D10307009
 */
public class ZoomRenderer implements SignalRenderer {

    private static final int POINT = 1000;

//    public ZoomRenderer(ValueInput vi, int samplerate) {
//        super(vi, samplerate);
//    }

    @Override
    public void renderer(ConcurrentLinkedQueue<XYChart.Data> queue, double time, ValueInput vi, int samplerate) {
        try {
            double rate = time * samplerate / POINT;
            int index = 1;
            double count = 0;
            double value = 0;
            while (index <= POINT) {
                if (index <= (count / rate)) {
                    double position = count / samplerate;
                    queue.add(new XYChart.Data(position, value));
                    index++;
                } else {
                    value = vi.readValue();
                    count++;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
