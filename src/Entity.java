import processing.core.PImage;
import java.util.List;

public abstract class Entity
{
   //Instance Vars
   private Point position;
   private List<PImage> images;
   private int imageIndex;

   //Constructor
   public Entity(Point position, List<PImage> images)
   {
      this.position = position;
      this.images = images;
   }

   //Protected methods
   protected PImage getCurrentImage() {return images.get(imageIndex);}
   protected Point getPosition() {return position;}
   protected void setPosition(Point pos)  {position = pos;}
   protected void setImageIndex(int i) {imageIndex = i;}
   protected int getImageIndex() {return imageIndex;}
   protected List<PImage> getImages() {return images;}
}
