package org.jill.game.level.handler.jill1;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

import org.jill.game.gui.menu.ClassicMenu;
import org.jill.game.gui.menu.HighScoreMenu;
import org.jill.game.level.AbstractChangeLevel;
import org.jill.game.level.cfg.JillLevelConfiguration;
import org.jill.game.screen.conf.RectangleConf;
import org.jill.openjill.core.api.jill.JillConst;

/**
 * Start menu of credit for level 1 of Jill Trilogy.
 *
 * @author Emeric MARTINEAU
 */
public class StartMenuJill1Handler extends AbstractChangeLevel {
    /**
     * Default constructor of level.
     *
     * @throws IOException                  if missing file
     * @throws ReflectiveOperationException if missing class must be load
     */
    public StartMenuJill1Handler() throws IOException, ReflectiveOperationException {
        super(new JillLevelConfiguration("JILL1.SHA", Optional.of("INTRO.JN1"), "JILL1.VCL",
                "JILL1.CFG", "JN1", Optional.of(StartMenuJill1Handler.class), false));
        centerScreen();

        infoBox.setContent(vclFile.getVclText().get(0).getText());

        this.menuLoadGame.setPreviousMenu(Optional.of(this.menu));
    }

    @Override
    protected void initMenu() {
        this.menuStd = new ClassicMenu("start_menu.json", pictureCache);
        this.menu = this.menuStd;
    }

    @Override
    protected void menuEntryValidate(final int value) {
        if (this.menu == this.menuLoadGame) {
            doMenuValidate();
        } else {
            switch (value) {
                case 0:
                    changeScreenManager(MapLevelHandler.class);
                    break;
                case 1:
                    this.menuLoadGame.setEnable(true);
                    this.menu = this.menuLoadGame;
                    break;
                case 2:
                    changeScreenManager(StoryScreenJill1Handler.class);
                    break;
                case 3:
                    infoBox.setEnable(true);
                    break;
                case 4:
                    changeScreenManager(OrderingInfoScreenJill1Handler.class);
                    break;
                case 5:
                    changeScreenManager(CreditScreenJill1Handler.class);
                    break;
                case 7:
                    changeScreenManager(NoisemakerScreenJill1Handler.class);
                    break;
                case 9:
                    System.exit(0);
                    break;
                default:
            }
        }
    }

    @Override
    protected void drawControl() {
        this.statusBar.drawControl(createHigScore());
    }

    @Override
    protected void drawInventory() {
        // Draw jill face
        this.statusBar.drawInventory(this.statusBar.createInventoryArea());
        this.statusBar.drawControl(createHigScore());
    }

    /**
     * Create highscore.
     *
     * @return picture
     */
    private BufferedImage createHigScore() {
        final BufferedImage highScore = statusBar.createControlArea();

        final RectangleConf controlAreaConf =
                this.statusBar.getControlAreaConf();

        new HighScoreMenu(highScore, pictureCache,
                cfgFile.getHighScore(), controlAreaConf.getX(),
                controlAreaConf.getY());

        return highScore;
    }

    @Override
    protected void centerScreen() {
        final int blocOffsetX = 112;
        final int blocOffsetY = 53;

        final RectangleConf offset
                = this.statusBar.getGameAreaConf().getOffset();

        // Picture offset
        offset.setX(
                -(blocOffsetX + 1) * JillConst.getBlockSize());
        offset.setY(
                -(blocOffsetY + 1) * JillConst.getBlockSize());
    }

    @Override
    protected void doRunNext() {
        super.doRunNext();
        runGame = false;
        menu.setEnable(true);
    }

    @Override
    protected void doEscape() {
        if (this.menu == this.menuLoadGame) {
            this.menu.setEnable(false);
            this.menu = this.menuStd;
        } else {
            System.exit(0);
        }
    }
}
