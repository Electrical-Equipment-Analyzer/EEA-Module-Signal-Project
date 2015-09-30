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
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.openide.util.Exceptions;
import tw.edu.sju.ee.eea.core.math.ComplexArray;
import tw.edu.sju.ee.eea.utils.io.ValueInput;

/**
 *
 * @author D10307009
 */
public class FourierTransformerRenderer implements SignalRenderer {

    private static final FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

//    public FourierTransformerRenderer(ValueInput vi, int samplerate) {
//        super(vi, samplerate);
//    }

    public void renderer(ConcurrentLinkedQueue<XYChart.Data> queue, double time, ValueInput vi, int samplerate) {
        try {
            double[] array = new double[8192];
            for (int i = 0; i < array.length; i++) {
                array[i] = vi.readValue();
            }
            Complex[] transform = fft.transform(array, TransformType.FORWARD);
//            double[] absolute = ComplexArray.getAbsolute(Arrays.copyOf(transform, transform.length / 2 + 1));
            double[] abs = ComplexArray.getAbsolute(transform);

            double[] absolute = new double[abs.length / 2 + 1];
            absolute[0] = abs[0];
            for (int i = 1; i < absolute.length; i++) {
                absolute[i] = abs[i] + abs[abs.length - i];
            }

            double x = samplerate / (double) transform.length;
            for (int i = 0; i < absolute.length; i++) {
                queue.add(new XYChart.Data(i * x, absolute[i] / transform.length));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
