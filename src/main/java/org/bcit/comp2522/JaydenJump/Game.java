package org.bcit.comp2522.JaydenJump;

import java.util.Iterator;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Game class.
 *
 * @author Shawn, Birring; Brian Kwon
 * @version 1.3
 */
public class Game extends PApplet {

  /**
   * Instance for the player.
   */
  private static Player player;

  /**
   * Flag for indicating if game is over.
   */
  static boolean gameOver;

  /**
   * Platform manager.
   */
  private static PlatformManager platformManager;

  /**
   * PowerUp Manager.
   */
  private static PowerUpManager powerUpManager;

  /**
   * Coin Manager.
   */
  private static CoinManager coinManager;

  /**
   * Window for displaying game.
   */
  private static MenuManager window;

  /**
   * Current score.
   */
  private static int score = 0;

  /**
   * Highest score achieved in the game so far.
   */
  private static int highscore = 0;

  /**
   * The lives of the player.
   */
  private static int lives = 3;

  /**
   * Manager for the enemies.
   */
  private EnemyManager enemyManager;

  /**
   * the boss manager.
   */
  private static BossManager bossManager;

  /**
   * the background image.
   */
  private final PImage backgroundImage;

  /**
   * the position of the background image.
   */
  private final PVector backgroundPos;

  /**
   * the speed of the background.
   */
  private static int scrollSpeed;

  /**
   * Constants.
   */
  private static int BOSS = 2000;
  private static int FONT_SIZE = 20;
  private static int X_OFFSET = 75;
  private static int Y_OFFSET = 50;
  private static int HEART1 = 400;
  private static int HEART2 = 337;
  private static int HEART3= 275;
  private static int PLAYER_LIVES = 3;

  /**********************************************************/

  /**
   * the level of the game.
   *
   * @param diff the level of the game
   * @param sketch the window for the game
   * @param powerUpImage the image for the power up
   * @param backgroundImage the image for the background
   * @param enemyImage the image for the enemy
   * @param playerImage the image for the player
   * @param coinImages the images for the coins
   */
  public Game(int diff, MenuManager sketch,
              PImage[] powerUpImage, PImage backgroundImage, PImage enemyImage,
              PImage[] playerImage, PImage[] coinImages) {
    window = sketch;
    this.backgroundImage = backgroundImage;
    this.backgroundPos = new PVector(0, 0);
    Level level = new Level(diff);
    initializeLevel(level, coinImages, powerUpImage, enemyImage, playerImage);
    platformManager.generateStartPlatforms();
    powerUpManager.generateStartPowerUps();
    coinManager.generateStartCoins();
    gameOver = false;
  }

  public static Player getPlayer() {
    return player;
  }

  private void initializeLevel(Level level, PImage[] coinImages, PImage[] powerUpImage,
                               PImage enemyImage, PImage[] playerImage) {
    player = Player.getInstance(window, playerImage, level.getPlayerSpeed(), level.getGravity());
    scrollSpeed = level.getScrollSpeed();
    platformManager = PlatformManager.getInstance(level.getMaxPlatform(), window,
            level.getPlatformSpeed(), level.getMoveableSpeed(), level.getJumpThroughHeight(),
            level.getPlayerJumpHeight(), player);
    powerUpManager = PowerUpManager.getInstance(level.getMaxPowerUps(), window,
            level.getPowerUpSpeed(), player, powerUpImage);
    coinManager = CoinManager.getInstance(level.getMaxCoins(), window, level.getCoinSpeed(),
            player, coinImages);
    this.enemyManager = EnemyManager.getInstance(window, level, enemyImage);
    this.bossManager = BossManager.getInstance(MenuManager.getBossImg(), 150, 150, window,
            player, level);
  }


  /**
   * Draws to window.
   * called every frame.
   */
  public void draw() {
    drawBackground();
    displayScore();
    drawPlayerLives(lives);

    if (!gameOver && lives > 0) {
      checkCollisions();
      updateScoreAndHighscore();

      if (playerReachedGround()) {
        handlePlayerLanding();
      }

      updateAndDrawGameElements();
      generateGameElements();

      if (score >= BOSS) {
        drawAndUpdateBoss();
      }
    }
  }

  /**
   * draws the score on the screen.
   */
  private void displayScore() {
    window.textSize(FONT_SIZE);
    window.fill(0);
    window.text("Score: " + Game.getScore(), X_OFFSET, Y_OFFSET);
  }

  /**
   * checks for any collisions and handles them.
   */
  private void checkCollisions() {
    platformManager.checkCollision();
    powerUpManager.checkCollision();
    coinManager.checkCollision();
    enemyManager.checkCollision(player);
  }

  /**
   * updates the score of the game.
   */
  private void updateScoreAndHighscore() {
    score++;
    if (score > highscore) {
      highscore = score;
    }
  }

  /**
   * checks if the player has reached the ground.
   *
   * @return true if the player has reached the ground
   */
  private boolean playerReachedGround() {
    return player.getYpos() >= window.height - player.getPlayerSize() / 2f;
  }

  /**
   * checks if the player has landed on the ground.
   */
  private void handlePlayerLanding() {
    lives--;
    restartGame();
    if (lives == 0) {
      bossManager.setIsAlive(false);
      bossManager.setBossHealth(3);
      endGame();
    }
  }


  /**
   * updates and draws the game elements.
   */
  private void updateAndDrawGameElements() {
    platformManager.updateAndDrawPlatforms();
    powerUpManager.updateAndDrawPowerUps();
    coinManager.updateAndDrawCoins();
    player.update();
    player.draw();
    enemyManager.update();
    enemyManager.draw();
  }

  /**
   * generates the game elements.
   */
  private void generateGameElements() {
    platformManager.generatePlatforms();
    powerUpManager.generatePowerUps();
    coinManager.generateCoins();
  }

  /**
   * draws and updates the boss.
   */
  private void drawAndUpdateBoss() {
    bossManager.draw();
    bossManager.update();
  }

  /**
   * making the background scroll.
   */
  public void drawBackground() {
    backgroundPos.y += scrollSpeed;
    backgroundPos.x = 0;

    int offsetX = (int) (backgroundPos.x % backgroundImage.width - backgroundImage.width);
    int offsetY = (int) (backgroundPos.y % backgroundImage.height - backgroundImage.height);

    for (int x = offsetX; x < window.width; x += backgroundImage.width) {
      for (int y = offsetY; y < window.height; y += backgroundImage.height) {
        window.image(backgroundImage, x, y);
      }
    }
  }

  /**
   * Draws appropriate number of hearts based on number of lives remaining.
   *
   * @param lives as an int
   */
  public void drawPlayerLives(int lives) {
    int[] heartPositions = {HEART1, HEART2, HEART3};
    for (int i = 0; i < lives; i++) {
      drawHeart(heartPositions[i]);
    }
  }

  /**
   * Draws a heart shape to window.
   *
   * @param x as an int
   */
  public void drawHeart(int x) {
    int heartColorRed = 255;
    float controlPoint1X = width / 4f + x;
    float controlPoint2X = 3 * width / 4f + x;
    float startPointX = width / 2f + x;
    float startPointY = height / 4f;
    float endPointX = startPointX;
    float endPointY = height / 2f;
    float controlPointY = 0;
    float anchorPointY = height / 3f;

    window.fill(heartColorRed, 0, 0);
    window.beginShape();
    window.vertex(startPointX, startPointY);
    window.bezierVertex(controlPoint1X, controlPointY, x, anchorPointY, endPointX, endPointY);
    window.bezierVertex(width + x, anchorPointY, controlPoint2X, controlPointY, startPointX, startPointY);
    window.endShape();
    }


  /**
   * Restarts the game when the player goes below
   * the screen or makes contact with an enemy.
   */
  public static void restartGame() {
    player.reset();
    platformManager.getPlatforms().clear();
    powerUpManager.getPowerups().clear();
    coinManager.getCoins().clear();
    platformManager.generateStartPlatforms();
    powerUpManager.generateStartPowerUps();
    coinManager.generateStartCoins();
    gameOver = false;
  }

  /**
   * Ends the game when the player runs out of lives.
   */
  public static void endGame() {
    gameOver = true;
    lives = PLAYER_LIVES;
  }

  /**
   * Starts the game.
   */
  public static void startGame() {
    score = 0;
  }

  public static void resetHighscore() {
    highscore = 0;
  }

  /**
   * Event listener for key presses.
   */
  public static void keyPressedListener(int key) {
    if (key == LEFT || key == 'A') {
      player.moveLeft();
    } else if (key == RIGHT || key == 'D') {
      player.moveRight();
    } else if (key == 'P') {
      if (MenuManager.getCurrentScreen() == 7) {
        MenuManager.setCurrentScreen(5);
      } else if (MenuManager.getCurrentScreen() == 5) {
        MenuManager.setCurrentScreen(7);
      }
    } else if (key == ' ') {
      player.shootProjectile();
    }
  }

  /**
   * Event listener for key releases.
   */
  public static void keyReleasedListener(int key) {
    if (key == LEFT || key == 'A') {
      player.setVx(player.getVx() - 2);
    } else if (key == RIGHT || key == 'D') {
      player.setVx(player.getVx() + 2);
    } else if (key == ' ') {
      player.shootProjectile();
    }
  }

  /**
   * Getter for score.
   *
   * @return score as an int
   */
  public static int getScore() {
    return score;
  }

  /**
   * Setter for score.
   *
   * @return score as an int
   */
  public static int increaseScore(int increase) {
    return score = score + increase;
  }

  /**
   * Getter for highscore.
   *
   * @return highscore as an int
   */
  public static int getHighscore() {
    return highscore;
  }

  /**
   * Retrieves the player's current lives in game.
   *
   * @return player's life count while in game.
   */
  public static int getLives() {
    return lives;
  }

  /**
   * Changes Player's lives to a specific amount.
   *
   * @param lives increased
   */
  public static void setLives(int lives) {
    Game.lives = lives;
  }

  /**
   * getter for the boss manager.
   *
   * @return the boss manager
   */
  public static BossManager getBossManager() {
    return bossManager;
  }
}

