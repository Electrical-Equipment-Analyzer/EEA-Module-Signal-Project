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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.w3c.dom.Document;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Leo
 */
public class IepeHistoryObject implements IepeProject.Child, Runnable, Serializable, Lookup.Provider {

    private Lookup lkp;
    private Document doc;

    public IepeHistoryObject(IepeProject project) {
        this.lkp = new ProxyLookup(new Lookup[]{Lookups.singleton(project), Lookups.singleton(this)});
        doc = project.getDoc();

        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        Element eElement = (Element) doc.getElementsByTagName("history").item(0);

        System.out.println("----------------------------");
        System.out.println("item : " + eElement.getElementsByTagName("patten").item(0).getTextContent());

    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    public String getDisplayName() {
        return "History";
    }

    @Override
    public void run() {
        IepeProject context = lkp.lookup(IepeProject.class);
        FileObject projectDirectory = context.getProjectDirectory();
        try {
            projectDirectory.createFolder("Record");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        FileObject fileObject = projectDirectory.getFileObject("Record");
        try {
            FileObject createData = fileObject.createData("aa.iepe");
            OutputStream outputStream = createData.getOutputStream();
            IEPEInput.Stream stream = context.getIepe().addStream(0, outputStream);
            while (!Thread.interrupted()) ;
            context.getIepe().removeStream(0, stream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    TopComponent tc;

    public Node createNodeDelegate() {
        return new AbstractNode(Children.LEAF, lkp) {

            @Override
            public Action getPreferredAction() {
                return new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (tc == null) {
                            tc = MultiViews.createMultiView("application/iepe-history", IepeHistoryObject.this);
                        }
                        tc.open();
                        tc.requestActive();
                    }
                };
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return "History";
            }

        };
    }
}
