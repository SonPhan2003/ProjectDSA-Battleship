/* Name: Phan Manh Son
 ID: ITDSIU21116
 Purpose: Battle ship game which play by human vs computer
*/

package battleship;

import battleship.UI.FrameManageship;
import battleship.UI.FrameSplashscreen;

public class BattleShip {

    public static void main(String[] args) {
        FrameSplashscreen intro = new FrameSplashscreen();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        intro.setVisible(false);
        // FrameManageship2PlClient manage = new FrameManageship2PlClient();
        // FrameManageship2PlServer manage = new FrameManageship2PlServer();
        FrameManageship manage = new FrameManageship();
        // FrameMenu menu = new FrameMenu();
        manage.setVisible(true);
        // menu.setVisible(true);
    }
}