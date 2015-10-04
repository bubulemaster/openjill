package org.jill.game.entities.obj;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jill.game.entities.obj.abs.AbstractParameterObjectEntity;
import org.jill.openjill.core.api.entities.ObjectEntity;
import org.jill.openjill.core.api.entities.ObjectParam;
import org.jill.openjill.core.api.keyboard.KeyboardLayout;
import org.jill.openjill.core.api.message.EnumMessageType;
import org.jill.openjill.core.api.message.InterfaceMessageGameHandler;

/**
 * Skull object.
 *
 * @author Emeric MARTINEAU
 */
public final class SkullManager extends AbstractParameterObjectEntity
    implements InterfaceMessageGameHandler {
    /**
     * Picture array.
     */
    private BufferedImage[] images;

    /**
     * Picture array.
     */
    private BufferedImage fixedImages;

    /**
     * Default constructor.
     *
     * @param objectParam object parameter
     */
    @Override
    public void init(final ObjectParam objectParam) {
        super.init(objectParam);

        int tileIndex = getConfInteger("fixedTile");
        int tileSetIndex = getConfInteger("fixedTileSet");

        this.fixedImages = this.pictureCache.getImage(tileSetIndex, tileIndex);

        tileIndex = getConfInteger("tile");
        tileSetIndex = getConfInteger("tileSet");

        int numberTileSet = getConfInteger("numberTileSet");
        int skullMax = getConfInteger("skullMax");

        loadSkullImage(numberTileSet, tileSetIndex, tileIndex, skullMax);

        drawEye(tileSetIndex, "eyeMinTile", "eyeMaxTile", "eyeLeftStart",
                "eyeLeftX", "eyeLeftY");
        drawEye(tileSetIndex, "eyeMinTile", "eyeMaxTile", "eyeRightStart",
                "eyeRightX", "eyeRightY");

        messageDispatcher.addHandler(EnumMessageType.TRIGGER, this);
    }

    private void drawEye(final int tileSetIndex, final String eyeMinTileStr,
            final String eyeMaxTileStr, final String eyeStartStr,
            final String eyeXstr, final String eyeYstr) {
        int eyeMinTile = getConfInteger(eyeMinTileStr);
        int eyeMaxTile = getConfInteger(eyeMaxTileStr);
        BufferedImage eye;
        int tileIncrement;

        int tileEye = getConfInteger(eyeStartStr);
        int eyeLeftX = getConfInteger(eyeXstr);
        int eyeLeftY = getConfInteger(eyeYstr);
        Graphics2D g2d;

        tileIncrement = 1;

        for (int indexEye = 0; indexEye < this.images.length;
                indexEye++) {
            eye = this.pictureCache.getImage(tileSetIndex, tileEye);
            //System.out.println(tileEye);

            // Draw eye
            g2d = this.images[indexEye].createGraphics();

            g2d.drawImage(eye, eyeLeftX, eyeLeftY, null);

            if ((tileEye == eyeMaxTile && tileIncrement > 0)
                    || (tileEye == eyeMinTile && tileIncrement < 0)) {
                tileIncrement *= -1;
            }

            tileEye += tileIncrement;
        }
    }

    /**
     * Load skull image.
     *
     * @param numberTileSet number of image
     * @param tileSetIndex tile set
     * @param tileIndex tile to start
     * @param skullMax skullMax
     */
    private void loadSkullImage(final int numberTileSet, final int tileSetIndex,
            final int tileIndex, final int skullMax) {
        this.images
            = new BufferedImage[numberTileSet * 2];

        int tileIncrement = 1;
        int tileSkull = 0;

        for (int index = 0; index < this.images.length; index += 2) {
            this.images[index]
                = this.pictureCache.getImage(tileSetIndex,
                        tileIndex + tileSkull);
            this.images[index + 1] = this.images[index];

            if (tileSkull == skullMax) {
                tileIncrement *= -1;
            }

            tileSkull += tileIncrement;
        }
    }

    @Override
    public BufferedImage msgDraw() {
        BufferedImage img;

        if (getState() == 0) {
            img = this.fixedImages;
        } else {
            img = this.images[getStateCount()];
        }

        return img;
    }

    /**
     * Call to update.
     */
    @Override
    public void msgUpdate(final KeyboardLayout keyboardLayout) {
        if (getState() != 0) {
            setStateCount(getStateCount() + 1);

            if (getStateCount() >= this.images.length) {
                setStateCount(0);
            }
        }
    }

    @Override
    public void recieveMessage(EnumMessageType type, Object msg) {
        switch (type) {
            case TRIGGER:
                if (getySpeed() == 0) {
                    final ObjectEntity switchObj = (ObjectEntity) msg;
                    if (switchObj.getCounter() == this.counter) {
                        // Start animation
                        this.setState(1);
                    }
                }
                break;
            default:
        }
    }
}