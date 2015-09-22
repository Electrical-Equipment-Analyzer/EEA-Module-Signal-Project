/*
 * Copyright (c) 2015, National Taiwan University of Science and Technology,
 * Department of Electrical Engineering EE-305. All rights reserved.
 */
package tw.edu.sju.ee.eea.module.signal.device;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;

/**
 *
 * @author 薛聿明
 */
public class SignalDeviceObject extends AbstractNode implements Serializable, Lookup.Provider {

    public SignalDeviceObject(Project project) {
        super(Children.LEAF);
        setDisplayName("NI1234");
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
    }

    TopComponent tc;

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tc == null) {
                    tc = MultiViews.createMultiView("application/function", SignalDeviceObject.this);
                }
                tc.open();
                tc.requestActive();
            }
        };
    }
}
