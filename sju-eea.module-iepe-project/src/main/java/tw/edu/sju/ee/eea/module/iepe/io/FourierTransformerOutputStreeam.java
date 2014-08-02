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

package tw.edu.sju.ee.eea.module.iepe.io;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import tw.edu.sju.ee.eea.core.math.ComplexArray;

/**
 *
 * @author Leo
 */
public class FourierTransformerOutputStreeam {
    
        private static final FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        private FrequencyOutput out;
        private int index;
        private double[] array;

        public FourierTransformerOutputStreeam(FrequencyOutput out, int size) {
            this.out = out;
            this.array = new double[size];
        }

        public void writeValue(double value) throws IOException {
            this.array[index++] = value;
            if (!(index < array.length)) {
                index = 0;
                Complex[] transform = fft.transform(array, TransformType.FORWARD);
                double[] absolute = ComplexArray.getAbsolute(Arrays.copyOf(transform, transform.length / 2 + 1));
                out.writeFrequency(32000.0 / transform.length, absolute);
            }
        }
}
