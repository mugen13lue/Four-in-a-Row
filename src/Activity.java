public class Activity implements Action
{
    //Instance Vars
    private ActiveEntity activeEntity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    //constructor
    public Activity(ActiveEntity executeAction, WorldModel world,
                    ImageStore imageStore)
    {
        this.activeEntity = executeAction;
        this.world = world;
        this.imageStore = imageStore;
        repeatCount = 0;
    }
    public void executeAction(EventScheduler scheduler) {
        activeEntity.executeActivity(world, imageStore, scheduler);
    }
}