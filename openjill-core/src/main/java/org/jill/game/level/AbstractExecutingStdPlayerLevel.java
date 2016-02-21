package org.jill.game.level;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jill.game.config.JillGameConfig;
import org.jill.game.config.ObjectInstanceFactory;
import org.jill.game.entities.obj.player.AbstractPlayerManager;
import org.jill.game.entities.obj.player.PalyerActionPerState;
import org.jill.game.entities.obj.player.PlayerAction;
import org.jill.game.level.cfg.LevelConfiguration;
import org.jill.game.manager.object.weapon.ObjectMappingWeapon;
import org.jill.openjill.core.api.message.statusbar.StatusBarTextMessage;
import org.jill.openjill.core.api.message.statusbar.inventory.
        EnumInventoryObject;
import org.jill.openjill.core.api.message.statusbar.inventory.
        InventoryItemMessage;
import org.jill.jn.ObjectItem;
import org.jill.openjill.core.api.entities.ObjectEntity;
import org.jill.openjill.core.api.entities.ObjectParam;
import org.jill.openjill.core.api.jill.JillConst;
import org.jill.openjill.core.api.message.EnumMessageType;
import org.simplegame.SimpleGameConfig;

/**
 * Abstract class of level of Jill trilogy.
 *
 * This class manage all of player
 *
 * @author Emeric MARTINEAU
 */
public abstract class AbstractExecutingStdPlayerLevel
    extends AbstractExecutingStdLevel {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
                    AbstractExecutingStdPlayerLevel.class.getName());

    /**
     * Begin level message.
     */
    private StatusBarTextMessage beginMessage;

    /**
     * Minimum size between player and border.
     */
    private static final int BORDER_SCREEN_PLAYER_X = 5
            * JillConst.getBlockSize();

    /**
     * Minimum size between player and border.
     */
    private static final int BORDER_SCREEN_PLAYER_Y = 3
            * JillConst.getBlockSize();

    /**
     * Special screen offset for up/down jill.
     */
    private int specialScreenOffset = 0;

    /**
     * Move screen state of player.
     */
    private final int playerStateMoveScreen;

    /**
     * Yd value to move up.
     */
    private final int playerYdUpMoveScreen;

    /**
     * Yd value to move down.
     */
    private final int playerYdDownMoveScreen;

    /**
     * Game width.
     */
    private final int gameWidth;

    /**
     * Game height.
     */
    private final int gameHeight;

    /**
     * Create message for status bar.
     *
     * @param prop propertoies
     * @param key key to search
     *
     * @return status bar
     */
    private static StatusBarTextMessage createSatusBarMessage(
            final Properties prop,
            final String key) {
        return new StatusBarTextMessage(prop.getProperty(key + ".msg"),
                Integer.valueOf(prop.getProperty(key + ".duration")),
                Integer.valueOf(prop.getProperty(key + ".color")));
    }

    /**
     * Level configuration.
     *
     * @param cfgLevel  configuration of level
     *
     * @throws IOException if error of reading file
     * @throws ClassNotFoundException if not class found
     * @throws IllegalAccessException if error
     * @throws InstantiationException  if error
     */
    public AbstractExecutingStdPlayerLevel(final LevelConfiguration cfgLevel)
            throws IOException, ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        super(cfgLevel);

        this.gameWidth = this.statusBar.getGameAreaConf().getWidth();
        this.gameHeight = this.statusBar.getGameAreaConf().getHeight();

        if (cfgLevel.getDisplayBeginMessage()) {
            beginMessage();
        }

        this.playerStateMoveScreen
            = ((JillGameConfig) SimpleGameConfig.getInstance()).
                    getPlayerMoveScreenState();

        this.playerYdUpMoveScreen
            = ((JillGameConfig) SimpleGameConfig.getInstance()).
                    getPlayerMoveScreenYdUp();

        this.playerYdDownMoveScreen
            = ((JillGameConfig) SimpleGameConfig.getInstance()).
                    getPlayerMoveScreenYdDown();
    }

    /**
     * Display begin message.
     */
    private void beginMessage() {
        try {
            Properties prop = new Properties();
            InputStream is =
                    AbstractExecutingStdPlayerLevel.class.getClassLoader().
                            getResourceAsStream("messages.properties");

            prop.load(is);

            this.beginMessage = createSatusBarMessage(prop, "beginLevel");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error at loading", ex);
        }

        this.messageDispatcher.sendMessage(EnumMessageType.MESSAGE_STATUS_BAR,
                this.beginMessage);
    }

    @Override
    protected void movePlayer() {
        // Move player

        // NOTE : Disable in 0.0.28 cause, new collision method do this
        //player.msgKeyboard(player, keyboardLayout);
        //player.msgUpdate();

        computeMoveScreen();

        centerScreen();

        keyboardLayout.clear();

        // Send msgTouch to background
        final int playerX = this.player.getX();
        final int playerY = this.player.getY();
        final int playerWidth = this.player.getWidth();
        final int playerHeight = this.player.getHeight();
        final int blockSize = JillConst.getBlockSize();

        final int startBlockX = playerX / blockSize;
        final int endBlockX = (playerX + playerWidth) / blockSize;

        final int startBlockY = playerY / blockSize;
        final int endBlockY = (playerY + playerHeight) / blockSize;

        for (int indexX = startBlockX; indexX < endBlockX; indexX++) {
            for (int indexY = startBlockY; indexY < endBlockY; indexY++) {
                this.backgroundObject[indexX][indexY].msgTouch(this.player);
            }
        }

    }

    /**
     * Compute the special offset of screen when player no move and up/down.
     */
    private void computeMoveScreen() {
        int ySpeed = this.player.getySpeed();

        // Player is Stand and not move, and plalyer in down or head up
        if (this.player.getState() == this.playerStateMoveScreen
                && ySpeed != 0) {
            if (ySpeed >= this.playerYdDownMoveScreen
                    && this.specialScreenOffset < BORDER_SCREEN_PLAYER_Y) {
                this.specialScreenOffset++;
            } else if (ySpeed <= this.playerYdUpMoveScreen
                    && this.specialScreenOffset > 0) {
                this.specialScreenOffset--;
            }

        }
    }

    /**
     * Center screen with player position.
     */
    protected void centerScreen() {
        final int playerXLeft = player.getX();
        final int playerXRight = playerXLeft + player.getWidth();

        // Move screen to X to let at right or left 4 case
        if (offsetX + BORDER_SCREEN_PLAYER_X > playerXLeft) {
            offsetX = -1 * Math.max(
                    playerXLeft - BORDER_SCREEN_PLAYER_X,
                    // To don't have negative value
                    0);
        } else if (offsetX - BORDER_SCREEN_PLAYER_X
                + this.gameWidth < playerXRight) {
            offsetX = -1 * Math.min(
                    playerXRight + BORDER_SCREEN_PLAYER_X
                    - this.gameWidth,
                    JillConst.getMaxWidth() - this.gameWidth);
        }

        final int playerYTop = player.getY();
        final int playerYBottom = playerYTop + player.getHeight();

        // Move screen to Y to let at up/down 2 case
        if (offsetY + BORDER_SCREEN_PLAYER_Y > playerYTop) {
            offsetY = -1 * Math.max(
                    playerYTop - BORDER_SCREEN_PLAYER_Y - specialScreenOffset,
                    // To don't have negative value
                    0);
        } else if (offsetY - BORDER_SCREEN_PLAYER_Y
                + this.gameHeight < playerYBottom) {
            offsetY = -1 * Math.min(
                    playerYBottom + BORDER_SCREEN_PLAYER_Y
                    - this.gameHeight + specialScreenOffset,
                    JillConst.getMaxHeight() - this.gameHeight);
        }
    }



    @Override
    protected final void doPlayerFire() {
        // Check if player can fire !
        if (PalyerActionPerState.canDo(this.player.getState(),
                PlayerAction.CANFIRE)) {

            // Get inventory
            final List<EnumInventoryObject> listInv =
                    this.inventoryArea.getObjects();

            // Get weapon
            final ObjectMappingWeapon[] weaponsList =
                    this.objectCache.getTypeOfInventoryWeapon();
            
            // Current weapon
            ObjectMappingWeapon currentWeapon;
            // Current inventory
            EnumInventoryObject currentInventory;
            
            // Search weapon from end to start
            for (int indexWeapon = weaponsList.length - 1; indexWeapon >= 0;
                    indexWeapon--) {
                currentWeapon = weaponsList[indexWeapon];
                
                currentInventory = EnumInventoryObject.valueOf(
                        currentWeapon.getInventoryKey());
                
                // Check if weapon found in inventory
                if (listInv.contains(currentInventory)) {
                    // Weapon is in inventory
                    // Cheack if can fire with this weapon
                    if (checkWeapon(listInv, currentInventory)) {
                        createWeapon(currentWeapon.getType());
                        
                        break;
                    }
                }
            }
        }
    }

    /**
     * Check if weapon knife.
     *
     * @param listInv list of inventory
     * @param currentInventory weapon inventory
     *
     * @return true if can create weapon
     */
    private boolean checkWeapon(
            final List<EnumInventoryObject> listInv,
            final EnumInventoryObject currentInventory) {

        boolean canFireThisWeapon;

        if (this.player.getInfo1()
                == AbstractPlayerManager.X_SPEED_MIDDLE) {
            canFireThisWeapon = false;
        } else {
            canFireThisWeapon = true;
            
            // Check if last objet to clear ALT text
            final int nbWeapon = Collections.frequency(listInv,
                    currentInventory);

            // Remove object in inventory list
            this.messageDispatcher.sendMessage(
                    EnumMessageType.INVENTORY_ITEM,
                    new InventoryItemMessage(currentInventory, false,
                    nbWeapon == 1, false));
        }
        
        return canFireThisWeapon;
    }

    /**
     * Create weapon.
     * 
     * @param typeWeapon weapon object type
     */
    private void createWeapon(final int typeWeapon) {
        // Object parameter
        final ObjectParam objParam = ObjectInstanceFactory.getNewObjParam();
        objParam.init(this.backgroundObject,
            this.pictureCache, this.messageDispatcher,
            this.levelConfiguration.getLevelNumber());

        final ObjectItem weapon = ObjectInstanceFactory.getNewObjectItem();

        weapon.setType(typeWeapon);

        objParam.setObject(weapon);

        weapon.setxSpeed(player.getxSpeed());

        weapon.setX(player.getX());
        weapon.setY(player.getY());
        weapon.setInfo1(player.getInfo1());

        // Get jill object
        final ObjectEntity obj = this.objectCache.getNewObject(objParam);

        // Add object in list
        if (obj != null) {
            this.listObject.add(obj);
        }
    }
}