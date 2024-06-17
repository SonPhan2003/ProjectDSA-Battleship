/* Name: Phan Manh Son
 ID: ITDSIU21116
 Purpose: Battle ship game which play by human vs computer
*/

package battleship.UI;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FrameSplashscreen extends JFrame {

    private static final long serialVersionUID = 1L;

    public FrameSplashscreen() {
        super("Burning Battle");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setSize(600, 400);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/images/icon.png")));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
        JPanel container = new JPanel(null);
        UIJPanelBG splashPanel = new UIJPanelBG(
                Toolkit.getDefaultToolkit().createImage(getClass().getResource("/res/images/splashimage2.png")));
        ImageIcon loadingIMG = new ImageIcon(getClass().getResource("/res/images/loading.gif"));
        JLabel loadingLabel = new JLabel(loadingIMG);
        container.add(splashPanel);
        splashPanel.setBounds(0, 0, 600, 400);
        container.add(loadingLabel, 0);
        loadingLabel.setBounds(560, 370, 24, 24);
        this.add(container);
        this.setVisible(true);
    }
}