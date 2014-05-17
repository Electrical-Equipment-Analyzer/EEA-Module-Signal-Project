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
package tw.edu.sju.ee.eea.module.iepe.project.ui;

import java.io.IOException;
import org.jfree.data.xy.XYSeries;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput.IepeStream;
import tw.edu.sju.ee.eea.util.iepe.io.SampledStream;

/**
 *
 * @author Leo
 */
public class SampledSeries extends XYSeries {

    private IepeStream stream;
    private SampledStream sampled;

    public SampledSeries(Comparable key) throws IOException {
        super(key);
        this.stream = new IepeStream();
        sampled = new SampledStream(stream, 1600);
    }

    public IepeStream getStream() {
        return stream;
    }
    
    public void proc(long time) throws IOException {
        this.add(time, sampled.readSampled());
    }
    
}
