package org.jill.game.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jill.game.gui.conf.InformationBoxConf;
import org.jill.game.gui.menu.SubMenu;
import org.jill.game.screen.conf.RectangleConf;
import org.jill.openjill.core.api.manager.TextManager;
import org.jill.openjill.core.api.manager.TileManager;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Information box like instruction or int.
 *
 * @author Emeric MARINEAU
 */
public final class InformationBox extends AbstractMessageBox {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            InformationBox.class.getName());
    /**
     * List of text.
     */
    private final ArrayList<SubMenu> listText = new ArrayList<>();
    /**
     * Length of line in box.
     */
    private int lineLength;
    /**
     * Number of line in box.
     */
    private int numberLinePerScreen;
    /**
     * Background.
     */
    private BufferedImage boxPicture;
    /**
     * Graphic of status bar.
     */
    private Graphics2D g2BoxPicture;
    /**
     * Picture cache.
     */
    private TileManager pictureCache;
    /**
     * Title of dialog box.
     */
    private SubMenu title;

    /**
     * Index of text.
     */
    private int currentMenuPos = 0;

    /**
     * End of index to don't display blank screen.
     */
    private int endMenuPos;

    /**
     * If menu is enable.
     */
    private boolean enable = false;

    /**
     * Configuration.
     */
    private InformationBoxConf conf;

    /**
     * Size of letter.
     */
    private int sizeOfLetter;

    /**
     * Constructor for dialog box 190x130 size.
     *
     * @param pctCache cache picture manager
     */
    public InformationBox(final TileManager pctCache) {
        constructor(pctCache);
    }

    /**
     * Read config file.
     *
     * @param filename final name of config file
     * @return properties file
     */
    private static InformationBoxConf readConf(final String filename) {

        final ObjectMapper mapper = new ObjectMapper();
        final InputStream is =
                LevelMessageBox.class.getClassLoader().
                        getResourceAsStream(filename);

        InformationBoxConf mc;

        // Load menu
        try {
            mc = mapper.readValue(is, InformationBoxConf.class);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,
                    String.format("Unable to load config for message level '%s'",
                            filename),
                    ex);

            throw new RuntimeException(ex);
        }

        return mc;
    }

    /**
     * Construct object.
     *
     * @param pctCache picture cache manage
     */
    private void constructor(final TileManager pctCache) {
        this.pictureCache = pctCache;

        this.sizeOfLetter = pctCache.getTextManager().
                createSmallText(" ", 0, 0).getWidth();

        this.conf = readConf("information_box.json");

        // Buffer image
        this.boxPicture =
                new BufferedImage(conf.getWidth(), conf.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);

        // Graphic
        this.g2BoxPicture = this.boxPicture.createGraphics();

        RectangleConf textArea = this.conf.getTextarea();

        drawArea(this.g2BoxPicture, pctCache, Optional.of(textArea));

        drawAllPicture(this.g2BoxPicture, this.pictureCache, this.conf);

        this.lineLength = (this.conf.getWidth() - this.conf.getBorderWith() * 2)
                / this.sizeOfLetter;
        this.numberLinePerScreen = (this.conf.getHeight()
                - (this.conf.getBorderHeight()
                + this.conf.getNbLineDraw())) / this.sizeOfLetter;
    }

    /**
     * Add space.
     *
     * @param text text to align
     * @return new text
     */
    private String addSpace(final String text) {
        final int numberStartSpace = (lineLength - text.length()) / 2;
        final int numberEndSpace = lineLength
                - numberStartSpace - text.length();

        final StringBuilder sb = new StringBuilder(lineLength);

        for (int i = 0; i < numberStartSpace; i++) {
            sb.append(' ');
        }

        sb.append(text);

        for (int i = 0; i < numberEndSpace; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }

    /**
     * Draw text
     */
    private void drawTextArea() {
        SubMenu line;

        RectangleConf textArea = this.conf.getTextarea();

        drawArea(this.g2BoxPicture, this.pictureCache, Optional.of(textArea));

        int end = currentMenuPos + numberLinePerScreen;

        if (listText.size() < numberLinePerScreen) {
            // Add blank line to center
            int nbLineToAdd = (numberLinePerScreen - listText.size()) / 2;

            line = new SubMenu(0, " ");

            for (int index = 0; index < nbLineToAdd; index++) {
                listText.add(index, line);
            }
        }

        if (end > listText.size()) {
            end = listText.size();
        }

        int yText = this.conf.getBorderHeight();

        int offsetTextX = this.conf.getOffsetTextDrawX();

        // Draw text
        for (int index = currentMenuPos; index < end; index++) {
            line = listText.get(index);
            pictureCache.getTextManager().drawSmallText(g2BoxPicture,
                    offsetTextX, yText, line.getText(), line.getColor(),
                    TextManager.BACKGROUND_COLOR_NONE);

            yText += this.sizeOfLetter;
        }

        int offsetTitleX = this.conf.getOffsetTitleDrawX();
        int offsetTitleY = this.conf.getOffsetTitleDrawY();

        // Now draw title
        pictureCache.getTextManager().drawBigText(g2BoxPicture,
                offsetTitleX, offsetTitleY, title.getText(), title.getColor(),
                TextManager.BACKGROUND_COLOR_NONE);
    }

    /**
     * Return status bar.
     *
     * @return picture of status bar
     */
    public BufferedImage getBox() {
        return boxPicture;
    }

    /**
     * Set content text.
     *
     * @param content text to draw
     */
    public void setContent(final String content) {
        // Split CRLF
        final String[] lines = content.split("\r\n");

        // Clear an set new capacity
        listText.clear();
        listText.ensureCapacity(lines.length);

        // Character of color if set
        char colorChar;
        // Color after convertion
        int color;
        // Text
        String text;

        for (String currentLine : lines) {
            // Check if first char is number. If it's, use for color
            if (currentLine.length() > 0) {
                colorChar = currentLine.charAt(0);
            } else {
                colorChar = ' ';
            }

            if (colorChar >= '0' && colorChar <= '9') {
                color = colorChar - 48;
                text = currentLine.substring(1);
            } else {
                color = TextManager.COLOR_WHITE;
                text = currentLine;
            }

            listText.add(new SubMenu(color, addSpace(text)));
        }

        // First is title
        title = listText.get(0);
        listText.remove(0);

        // Calculate the last first line to display
        endMenuPos = listText.size() - numberLinePerScreen - 1;

        drawTextArea();
    }

    /**
     * Up cursor
     */
    public void up() {
        if (currentMenuPos > 0) {
            currentMenuPos--;

            drawTextArea();
        }
    }

    /**
     * Down cursor
     */
    public void down() {
        if (currentMenuPos < endMenuPos) {
            currentMenuPos++;

            drawTextArea();
        }
    }

    /**
     * @return enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @param enable enable � d�finir
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * Position of box.
     *
     * @return X
     */
    public int getX() {
        return this.conf.getX();
    }

    /**
     * Position of box.
     *
     * @return Y
     */
    public int getY() {
        return this.conf.getY();
    }


}