package org.jill.game.entities.obj;

import java.awt.image.BufferedImage;
import java.util.Optional;

import org.jill.game.entities.obj.abs.AbstractHitPlayerObjectEntity;
import org.jill.game.entities.obj.player.PlayerState;
import org.jill.game.entities.obj.util.UtilityObjectEntity;
import org.jill.openjill.core.api.entities.BackgroundEntity;
import org.jill.openjill.core.api.entities.ObjectEntity;
import org.jill.openjill.core.api.entities.ObjectParam;
import org.jill.openjill.core.api.jill.JillConst;
import org.jill.openjill.core.api.keyboard.KeyboardLayout;
import org.jill.openjill.core.api.message.EnumMessageType;
import org.jill.openjill.core.api.message.InterfaceMessageGameHandler;
import org.jill.openjill.core.api.message.object.ObjectListMessage;
import org.jill.openjill.core.api.message.statusbar.inventory.InventoryLifeMessage;

/**
 * Bonus item manager.
 *
 * @author Emeric MARTINEAU
 */
public final class FallingSpikeManager extends AbstractHitPlayerObjectEntity
        implements InterfaceMessageGameHandler {

    /**
     * Picture array.
     */
    private Optional<BufferedImage> images;

    /**
     * To remove this object from object list.
     */
    private ObjectListMessage killme;

    /**
     * Size of falling.
     */
    private int fallingSpeed;

    /**
     * Falling speed max.
     */
    private int fallingSpeedMax;

    /**
     * Background map.
     */
    private BackgroundEntity[][] backgroundObject;

    /**
     * Default constructor.
     *
     * @param objectParam object paramter
     */
    @Override
    public void init(final ObjectParam objectParam) {
        super.init(objectParam);

        this.messageDispatcher.addHandler(EnumMessageType.TRIGGER, this);

        // Init list of picture
        int tileIndex = getConfInteger("tile");
        int tileSetIndex = getConfInteger("tileSet");

        this.images = this.pictureCache.getImage(tileSetIndex, tileIndex);

        this.fallingSpeed = getConfInteger("fallingSpeed");
        this.fallingSpeedMax = getConfInteger("fallingSpeedMax");

        this.killme = new ObjectListMessage(this, false);

        this.backgroundObject = objectParam.getBackgroundObject();
    }

    @Override
    public Optional<BufferedImage> msgDraw() {
        return this.images;
    }

    @Override
    public void msgTouch(final ObjectEntity obj,
            final KeyboardLayout keyboardLayout) {
        if (obj.isPlayer()) {

            if (this.ySpeed != 0) {
                // Spike fall
                hitPlayerRock(obj);
            } else {
                // Spike don't move, kill player.
                obj.msgKill(
                        this.backgroundObject[this.x / JillConst.getBlockSize()]
                                [this.y / JillConst.getBlockSize()],
                        InventoryLifeMessage.DEAD_MESSAGE,
                        PlayerState.DIE_SUB_STATE_OTHER_BACK);
            }
            this.messageDispatcher.sendMessage(EnumMessageType.OBJECT,
                    this.killme);
        }
    }

    @Override
    public void recieveMessage(final EnumMessageType type,
            final Object msg) {
        switch (type) {
            case TRIGGER:
                final ObjectEntity switchObj = (ObjectEntity) msg;
                if (switchObj.getCounter() == this.counter) {
                    // Start fall
                    setySpeed(this.fallingSpeed);
                }

                // Remove source message
                this.messageDispatcher.sendMessage(EnumMessageType.OBJECT,
                        new ObjectListMessage(switchObj, false));

                break;
            default:
        }
    }

    @Override
    public void msgUpdate(final KeyboardLayout keyboardLayout) {
        if (this.ySpeed > 0) {

            if (UtilityObjectEntity.moveObjectDown(this, this.ySpeed,
                    this.backgroundObject)) {
                if (this.ySpeed < this.fallingSpeedMax) {
                    this.ySpeed += this.fallingSpeed;
                }
            } else {
                this.messageDispatcher.sendMessage(EnumMessageType.OBJECT,
                        this.killme);
            }
        }
    }
}
