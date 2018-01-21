/*
 * SpriteCache.java
 *
 * Created on 2 de Julho de 2006, 20:01
 *
 */

package antsystem.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author  Danilo N Costa
 */
public class SpriteCache {
    private Map<String,BufferedImage> loadedImages = new HashMap<String, BufferedImage>();
    private String imageBase;
    /** Creates a new instance of SpriteCache */
    public SpriteCache(String imageBase) {
        this.imageBase = imageBase;
    }
    
    private BufferedImage loadImage(String imagePath){
        URL url = this.getClass().getResource(imagePath);
        try {
            
            return ImageIO.read(url);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public BufferedImage getSprite(String name){
        BufferedImage sprite = null;
        sprite = loadedImages.get(name);
        if(sprite==null){
            sprite = loadImage(imageBase+name);
            loadedImages.put(name, sprite);
        }
        return sprite;
    }
    
}
