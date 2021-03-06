package org.jill.game.entities.obj.util;

import org.jill.jn.BackgroundLayer;
import org.jill.openjill.core.api.entities.BackgroundEntity;
import org.jill.openjill.core.api.entities.ObjectEntity;
import org.jill.openjill.core.api.jill.JillConst;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Object utilility class.
 *
 * @author Emeric MARTINEAU
 */
public final class UtilityObjectEntity {

    /**
     * Private constructor.
     */
    private UtilityObjectEntity() {
        // Nothing
    }

    /**
     * Check if object can goto this/there block(s).
     *
     * @param startX           block x start
     * @param endX             block x end
     * @param startY           block y start
     * @param endY             block end y end
     * @param backgroundObject background map
     * @return true if object can move
     */
    private static Optional<BackgroundEntity> checkObjectHitFloor(final int startX,
            final int endX, final int startY, final int endY,
            final BackgroundEntity[][] backgroundObject) {
        return checkObjectHitBlockOrStair(startX, endX, startY, endY,
                backgroundObject, true, false, true);
    }

    /**
     * Check if object can goto this/there block(s).
     *
     * @param startX           block x start
     * @param endX             block x end
     * @param startY           block y start
     * @param endY             block end y end
     * @param backgroundObject background map
     * @return true if object can move
     */
    private static Optional<BackgroundEntity> checkObjectHitBlockUp(final int startX,
            final int endX, final int startY, final int endY,
            final BackgroundEntity[][] backgroundObject) {
        return checkObjectHitBlockOrStair(startX, endX, endY, startY,
                backgroundObject, false, true, true);
    }

    /**
     * Check if object can goto this/there block(s).
     *
     * @param startX           block x start
     * @param endX             block x end
     * @param startY           block y start
     * @param endY             block end y end
     * @param backgroundObject background map
     * @return true if object can move
     */
    private static Optional<BackgroundEntity> checkObjectHitBlockLeft(final int startX,
            final int endX, final int startY, final int endY,
            final BackgroundEntity[][] backgroundObject) {
        return checkObjectHitBlockOrStair(endX, startX, startY, endY,
                backgroundObject, false, false, false);
    }

    /**
     * Check if object can goto this/there block(s).
     *
     * @param startX           block x start
     * @param endX             block x end
     * @param startY           block y start
     * @param endY             block end y end
     * @param backgroundObject background map
     * @return true if object can move
     */
    private static Optional<BackgroundEntity> checkObjectHitBlockRight(final int startX,
            final int endX, final int startY, final int endY,
            final BackgroundEntity[][] backgroundObject) {
        return checkObjectHitBlockOrStair(startX, endX, startY, endY,
                backgroundObject, false, false, true);
    }

    /**
     * Check if object can goto this/there block(s).
     *
     * @param startX           block x start
     * @param endX             block x end
     * @param startY           block y start
     * @param endY             block end y end
     * @param backgroundObject background map
     * @param checkStair       check also stair
     * @param up               true if search is up, else search is down
     * @param right            true if search is right, else search is left
     * @return true if object can move
     */
    private static Optional<BackgroundEntity> checkObjectHitBlockOrStair(final int startX,
                                                final int endX, final int startY, final int endY,
                                                final BackgroundEntity[][] backgroundObject, final boolean checkStair,
                                                final boolean up, final boolean right) {
        boolean objectCanMove;

        // Index of background for check move
        int indexBackX;
        int indexBackY;

        final int lEndX = Math.min(endX, BackgroundLayer.MAP_WIDTH - 1);
        final int lEndY = Math.min(endY, BackgroundLayer.MAP_HEIGHT - 1);

        BackgroundEntity be;

        // Check if floor
        for (indexBackX = startX; ; ) {
            for (indexBackY = startY; ; ) {
                be = backgroundObject[indexBackX][indexBackY];

                objectCanMove
                        = be.isPlayerThru();

                if (checkStair && objectCanMove) {
                    objectCanMove =
                            !be.isStair();
                }

                if (!objectCanMove) {
                    return Optional.of(be);
                }

                if (up) {
                    indexBackY--;

                    if (indexBackY < lEndY) {
                        break;
                    }
                } else {
                    indexBackY++;

                    if (indexBackY > lEndY) {
                        break;
                    }
                }
            }

            if (right) {
                indexBackX++;

                if (indexBackX > lEndX) {
                    break;
                }
            } else {
                indexBackX--;

                if (indexBackX < lEndX) {
                    break;
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Check if object can goto this/there block(s).
     *
     * @param startX           block x start
     * @param endX             block x end
     * @param startY           block y start
     * @param endY             block end y end
     * @param backgroundObject background map
     * @return true if object can move
     */
    private static Optional<BackgroundEntity> checkObjectHitVine(final int startX,
            final int endX, final int startY, final int endY,
            final BackgroundEntity[][] backgroundObject) {
        boolean isVine;

        // Index of background for check move
        int indexBackX;
        int indexBackY;

        final int lEndX = Math.min(endX, BackgroundLayer.MAP_WIDTH - 1);
        final int lEndY = Math.min(endY, BackgroundLayer.MAP_HEIGHT - 1);

        // Check if floor
        for (indexBackX = startX; indexBackX <= lEndX; indexBackX++) {
            for (indexBackY = startY; indexBackY <= lEndY; indexBackY++) {
                isVine
                        = backgroundObject[indexBackX][indexBackY].isVine();

                if (isVine) {
                    return Optional.of(backgroundObject[indexBackX][indexBackY]);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Check if floor ?
     *
     * @param obj              object
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean checkIfFloorUnderObject(final ObjectEntity obj,
            final BackgroundEntity[][] backgroundObject) {
        return !moveObjectDown(obj, 1, backgroundObject, false, false);
    }

    /**
     * Move object down.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectDown(final ObjectEntity obj,
            final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        return moveObjectDown(obj, mvtSize, backgroundObject, true, false);
    }

    /**
     * Move object down.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectDownWithIgnoreStair(final ObjectEntity obj,
            final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        return moveObjectDown(obj, mvtSize, backgroundObject, true, true);
    }

    /**
     * Move object down.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @param updateObj        update object position ?
     * @param ignoreStair      if ignore stair
     * @return true if can move
     */
    private static boolean moveObjectDown(final ObjectEntity obj,
            final int mvtSize, final BackgroundEntity[][] backgroundObject,
            final boolean updateObj, final boolean ignoreStair) {
        boolean canMove;

        Optional<BackgroundEntity> block = isBlockOrStairAtThisPosition(obj.getX(),
                obj.getY(), obj.getWidth(), obj.getHeight(), mvtSize,
                backgroundObject);

        // Check if block is stair
        if (block.isPresent() && block.get().isStair()) {
            // If stair, object can down if object not upper that block
            final int topOnBlockStair = block.get().getY() * JillConst.getBlockSize();

            // If before move, bottom object is under stair, ignore staire.
            final int bottomObject = obj.getY() + obj.getHeight();

            if (ignoreStair || bottomObject > topOnBlockStair) {
                block = Optional.empty();
            }
        }

        if (!block.isPresent()) {
            if (updateObj) {
                forceMoveUpDown(obj, mvtSize);
            }

            canMove = true;
        } else {
            final int newY = (block.get().getY() * JillConst.getBlockSize())
                    - obj.getHeight();
            if (updateObj) {
                canMove = newY != obj.getY();

                // Jump size are to big and object hit a block
                // Set Y to the below block
                obj.setY(newY);
            } else {
                canMove = newY != obj.getY();
            }

        }

        return canMove;
    }

    /**
     * Move down object. Don't care background.
     *
     * @param obj     object
     * @param mvtSize size of movement
     */
    public static void forceMoveUpDown(final ObjectEntity obj,
            final int mvtSize) {
        int newY = obj.getY() + mvtSize;

        int maxY = JillConst.getMaxHeight() - obj.getHeight();

        // Object cannot go out of map.
        if (newY < 0) {
            obj.setY(0);
        } else if (newY < maxY) {
            obj.setY(newY);
        } else {
            obj.setY(maxY);
        }
    }

    /**
     * Chech is block or stair.
     *
     * @param objX             object x
     * @param objY             object y
     * @param objWidth         object width
     * @param objHeight        object height
     * @param mvtSize          movement size
     * @param backgroundObject background layer
     * @return block if found
     */
    private static Optional<BackgroundEntity> isBlockOrStairAtThisPosition(final int objX,
            final int objY, final int objWidth, final int objHeight,
            final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        // Calculate number of case X
        final int startBlockX = objX / JillConst.getBlockSize();
        final int endBlockX = (objX
                + objWidth - 1) / JillConst.getBlockSize();
        // Object is jumping
        int newPosY = objY + objHeight + mvtSize;
        if (newPosY > JillConst.getMaxHeight()) {
            // Hit top border of screen
            newPosY = JillConst.getMaxHeight() - objHeight;
        }
        //final int objPosY = obj.getY() + obj.getHeight();
        final int newStartY = (objY + objHeight)
                / JillConst.getBlockSize();

        final int newEndY = newPosY / JillConst.getBlockSize();

        Optional<BackgroundEntity> block = Optional.empty();
        Optional<BackgroundEntity> blockStair = Optional.empty();

        // for stair, we need check all block
        for (int testY = newStartY; testY <= newEndY; testY++) {
            block = checkObjectHitFloor(
                    startBlockX, endBlockX, testY, testY, backgroundObject);

            // In case of stair, we take last stair
            if (block.isPresent() && block.get().isStair()) {
                blockStair = block;
            } else if (block.isPresent() && !block.get().isStair()) {
                break;
            }
        }

        if (!block.isPresent()) {
            block = blockStair;
        }

        return block;
    }

    /**
     * Move object right.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectRight(final ObjectEntity obj,
                                          final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        boolean canMove;
        final Optional<BackgroundEntity> back = giveBlockAtRight(obj, mvtSize, backgroundObject);

        if (!back.isPresent()) {
            // Object can't out of map
            obj.setX(
                    Math.min(obj.getX() + mvtSize,
                            JillConst.getMaxWidth() - obj.getWidth()));

            canMove = true;
        } else {
            final int newX = (back.get().getX() * JillConst.getBlockSize())
                    - obj.getWidth();

            canMove = newX != obj.getX();

            obj.setX(newX);
        }

        return canMove;
    }

    /**
     * Give block at right.
     *
     * @param obj object
     * @param mvtSize movement size
     * @param backgroundObject background
     *
     * @return empty or block
     */
    public static Optional<BackgroundEntity> giveBlockAtRight(final ObjectEntity obj, final int mvtSize,
                                                    final BackgroundEntity[][] backgroundObject) {
        // Calculate number of case Y
        final int startBlockY = obj.getY() / JillConst.getBlockSize();
        final int endBlockY = (obj.getY()
                + obj.getHeight() - 1) / JillConst.getBlockSize();

        // Calculate X
        int newPosX = obj.getX() + obj.getWidth() + mvtSize;
        int endBlockX = (newPosX - 1) / JillConst.getBlockSize();

        // Check if can move
        return checkObjectHitBlockRight(endBlockX,
                endBlockX, startBlockY, endBlockY, backgroundObject);
    }

    /**
     * Move object left.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectLeft(final ObjectEntity obj,
                                         final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        boolean canMove;

        // Check if can move
        final Optional<BackgroundEntity> back = giveBlockAtLeft(obj, mvtSize, backgroundObject);

        if (!back.isPresent()) {
            // Object can't have negative X
            obj.setX(
                    Math.max(obj.getX() + mvtSize, 0));

            canMove = true;
        } else {
            final int newX = (back.get().getX() + 1) * JillConst.getBlockSize();

            canMove = newX != obj.getX();

            obj.setX(newX);
        }

        return canMove;
    }

    /**
     * Give block at left.
     *
     * @param obj object
     * @param mvtSize movement size
     * @param backgroundObject background map
     *
     * @return empty or block
     */
    public static Optional<BackgroundEntity> giveBlockAtLeft(final ObjectEntity obj, final int mvtSize,
                                                   final BackgroundEntity[][] backgroundObject) {
        // Calculate number of case Y
        final int startBlockY = obj.getY() / JillConst.getBlockSize();
        final int endBlockY = (obj.getY()
                + obj.getHeight() - 1) / JillConst.getBlockSize();

        // Calculate number of case X
        int newPosX = obj.getX() + mvtSize;
        int startBlockX = Math.max(0, newPosX / JillConst.getBlockSize());

        // Check if can move
        return checkObjectHitBlockLeft(startBlockX,
                startBlockX, startBlockY, endBlockY, backgroundObject);
    }

    /**
     * Move object up.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectUp(final ObjectEntity obj,
            final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        boolean canMove;
        boolean hitBorder = false;

        // Calculate number of case X
        final int startBlockX = obj.getX() / JillConst.getBlockSize();
        final int endBlockX = (obj.getX()
                + obj.getWidth() - 1) / JillConst.getBlockSize();

        // Object is jumping
        int newPosY = obj.getY() + mvtSize;

        if (newPosY < 0) {
            // Hit top border of screen
            newPosY = 0;
            hitBorder = true;
        }

        final int newStartY = newPosY / JillConst.getBlockSize();
        final int newEndY = obj.getY() / JillConst.getBlockSize();

        final Optional<BackgroundEntity> block = checkObjectHitBlockUp(
                startBlockX, endBlockX, newStartY, newEndY, backgroundObject);

        if (!block.isPresent()) {
            obj.setY(newPosY);

            canMove = !hitBorder;
        } else {
            // Jump size are to big and object hit a block
            // Set Y to the below block
            final int newY = (block.get().getY() + 1) * JillConst.getBlockSize();

            canMove = newY != obj.getY();

            obj.setY(newY);
        }

        return canMove;
    }

    /**
     * Check if climb background.
     *
     * @param obj              object
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean isClimbing(final ObjectEntity obj,
            final BackgroundEntity[][] backgroundObject) {

        final Optional<BackgroundEntity> block;

        // Now check player is on same position that block
        final int modX = obj.getX() % JillConst.getBlockSize();

        if (modX == 0) {
            // Calculate number of case X
            final int startBlockX = obj.getX() / JillConst.getBlockSize();
            final int endBlockX = (obj.getX()
                    + obj.getWidth() - 1) / JillConst.getBlockSize();

            final int newStartY = obj.getY() / JillConst.getBlockSize();
            final int newEndY = (obj.getY() + obj.getHeight() - 1)
                    / JillConst.getBlockSize();

            block = checkObjectHitVine(
                    startBlockX, endBlockX, newStartY, newEndY, backgroundObject);
        } else {
            block = Optional.empty();
        }

        return block.isPresent();
    }

    /**
     * Move object left.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectLeftOnFloor(final ObjectEntity obj,
            final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        boolean canMove;

        // Calculate number of case Y
        final int startBlockY = obj.getY() / JillConst.getBlockSize();
        final int endBlockY = (obj.getY()
                + obj.getHeight() - 1) / JillConst.getBlockSize();

        // Calculate number of case X
        int newPosX = obj.getX() + mvtSize;
        int startBlockX = newPosX / JillConst.getBlockSize();

        // Check if can move
        final Optional<BackgroundEntity> back = checkObjectHitBlockLeft(startBlockX,
                startBlockX, startBlockY, endBlockY, backgroundObject);

        if (!back.isPresent()) {
            final int newX = Math.max(newPosX, 0);

            // Now check if floor at new position
            Optional<BackgroundEntity> block = isBlockOrStairAtThisPosition(
                    newX, obj.getY(), 1, obj.getHeight(),
                    1, backgroundObject);

            canMove = block.isPresent();

            if (canMove) {
                // Object can't have negative X
                obj.setX(newX);
            }
        } else {
            final int newX = (back.get().getX() + 1) * JillConst.getBlockSize();

            canMove = newX != obj.getX();

            obj.setX(newX);
        }

        return canMove;
    }

    /**
     * Move object right.
     *
     * @param obj              object
     * @param mvtSize          movement size
     * @param backgroundObject background map
     * @return true if can move
     */
    public static boolean moveObjectRightOnFloor(final ObjectEntity obj,
            final int mvtSize, final BackgroundEntity[][] backgroundObject) {
        boolean canMove;

        // Calculate number of case Y
        final Optional<BackgroundEntity> back = giveBlockAtRight(obj, mvtSize, backgroundObject);

        if (!back.isPresent()) {
            // Object can't out of map
            final int newX = Math.min(obj.getX() + mvtSize,
                    JillConst.getMaxWidth() - obj.getWidth());

            // Now check if floor at new position
            Optional<BackgroundEntity> block = isBlockOrStairAtThisPosition(
                    newX + obj.getWidth() - 1, obj.getY(), 1, obj.getHeight(),
                    1, backgroundObject);

            canMove = block.isPresent();

            if (canMove) {
                // Object can't have negative X
                obj.setX(newX);
            }

        } else {
            final int newX = (back.get().getX() * JillConst.getBlockSize())
                    - obj.getWidth();

            canMove = obj.getX() != newX;

            obj.setX(newX);
        }

        return canMove;
    }

    /**
     * Short way to draw.
     *
     * @param g2d graphic
     * @param img image
     * @param x x
     * @param y y
     */
    public static void draw(Graphics g2d, Image img, int x, int y) {
        g2d.drawImage(img, x, y, null);
    }

    /**
     * Short way to draw.
     *
     * @param dest image
     * @param src
     * @param x x
     * @param y y
     */
    public static void drawFromImage(BufferedImage dest, Image src, int x, int y) {
        final Graphics2D g2d = dest.createGraphics();

        g2d.drawImage(src, x, y, null);

        g2d.dispose();
    }
}
