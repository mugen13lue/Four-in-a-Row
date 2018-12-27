public class Animation implements Action
{
    //Instance Vars
    private Animated animated;
    private int repeatCount;

    //Constructor
    public Animation( Animated animated, int repeatCount)
    {
        this.animated = animated;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        animated.nextImage();

        if (repeatCount != 1)
        {
            scheduler.scheduleEvent( animated, new Animation(animated,Math.
                            max(repeatCount - 1, 0)), animated.getAnimationPeriod());
        }
    }
}