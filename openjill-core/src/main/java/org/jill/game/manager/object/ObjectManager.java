package org.jill.game.manager.object;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jill.game.manager.object.weapon.ObjectMappingWeapon;
import org.jill.openjill.core.api.entities.ObjectEntity;
import org.jill.openjill.core.api.entities.ObjectParam;

/**
 * Object manager.
 *
 * @author Emeric MARTINEAU
 */
public final class ObjectManager {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
                    ObjectManager.class.getName());

    /**
     * Config file name.
     */
    private static final String CONFIG_FILENAME =
            "objects_manager_mapping.json";

    /**
     * Instance of this object.
     */
    private static final ObjectManager INSTANCE = new ObjectManager();

    /**
     * Map of object tile.
     */
    private Map<String, Class<ObjectEntity>> mapObjectClass;

    /**
     * Map between name of inventory and id of object.
     */
    private ObjectMappingWeapon[] listWeapon;
    
    /**
     * Return this instance of object.
     *
     * @return instance
     */
    public static ObjectManager getInstance() {
        return INSTANCE;
    }

    /**
     * Load properties.
     *
     * @return properties
     */
    private static List<ObjectMapping> loadObjectTitle() {
        final ObjectMapper mapper = new ObjectMapper();
        final InputStream is = ObjectManager.class.
                getClassLoader().getResourceAsStream(CONFIG_FILENAME);

        List<ObjectMapping> mapObjectTile;

        try {
            mapObjectTile = mapper.readValue(is,
                    new TypeReference<List<ObjectMapping>>() { });
        } catch (final IOException ex) {
            LOGGER.log(Level.SEVERE,
                "Error, can't load properties file where "
                + "mapping objects and manager", ex);

            mapObjectTile = null;
        }

        return mapObjectTile;
    }

    /**
     * Private constructor.
     */
    private ObjectManager() {
        try {
            mapObjectClass = initMapOfObjectSprite();
        } catch (ReflectiveOperationException ex) {
            LOGGER.log(Level.SEVERE, "Error loading object", ex);
        }
    }

    /**
     * Init picture of know object.
     *
     * @return map to link object name and class
     *
     * @throws ClassNotFoundException if error
     * @throws InstantiationException if error
     * @throws IllegalAccessException if error
     */
    @SuppressWarnings("unchecked")
    private Map<String, Class<ObjectEntity>> initMapOfObjectSprite()
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        // Load mapping
        final List<ObjectMapping> mapObjectTile = loadObjectTitle();

        // Map between key and manager
        final Map<String, Class<ObjectEntity>> mapObjectNamePicture =
                new HashMap<>();

        final Map<String, ObjectMappingWeapon> mapInventoryWeapon = new HashMap<>();

        String key;
        ObjectMappingWeapon currentWeapon;
        Class<ObjectEntity> c;

        for (ObjectMapping om : mapObjectTile) {
            key = String.valueOf(om.getType());

            c = (Class<ObjectEntity>) Class.forName(
                    om.getImplementationClass());

            mapObjectNamePicture.put(key, c);

            if (om.getWeapon() != null) {
                currentWeapon = om.getWeapon();
                currentWeapon.setType(om.getType());
                
                mapInventoryWeapon.put(currentWeapon.getInventoryKey(),
                        currentWeapon);
            }
        }

        this.listWeapon = new ObjectMappingWeapon[mapInventoryWeapon.size()];
        
        for (String weaponKey : mapInventoryWeapon.keySet()) {
            currentWeapon = mapInventoryWeapon.get(weaponKey);
            
            this.listWeapon[currentWeapon.getOrder() - 1] = currentWeapon;
        }
        
        return mapObjectNamePicture;
    }

    /**
     * Create a object.
     *
     * @param className class name
     * @param objectParam  object
     *
     * @return object
     */
    private ObjectEntity createObject(final Class<ObjectEntity> className,
            final ObjectParam objectParam) {
        ObjectEntity o = null;

        try {
            o = (ObjectEntity) className.
                getConstructor().
                newInstance();

            o.init(objectParam);
        } catch (IllegalArgumentException | SecurityException |
                InvocationTargetException | NoSuchMethodException |
                IllegalAccessException | InstantiationException ex) {
            LOGGER.log(Level.SEVERE,
                "Create jill object error !", ex);
        }

        return o;
    }


    /**
     * Create an jill object.
     *
     * @param objectParam object in file of level
     *
     * @return object
     */
    public ObjectEntity getNewObject(final ObjectParam objectParam) {
        ObjectEntity o = null;

        final Class<ObjectEntity> className = mapObjectClass.get(
                String.valueOf(objectParam.getObject().getType()));

        if (className != null) {
            o = createObject(className, objectParam);
        }

        return o;
    }

    /**
     * Return id of type.
     * 
     * @return list of weapon
     */
    public ObjectMappingWeapon[] getTypeOfInventoryWeapon() {
        return this.listWeapon;
    }
}
