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
package tw.edu.sju.ee.eea.module.iepe.project.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import tw.edu.sju.ee.eea.core.math.ComplexArray;

/**
 *
 * @author Leo
 */
public class Pattern extends ArrayList<double[]> {

    private static final FastFourierTransformer FFT = new FastFourierTransformer(DftNormalization.STANDARD);
    private Date date;
    private int bps;
    private int length;

    public Pattern(Date date, int bps, int length, double[] ... channels) {
        this.date = date;
        this.bps = bps;
        this.length = length;
        for (double[] channel : channels) {
            Complex[] transform = FFT.transform(channel, TransformType.FORWARD);
            int max = transform.length / 2 + 1;
            this.add(ComplexArray.getAbsolute(Arrays.copyOf(transform, max)));
        }
    }
    
    public Pattern(int bps, int length, double[] ... channels) {
        this(new Date(), bps, length, channels);
    }

    private double frequency(int i) {
        return i * bps / length;
    }

    private int frequency(double frequency) {
        return (int) (frequency * length / bps);
    }

    public Warning max(AnalyzerRule rule, int channel, double mimimum, double maximum) {
        try {
            double max = 0;
            double frequency = 0;
            int from = frequency(mimimum);
            int to = frequency(maximum);
            double[] data = this.get(channel);
            for (int i = from; i < to; i++) {
                if (data[i] > max) {
                    max = data[i];
                    frequency = frequency(i);
                }
            }
            max = max / length * 2;
            return new Warning(rule, date, frequency, max);
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    public List<Warning> rules(List<AnalyzerRule> rules) {
        List<Warning> list = new ArrayList<Warning>();
        for (AnalyzerRule rule : rules) {
            Warning warning = max(rule, rule.getChannel(), rule.getMinimum(), rule.getMaximum());
//            System.out.println(warning);
            if (warning != null && warning.getValue() > rule.getMagnitude()) {
                list.add(warning);
            }
        }
        return list;
    }
}
