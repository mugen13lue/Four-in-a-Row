import jdk.internal.cmm.SystemResourcePressureImpl;
import processing.core.PImage;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public abstract class GamePiece extends Movable
{
    //static Vars
    private static final int GAMEPIECE_ACTION_PERIOD = 5;
    private static final int GAMEPIECE_ANIMATION_PERIOD = 6;
    private static final int GAMEPIECE_ANIMATION_REPEAT_COUNT = 0;
    private static final String WINNER1_KEY = "winner1";
    private static final String WINNER2_KEY = "winner2";
    private static final String PLAYER_WINNER1_KEY = "player1winner1";
    private static final String PLAYER_WINNER2_KEY = "player2winner2";
    private static final boolean WIN_KEY = true;
    private static final int START_SEARCH_VAL = 0;


    //instance var
    private boolean won;
    private ArrayList<Entity> winners = new ArrayList<>();
    private boolean winnersDisplayed;

    public GamePiece(Point position, List<PImage> images, int actionPeriod,
                     int animationPeriod, int animationRepeatCount, boolean won,boolean winnersDisplayed)
    {
        super(position, images, actionPeriod,  animationPeriod, animationRepeatCount);
        this.won = won;
        this.winnersDisplayed = winnersDisplayed;
    }

    protected void executeActivity(WorldModel world,
                                   ImageStore imageStore, EventScheduler scheduler)
    {//design the implementation of finding a connect 4 here.
        Point nextPos = nextPosition(world, getPosition());

        if (moveTo(world, scheduler, nextPos) || getPosition().equals(nextPos))
        {

            ArrayList<Entity> couldbethewinners = new ArrayList<>();
            search(world, imageStore, scheduler, getPosition(), START_SEARCH_VAL, couldbethewinners);
            if (won && (world.getOccupancyCell(new Point (0,0)) == null || world.getOccupancyCell(new Point (0,0)).getClass() != Sign.class
                    || world.getOccupancyCell(new Point (0,0)) instanceof GamePiece))
            {
                System.out.println("WInner");
                if (getClass() == Player1.class) {//I HAVE THE ABILITY TO MAKE THE WIN ANIMATED LIKE A GIF.
                    Sign Victory = new Sign(new Point(0,0),
                            imageStore.getImageList(WINNER1_KEY), GAMEPIECE_ACTION_PERIOD,GAMEPIECE_ANIMATION_PERIOD, GAMEPIECE_ANIMATION_REPEAT_COUNT);
                    world.addEntity(Victory);
                }
                else {
                    Sign Victory = new Sign(new Point(0,0),
                            imageStore.getImageList(WINNER2_KEY), GAMEPIECE_ACTION_PERIOD,GAMEPIECE_ANIMATION_PERIOD, GAMEPIECE_ANIMATION_REPEAT_COUNT);
                    world.addEntity(Victory);
                }

            }

        }

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
    getActionPeriod());
    }

    protected Point nextPosition(WorldModel world, Point destPos)
    {
        Point nextPos = new Point(getPosition().x, getPosition().y + 1);
        if (world.withinBounds(nextPos) && !world.isOccupied(nextPos))
            return nextPos;
        return getPosition();

    }

    protected void search(WorldModel world,
                          ImageStore imageStore, EventScheduler scheduler
                          , Point pos, int val, ArrayList<Entity> couldbethewinners)
    {
        if (!won)
        {
            couldbethewinners.clear();
            if (searchVertical(world, pos, val, couldbethewinners) >= 4) {
                System.out.println("wonVert");
                for (Entity ent : couldbethewinners) {
                    winners.add(ent);
                }
                System.out.println(winners);
                won = true;
            }
            couldbethewinners.clear();
            if (searchHorizontal(world, pos, val, couldbethewinners) >= 4) {
                System.out.println("wonHoriz");
                for (Entity ent : couldbethewinners) {
                    winners.add(ent);
                }
                won = true;
            }
            couldbethewinners.clear();
            if (searchDiagonalTopLeftToBottomRight(world, pos, val, couldbethewinners) >= 4) {
                System.out.println("wonDiagonLeft");
                for (Entity ent : couldbethewinners) {
                    winners.add(ent);
                }
                won = true;
            }
            couldbethewinners.clear();
            if (searchDiagonalTopRightToBottomLeft(world, pos, val, couldbethewinners) >= 4) {
                System.out.println("wonDiagonRight");
                for (Entity ent : couldbethewinners) {
                    winners.add(ent);
                }
                won = true;
            }
        }
        else if (!winnersDisplayed)
        {
            for (Entity wow : winners)
            {
                ((GamePiece)wow).transform(world, imageStore, scheduler);

            }
            winnersDisplayed = true;
        }
    }

    protected int searchVertical(WorldModel world, Point pos, int val, ArrayList<Entity> potentialWinners)
    {
        Optional<Entity> searchEntity = world.getOccupant(pos);
        if (!searchEntity.isPresent())
        {
            return 0;
        }
        else if (getClass().equals(searchEntity.get().getClass()))
        {
            potentialWinners.add(world.getOccupancyCell(pos));
            val = 1 + searchVertical(world, new Point(pos.x, pos.y+1), val,  potentialWinners);
        }

        return val;
    }

    protected int searchHorizontal(WorldModel world, Point pos, int val, ArrayList<Entity> potentialWinners)
    {
        Optional<Entity> searchEntity = world.getOccupant(pos);
        if (!searchEntity.isPresent())
        {
            return 0;
        }
        else if (getClass().equals(searchEntity.get().getClass()) && world.getOccupancyCell(getPosition()) != null)
        {
            potentialWinners.add(world.getOccupancyCell(pos));
            val = 1 + searchHorizontal(world, new Point(pos.x+1, pos.y), val,  potentialWinners);
        }

        return val;
    }

    protected int searchDiagonalTopLeftToBottomRight(WorldModel world, Point pos, int val, ArrayList<Entity> potentialWinners)
    {
        Optional<Entity> searchEntity = world.getOccupant(pos);
        if (!searchEntity.isPresent())
        {
            return 0;
        }
        else if (getClass().equals(searchEntity.get().getClass()) && world.getOccupancyCell(getPosition()) != null)
        {
            potentialWinners.add(world.getOccupancyCell(pos));
            val = 1 + searchDiagonalTopLeftToBottomRight(world, new Point(pos.x+1, pos.y+1), val, potentialWinners);
        }

        return val;
    }

    protected int searchDiagonalTopRightToBottomLeft(WorldModel world, Point pos, int val, ArrayList<Entity> potentialWinners)
    {
        Optional<Entity> searchEntity = world.getOccupant(pos);
        if (!searchEntity.isPresent())
        {
            return 0;
        }
        else if (getClass().equals(searchEntity.get().getClass()) && world.getOccupancyCell(getPosition()) != null)
        {
            potentialWinners.add(world.getOccupancyCell(pos));
            val = 1 + searchDiagonalTopRightToBottomLeft(world, new Point(pos.x-1, pos.y+1), val,potentialWinners);
        }

        return val;
    }

    protected void transform(WorldModel world,
                             ImageStore imageStore, EventScheduler scheduler)
    {
        Point thePos = getPosition();
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        if (getClass() == Player1.class) {
            Player1 winn = new Player1(thePos, imageStore.getImageList(PLAYER_WINNER1_KEY),GAMEPIECE_ACTION_PERIOD,
                    GAMEPIECE_ANIMATION_PERIOD, GAMEPIECE_ANIMATION_REPEAT_COUNT, GamePiece.WIN_KEY, GamePiece.WIN_KEY);
            world.addEntity(winn);
            winn.scheduleAction(scheduler, world, imageStore);
        }
        else {
            Player2 winn = new Player2(thePos, imageStore.getImageList(PLAYER_WINNER2_KEY),GAMEPIECE_ACTION_PERIOD,
                    GAMEPIECE_ANIMATION_PERIOD, GAMEPIECE_ANIMATION_REPEAT_COUNT, GamePiece.WIN_KEY, GamePiece.WIN_KEY);
            world.addEntity(winn);
            winn.scheduleAction(scheduler, world, imageStore);
        }
    }

}