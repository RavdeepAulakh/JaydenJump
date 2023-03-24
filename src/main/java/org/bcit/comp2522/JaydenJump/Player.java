package org.bcit.comp2522.JaydenJump;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Doodle guy or the player class.
 *
 * @author Ravdeep, Aulakh
 * @version 1.0
 */
public class Player extends Sprite {


  /**
   * Check to see which way the player is facing.
   */
  private static boolean isFacingRight;

  /**
   * instance for the player.
   */
  private static Player instance = null;

  /**
   * for the image for the player.
   */
  private PImage image;

  /**
   * gravity for the jumps.
   */
  private float gravity;

  /**
   * speed for moving left and right.
   */
  private final float moveMentspeed;

  /**
   * size of the player usesd as width and height.
   * since the player is a square.
   */
  private final int imgSize;

  /**
   * Check to see if the player is shooting.
   */
  private boolean isShooting = false;

  /**
   * the projectile the player will shoot.
   */
  private Projectile projectile;

  /**
   * constructor for the player class.
   *
   * @param xpos the x position of the player
   * @param ypos the y position of the player
   * @param vx the x velocity of the player
   * @param vy the y velocity of the player
   * @param sketch the sketch for the player
   * @param image the image to set the player to
   * @param imgSize the size of the player
   * @param moveMentspeed the speed of the player
   * @param gravity the gravity on the player
   */
  private Player(float xpos, float ypos, float vx, float vy, PApplet sketch, PImage image,
                 int imgSize, float moveMentspeed, float gravity) {
    super(xpos, ypos, vx, vy, sketch);
    System.out.println(super.getSketch());
    this.image = image;
    this.imgSize = imgSize;
    this.moveMentspeed = moveMentspeed;
    this.gravity = gravity;
    projectile = new Projectile(xpos, ypos, 0, -2, 1, this);
  }

  /**
   * since this is singleton design this is to get a instance of the player.
   *
   * @param sketch the sketch
   * @param image the image for the player
   * @return instance of the player
   */
  public static Player getInstance(float xpos, float ypos, float vx, float vy, PApplet sketch,
                                   PImage image, int imgSize, float moveMentspeed, float gravity) {
    if (instance == null) {
      instance = new Player(xpos, ypos, vx, vy, sketch, image, imgSize, moveMentspeed, gravity);
      isFacingRight = true;
    }
    return instance;
  }

  /**
   * draw the player onto the screen.
   * also does some projectile stuff and
   * will flip player if facing left or right.
   */
  @Override
  public void draw() {
    if (image != null) {
      super.getSketch().pushMatrix();
      super.getSketch().translate(getXpos() + imgSize / 2, getYpos() + imgSize / 2);
      if (!isFacingRight) {
        super.getSketch().scale(-1, 1);
      }
      super.getSketch().image(image, -imgSize / 2, -imgSize / 2, imgSize, imgSize);
      super.getSketch().popMatrix();
      if (isShooting) {
        projectile.draw();
      }
    } else {
      System.err.println("Image is null. Please check the image loading process.");
    }

    if (isShooting) {
      projectile.update();
      if (projectile.getYpos() < 0) {
        stopShooting();
      }
    }


  }

  /**
   * update the players position after he jumps and moves side to side.
   */
  @Override
  public void update() {
    setVy(getVy() + gravity);

    setXpos(getXpos() + getVx());
    setYpos(getYpos() + getVy());

    setXpos(super.getSketch().constrain(getXpos(),
            imgSize / 10, super.getSketch().width - imgSize / 2));
    setYpos(super.getSketch().constrain(getYpos(),
            imgSize / 2, super.getSketch().height - imgSize / 2));

  }

  /**
   * check to see if the player is colliding with a platform.
   *
   * @param o The object to check for collision
   *
   * @return true if the player is colliding with a platform else false
   */
  @Override
  public boolean collides(Object o) {
    if (o instanceof Platform) {
      Platform platform = (Platform) o;

      float playerLeft = getXpos() - imgSize / 2;
      float playerRight = getXpos() + imgSize / 2;
      float playerTop = getYpos() - imgSize / 2;
      float playerBottom = getYpos() + imgSize / 2;

      float platformLeft = platform.getXpos() - platform.getWidth() / 2;
      float platformRight = platform.getXpos() + platform.getWidth() / 2;
      float platformTop = platform.getYpos() - platform.getHeight() / 2;
      float platformBottom = platform.getYpos() + platform.getHeight() / 2;

      boolean horizontallyOverlapping = playerLeft < platformRight && playerRight > platformLeft;
      boolean verticallyOverlapping = playerTop < platformBottom && playerBottom > platformTop;
      boolean abovePlatform = playerBottom < platformTop + platform.getHeight() / 2;

      return horizontallyOverlapping && verticallyOverlapping && abovePlatform;
    }
    return false;
  }

  /**
   * move the player to the left.
   */
  public void moveLeft() {
    setVx(-moveMentspeed);
    isFacingRight = false;
  }

  /**
   * move the player to the right.
   */
  public void moveRight() {
    setVx(moveMentspeed);
    isFacingRight = true;
  }

  public void stopMoving() {
    setVx(0);
  }


  /**
   * Method to shoot projectiles.
   */
  public void shootProjectile() {
    if (!isShooting) {
      isShooting = true;
      projectile.setXpos(getXpos());
      projectile.setYpos(getYpos());
      projectile.setVy(-10);
    }
  }

  /**
   * Method to stop shooting projectiles.
   */
  public void stopShooting() {
    isShooting = false;
  }

  /**
   * Method to reset the player.
   *
   * @param x postion
   *
   * @param y position
   *
   * @param vx velocity
   *
   * @param vy velocity
   */
  public void reset(float x, float y, float vx, float vy) {
    setXpos(x);
    setYpos(y);
    setVx(vx);
    setVy(vy);
  }

  /**
   * Method to get the gravity.
   *
   * @return gravity
   */
  public float getGravity() {
    return gravity;
  }

  /**
   * Method to set the gravity.
   *
   * @param gravity used to set the gravity
   */
  public void setGravity(float gravity) {
    this.gravity = gravity;
  }

  /**
   * Method to get the moveMentspeed.
   *
   * @return moveMentspeed
   */
  public float getMoveMentspeed() {
    return moveMentspeed;
  }

  /**
   * Method to get the image size.
   *
   * @return image size
   */
  public int getImgSize() {
    return imgSize;
  }

  /**
   * method to check if the player is shooting.
   *
   * @return true if the player is shooting else false
   */
  public boolean isShooting() {
    return isShooting;
  }

  /**
   * Method to set if the player is shooting.
   *
   * @param shooting used to set if the player is shooting
   */
  public void setShooting(boolean shooting) {
    isShooting = shooting;
  }

  /**
   * Method to get the projectile.
   *
   * @return projectile the projectile
   */
  public Projectile getProjectile() {
    return projectile;
  }

  /**
   * Method to set the projectile.
   *
   * @param projectile used to set the projectile
   *
   */
  public void setProjectile(Projectile projectile) {
    this.projectile = projectile;
  }

  public void setImage(PImage image) {
    this.image = image;
  }

}