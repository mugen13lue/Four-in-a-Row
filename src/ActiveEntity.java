import processing.core.PImage;
import java.util.List;

public abstract class ActiveEntity extends Entity
{
    //instance vars
    private int actionPeriod;

    //constructor
    public ActiveEntity(Point position, List<PImage> images, int actionPeriod)
    {
        super(position, images);
        this.actionPeriod = actionPeriod;
    }
    protected abstract void executeActivity(WorldModel world,
                                ImageStore imageStore, EventScheduler scheduler) ;
    protected void scheduleAction(EventScheduler scheduler,
                               WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                new Activity( this, world, imageStore),
                actionPeriod);
    }
    protected int getActionPeriod() {return actionPeriod;}
}