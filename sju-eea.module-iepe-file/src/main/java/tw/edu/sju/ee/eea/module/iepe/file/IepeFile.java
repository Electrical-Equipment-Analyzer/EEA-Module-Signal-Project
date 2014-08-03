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
package tw.edu.sju.ee.eea.module.iepe.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 *
 * @author Leo
 */
public class IepeFile implements Serializable {

    private long time;
    private int samplerate;
    private int channel;

    public IepeFile(long time, int samplerate, int channel) {
        this.time = time;
        this.samplerate = samplerate;
        this.channel = channel;
    }

    public long getTime() {
        return time;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public int getChannel() {
        return channel;
    }

    public static class Input {

        private IepeFile header;
        private InputStream in;

        public Input(InputStream in) throws FileNotFoundException, IOException, ClassNotFoundException {
            this.header = (IepeFile) new ObjectInputStream(in).readObject();
            this.in = in;
        }

        public IepeFile getHeader() {
            return header;
        }

        public InputStream getInputStream() {
            return in;
        }
    }

    public static class Output {

        private IepeFile header;
        private OutputStream out;

        public Output(OutputStream out, IepeFile header) throws FileNotFoundException, IOException {
            this.header = header;
            this.out = out;
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(header);
            objectOutputStream.flush();
        }

        public IepeFile getHeader() {
            return header;
        }

        public OutputStream getOutputStream() {
            return out;
        }
    }

}
