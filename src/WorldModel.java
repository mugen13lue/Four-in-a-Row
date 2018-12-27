import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import processing.core.PApplet;
import processing.core.PImage;

final class WorldModel
{
   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;

   private static final String ORE_ID_PREFIX = "ore -- ";
   private static final int ORE_CORRUPT_MIN = 20000;
   private static final int ORE_CORRUPT_MAX = 30000;
   private static final int ORE_REACH = 1;


   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public void addEntity( Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity( Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds( pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt( pos);
         setOccupancyCell( pos, entity);
         entity.setPosition(pos);
      }
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows &&
              pos.x >= 0 && pos.x < numCols;
   }

   //public boolean

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   protected Entity getOccupancyCell( Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   protected void setOccupancyCell( Point pos, Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition())) {

         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }
      addEntity(entity);
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++)
      {
         for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }
      return Optional.empty();
   }

   public void removeEntity( Entity entity)
   {
      removeEntityAt( entity.getPosition());
   }

   private void removeEntityAt( Point pos)
   {
      if (withinBounds( pos)
              && getOccupancyCell( pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell( pos, null);
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(background[pos.y][pos.x].getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public void setBackground(Point pos, Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public static boolean neighbors(Point p1, Point p2)
   {
      return p1.x+1 == p2.x && p1.y == p2.y ||
              p1.x-1 == p2.x && p1.y == p2.y ||
              p1.x == p2.x && p1.y+1 == p2.y ||
              p1.x == p2.x && p1.y-1 == p2.y;
   }
   public static boolean diagonalNeighbors(Point p1, Point p2)
   {
      return p1.x+1 == p2.x && p1.y+1 == p2.y ||
              p1.x-1 == p2.x && p1.y-1 == p2.y ||
              p1.x-1 == p2.x && p1.y+1 == p2.y ||
              p1.x+1 == p2.x && p1.y-1 == p2.y;
   }


   private Background getBackgroundCell(Point pos)
   {
      return background[pos.y][pos.x];
   }

   private void setBackgroundCell(Point pos,Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public int getNumRows() {return numRows;}
   public int getNumCols() {return numCols;}
   public Background[][] getBackground() {return background;}
   public Entity[][] getOccupancy() {return occupancy;}
   public Set<Entity> getEntities() {return entities;}

}
