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
package tw.edu.sju.ee.eea.module.iepe.project.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import tw.edu.sju.ee.eea.jni.mps.MPS140801IEPE;
import tw.edu.sju.ee.eea.module.iepe.file.IepeCursor;
import tw.edu.sju.ee.eea.module.iepe.file.IepeDataInfo;
import tw.edu.sju.ee.eea.util.iepe.IEPEException;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import tw.edu.sju.ee.eea.util.iepe.io.IepeInputStream;

/**
 *
 * @author Leo
 */
public class IepeRealtimeObject extends Thread implements IepeDataInfo, Serializable, Lookup.Provider {

    private IEPEInput iepe;
    private OutputStream screen;

    public IepeRealtimeObject() {
        iepe = new IEPEInput(new MPS140801IEPE(0, 16000), new int[]{1}, 512);
    }

    public void setScreen(OutputStream screen) {
        this.screen = screen;
        try {
            iepe.startIEPE();
            this.start();
        } catch (IEPEException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void run() {
        IepeInputStream iepeStreams = iepe.getIepeStreams(0);
        while (true) {
            try {
                while (iepeStreams.available() < 128) {
                    yield();
                }
                byte[] buffer = new byte[128];
                iepeStreams.read(buffer);
                screen.write(buffer);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public IepeCursor getCursor() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Real-time";
    }

    @Override
    public Lookup getLookup() {
        return Lookups.singleton(this);
    }

}
