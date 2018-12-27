import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import processing.core.*;
import java.net.URL;

public final class VirtualWorld
   extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;

   //change pixel diemnsions for how big your screen is or how large you want the board to be. 21x21 squares
   private static final int VIEW_WIDTH = 680;//640
   private static final int VIEW_HEIGHT = 680;//480
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   //change below to get a movable window
   private static final int WORLD_WIDTH_SCALE = 1;//2
   private static final int WORLD_HEIGHT_SCALE = 1;//2

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_NAME = "gaia.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static final int PROPERTY_KEY = 0;

   private static final String BGND_KEY = "background";
   private static final int BGND_NUM_PROPERTIES = 4;
   private static final int BGND_ID = 1;
   private static final int BGND_COL = 2;
   private static final int BGND_ROW = 3;

   private static final int GAMEPIECE_ACTION_PERIOD = 5;
   private static final int GAMEPIECE_ANIMATION_PERIOD = 6;
   private static final int GAMEPIECE_ANIMATION_REPEAT_COUNT = 0;

   private static final boolean NOT_WIN_KEY = false;
   private static final String PLAYER1_KEY = "player1";
   private static final String PLAYER2_KEY = "player2";



   private static double timeScale = 1.0;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;
   private boolean Player1Turn = true;


   private long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */



   public void keyPressed()
   {
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;

         switch (keyCode)
         {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;
         }
         view.shiftView(dx, dy);
      }
   }

   public void setup()
   {
      this.imageStore = new ImageStore(
              createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
              createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
              TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }
      view.drawViewport();
   }

   private static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
              imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   private static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
         //System.out.println("Loading images!");
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         load(in, world, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }
   public static void load(Scanner in, WorldModel world, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), world, imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   private static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities())
      {
         if (entity instanceof ActiveEntity) {

            ((ActiveEntity) entity).scheduleAction(scheduler, world, imageStore);// can i caste an interface
         }

      }
   }

   private static boolean processLine(String line, WorldModel world,
                                      ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties, world, imageStore);
         }
      }

      return false;
   }

   // added the mousepressed function from PathingMain into this file.
   //hypothetically just need to create a function that if mousepressed : create.
   public void mousePressed()
   {
      Point pressed = mouseToPoint(mouseX, mouseY);

      if (world.getOccupancyCell(new Point(0,0)) == null || world.getOccupancyCell(new Point(0,0)).getClass() != Sign.class)
      {
         pressed = getToTheTop(pressed);
         if (world.withinBounds(pressed)) {
            if(Player1Turn) {
               Player1 piece = new Player1(new Point(pressed.x, pressed.y),
                    imageStore.getImageList(PLAYER1_KEY), GAMEPIECE_ACTION_PERIOD, GAMEPIECE_ANIMATION_PERIOD, GAMEPIECE_ANIMATION_REPEAT_COUNT, NOT_WIN_KEY, NOT_WIN_KEY);
               SpawnGamePiece(piece);
               Player1Turn = false;
            }
            else {
               Player2 piece = new Player2(new Point(pressed.x, pressed.y),
                    imageStore.getImageList(PLAYER2_KEY), GAMEPIECE_ACTION_PERIOD, GAMEPIECE_ANIMATION_PERIOD, GAMEPIECE_ANIMATION_REPEAT_COUNT, NOT_WIN_KEY, NOT_WIN_KEY);
               SpawnGamePiece(piece);
               Player1Turn = true;

            }
         }
      }
      else {
         System.out.println("out o bounds");
      }
   }

   private Point getToTheTop(Point pos)
   {
      if (!world.withinBounds(pos)) {return new Point(-1,-1);}
      else if (world.getOccupancyCell(pos) == null) {return pos;}
      return getToTheTop(new Point(pos.x, pos.y-1));
   }

   //create helper function to spawn ish
   private void SpawnGamePiece(GamePiece GP)
   {
      if (world.getOccupancyCell(GP.getPosition())!= null )
      {
         Entity RemoveMyGuy = world.getOccupancyCell(GP.getPosition());
         world.removeEntity(RemoveMyGuy);
      }

      world.addEntity(GP);
      GP.scheduleAction(scheduler, world, imageStore);
      redraw();
   }

   private Point mouseToPoint(int x, int y)
   {
      return new Point((mouseX) /TILE_WIDTH + view.getViewport().getCol(), (mouseY)/TILE_HEIGHT + view.getViewport().getRow());
   }

   private static boolean parseBackground(String [] properties,
                                          WorldModel world, ImageStore imageStore)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                 Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         world.setBackground(pt, new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
