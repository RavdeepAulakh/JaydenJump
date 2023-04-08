package org.bcit.comp2522.JaydenJump.sprites;

import org.bcit.comp2522.JaydenJump.interfaces.Audible;
import org.bcit.comp2522.JaydenJump.gameUI.MenuManager;
import processing.core.PImage;
import javax.sound.sampled.Clip;

/**
 * The Tire class is a type of PowerUp that the Player can interact
 * with to be able momentarily to jump higher.
 *
 * @version 1.0
 *
 * @author Shawn Birring
 *
 * @since 2023-03-22
 */

public class Tire extends PowerUp implements Audible {

  /** The amount that affects the y velocity of the Player. */
  private int boostHeight;

  /** Audio clip used for playing Tire PowerUp. */
  private Clip clip;

  /**
   * Creates an instance of the Tire class in the game.
   *
   * @param xpos The x position of the Tire
   *
   * @param ypos The y position of the Tire
   *
   * @param vx The x velocity of the Tire
   *
   * @param vy The y Velocity of the Tire
   *
   * @param isActive The boolean state that determines if the Tire is active or not
   */
  public Tire(float xpos,
              float ypos,
              float vx,
              float vy,
              int boostHeight,
              boolean isActive,
              PImage image) {
    super(xpos, ypos, vx, vy, isActive, image);
    this.boostHeight = boostHeight;
    this.clip = loadSound("tire.wav");
  }

  /**
   * Boosts the height of the player.
   */
  private void boostPlayer() {
    if (isActive()) {
      getPlayer().setVy(getPlayer().getVy() - getBoostHeight());
    }
  }

  /**
   * Activates the Tire.
   */
  @Override
  public void activate() {
    playSound();
    setActive(true);
    boostPlayer();
    super.getSketch().text("TIRE BOOST", getSketch().CENTER, getSketch().CENTER);
  }


  /**
   * Deactivates the Tire.
   */
  @Override
  public void deactivate() {
    setActive(false);
  }

  /**
   * Returns the boost height of the Tire.
   *
   * @return boostHeight The boost height of the Tire
   */
  public int getBoostHeight() {
    return boostHeight;
  }

  /**
   * Sets the boost height of the Tire.
   *
   * @param boostHeight The boost height of the Tire
   */
  public void setBoostHeight(int boostHeight) {
    this.boostHeight = boostHeight;
  }

  /** Plays sound clip of Tire if MenuManager's sound is on */
  @Override
  public void playSound() {
    if (MenuManager.sound) {
      clip.setFramePosition(0);
      clip.start();
    }
  }
}