import processing.core.PImage;

import java.util.List;

public class Player1 extends GamePiece
{
    public Player1(Point position, List<PImage> images, int actionPeriod,
            int animationPeriod, int animationRepeatCount, boolean won, boolean winnersDisplayed)
    { super(position, images, actionPeriod,  animationPeriod, animationRepeatCount, won, winnersDisplayed);}
}