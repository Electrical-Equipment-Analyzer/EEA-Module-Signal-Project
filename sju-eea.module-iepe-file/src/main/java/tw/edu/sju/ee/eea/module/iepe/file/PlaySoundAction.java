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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import tw.edu.sju.ee.eea.module.iepe.cookie.PlayStreamCookie;
import tw.edu.sju.ee.eea.util.iepe.IEPEPlayer;

@ActionID(
        category = "Acoustic",
        id = "tw.edu.sju.ee.eea.module.iepe.file.PlaySoundAction"
)
@ActionRegistration(
        displayName = "#CTL_PlaySoundAction"
)
@ActionReference(path = "Menu/Analyzers", position = 200, separatorBefore = 150)
@Messages("CTL_PlaySoundAction=PlaySound")
public final class PlaySoundAction implements ActionListener {

    private final PlayStreamCookie context;

    public PlaySoundAction(PlayStreamCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    IEPEPlayer audio = new IEPEPlayer();
                    Thread audioTread = null;
                    try {
                        audioTread = new Thread(audio);
                        audioTread.start();
                        OutputStream out = audio.getOutputStream();
                        byte[] buffer = new byte[1024];
                        while (context.readStream(buffer) > 0) {
                            out.write(buffer, 0, buffer.length);
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        audioTread.stop();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        thread.start();
    }
}
