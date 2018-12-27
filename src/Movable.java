import processing.core.PImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class Movable extends Animated
{
    //constructor
    public Movable(Point position, List<PImage> images, int actionPeriod,
                   int animationPeriod, int animationRepeatCount)
    {
        super(position, images, actionPeriod,  animationPeriod, animationRepeatCount);
    }

    protected Optional<Entity> findNearest(WorldModel world, Point pos, Class entityClass)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : world.getEntities()) {
            if (entity.getClass().equals(entityClass)) {
                ofType.add(entity);
            }
        }
        return nearestEntity(ofType, pos);
    }

    protected Optional<Entity> nearestEntity(List<Entity> entities, Point pos)
    {
        if (entities.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared( pos);

            for (Entity other : entities)
            {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance)
                {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    protected boolean moveTo(WorldModel world, EventScheduler scheduler, Point nextPos)
    {
        if (!getPosition().equals(nextPos))
        {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
                scheduler.unscheduleAllEvents(occupant.get());
            }
            world.moveEntity(this, nextPos);
            return true;
        }
        return false;
    }

    protected abstract Point nextPosition(WorldModel world, Point destPos);


}
