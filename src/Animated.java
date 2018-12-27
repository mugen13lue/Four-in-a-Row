import processing.core.PImage;
import java.util.List;

public abstract class Animated extends ActiveEntity
{
    //instance vars
    private int animationPeriod;
    private int animationRepeatCount;

    /** I implemented @param animationRepeatCount here because animationRepeatCount is a parameter to Animation in
     * scheduleActions().
     *
     * @param animationRepeatCount
     */

    //constructor
    public Animated(Point position, List<PImage> images, int actionPeriod,
                    int animationPeriod, int animationRepeatCount)
    {
        super(position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
        this.animationRepeatCount = animationRepeatCount;
    }
    //overrides scheduleAction from ExecuteActivity.
    protected void scheduleAction(EventScheduler scheduler,
                                  WorldModel world, ImageStore imageStore)
    {
        super.scheduleAction(scheduler, world, imageStore);
        scheduler.scheduleEvent(this, new Animation(this,
                animationRepeatCount), animationPeriod);
        /** repeatCount is a parameter to Animation() so instead of hardcoding 0, all entities that are animated will
         * have an animationPeriod and an animationRepeatCount. Allows for flexibility if we want to change
         * animationRepeatCount in future projects.
         */
    }

    protected void nextImage() {setImageIndex((getImageIndex() + 1) % getImages().size());}
    protected int getAnimationPeriod() {return animationPeriod;}
    protected int getAnimationRepeatCount() {return animationRepeatCount;}
    /** for functionality if we ever need to use animationrepeatCount outside of this class.*/
}