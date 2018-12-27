import processing.core.PImage;

import java.util.List;

public class Sign extends Animated
{
    public Sign(Point position, List<PImage> images, int actionPeriod,
                int animationPeriod, int animationRepeatCount)
    {
        super(position, images, actionPeriod, animationPeriod, animationRepeatCount);
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }
}