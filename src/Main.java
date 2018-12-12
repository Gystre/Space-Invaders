import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {
	static boolean playAgain = false;
	
	boolean ready = true;
	public void countdown(int secs) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int i = secs;

			public void run() {
				i--;
				if (i < 0) {
					timer.cancel();
					ready = true;
					System.out.println("Ready!");
				}
			}
		}, 100, 100);
	}

	public void createObjs(ArrayList<Sprite> enemyList, ArrayList<Sprite> bulletList, int numBullets) {
		for (int i = 0; i < numBullets; i++) {
			Sprite bullet = new Sprite();
			bullet.setImage("/assets/bullet.png");
			bullet.setPosition(-100, -100);
			bulletList.add(bullet);
		}

		// first row of enemies
		double px = 10;
		double py = 10;
		for (int i = 0; i < 10; i++) {
			Sprite enemy = new Sprite();
			enemy.setImage("/assets/enemy.png");
			enemy.setPosition(px, py);
			enemyList.add(enemy);

			px += 75;
		}

		// second row of enemies
		px = 10;
		for (int i = 10; i < 20; i++) {
			Sprite enemy = new Sprite();
			enemy.setImage("/assets/enemy.png");
			enemy.setPosition(px, py + 60);
			enemyList.add(enemy);

			px += 75;
		}
	}

	public void start(Stage window) throws Exception {
		int SCREENX = 800;
		int SCREENY = 600;

		int numBullets = 30;

		BorderPane endRoot = new BorderPane();
		endRoot.setPrefSize(400, 600);
		
		Label winTitle = new Label("yaaaaaaaaaaaaaaaaaayy");
		winTitle.setPrefSize(355, 87);
		winTitle.setFont(new Font("Zapfino", 32));
		winTitle.setAlignment(Pos.CENTER);
		endRoot.setTop(winTitle);
		
		Button replay = new Button("replay");
		replay.setPrefSize(317, 145);
		endRoot.setCenter(replay);
		
		Scene end = new Scene(endRoot, SCREENX, SCREENY);

		window.setTitle("pretty good");
		BorderPane root = new BorderPane();
		Label howTo = new Label("press the arrow keys and space");
		howTo.setFont(new Font("Arial", 50));
		root.setCenter(howTo);
		
		Scene game = new Scene(root, SCREENX, SCREENY);
		Image icon = new Image(getClass().getResourceAsStream("/assets/enemy.png"));
		window.getIcons().add(icon);

		window.setScene(game);

		Canvas canvas = new Canvas(SCREENX, SCREENY);
		root.getChildren().add(canvas);
		//used to draw on canvas
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// handle keyboard events
		ArrayList<String> input = new ArrayList<String>();
		game.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				String code = e.getCode().toString();

				// only add once... prevent duplicates
				if (!input.contains(code))
					input.add(code);
			}
		});
		game.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				String code = e.getCode().toString();
				input.remove(code);
			}
		});

		// set font
		Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
		gc.setFont(theFont);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		// create sprites
		Sprite heart = new Sprite();
		heart.setImage("/assets/heart.png");
		heart.setPosition(SCREENX / 2 - 50 + 19 - 4, SCREENY - 90 + 20); // middle of screen - player sprite X + half of player - half of heart sprite X

		Sprite player = new Sprite();
		player.setImage("/assets/ship.png");
		player.setPosition(SCREENX / 2 - 50, SCREENY - 90); // from top right (half of screenx - player width, half of screeny - player height)

		ArrayList<Sprite> bulletList = new ArrayList<Sprite>();
		ArrayList<Sprite> enemyBullets = new ArrayList<Sprite>();
		ArrayList<Sprite> enemyList = new ArrayList<Sprite>();

		createObjs(enemyList, bulletList, numBullets);

		// main game loop (60 times per second)
		AnimationTimer GameLoop = new AnimationTimer() {
			long lastNanoTime = System.nanoTime();
			int bIndex = bulletList.size() - 1;
			int bLeft = numBullets;
			int enemyLeft = 20;
			int lives = 3;
			
			@Override
			public void handle(long currentNanoTime) {
				double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;
				lastNanoTime = currentNanoTime;
				
				// win / lose conditions
				if(lives == 0) {
					stop();
					window.setScene(end);
				}
				
				if (enemyLeft == 0) {
					stop();
					window.setScene(end);
				}

				// game logic
				heart.setVelocity(0, 0);
				player.setVelocity(0, 0);
				if (input.contains("LEFT")) {
					heart.addVelocity(-150, 0);
					player.addVelocity(-150, 0);
				}
				if (input.contains("RIGHT")) {
					heart.addVelocity(150, 0);
					player.addVelocity(150, 0);
				}
				if (input.contains("UP")) {
					heart.addVelocity(0, -150);
					player.addVelocity(0, -150);
				}
				if (input.contains("DOWN")) {
					heart.addVelocity(0, 150);
					player.addVelocity(0, 150);
				}

				if (input.contains("SPACE")) {
					if (bLeft > 0 && ready) {
						bulletList.get(bIndex).setPosition(player.getPosX() + 19 - 4.5, player.getPosY());
						bulletList.get(bIndex).setVelocity(0, -300);
						bIndex--;
						bLeft--;
						ready = false;
						countdown(1);
					} else {
						System.out.println("not ready!");
					}
				}

				//check if out of bounds on x-axis
				if (player.getPosX() > SCREENX) {
					player.setPosition(0, player.getPosY());
				}

				if (player.getPosX() < 0) {
					player.setPosition(SCREENX, player.getPosY());
				}

				heart.update(elapsedTime);
				player.update(elapsedTime);

				// create bullet for enemy
				for (Sprite enemy : enemyList) {
					enemy.willShoot = (int) ((Math.random() * 100) + 1);
					if (enemy.willShoot == 10) {
						Sprite eBullet = new Sprite();
						eBullet.setPosition(enemy.getPosX() + 37.5 - 4.5, enemy.getPosY());
						eBullet.setImage("/assets/enemybullet.png");
						eBullet.setVelocity(0, 300);
						enemyBullets.add(eBullet);
					}
				}

				// collision detection
				Iterator<Sprite> enemyIter = enemyList.iterator();
				while (enemyIter.hasNext()) {
					Sprite enemy = enemyIter.next();
					if (enemy.intersects(player)) {
						System.out.println("roflmao how you even died, they dont even move");
						System.exit(0);
					}

					// make sure we get none a dat index 0 error
					if (enemyLeft > 0) {
						//work from back because arraylist big dumb and adds stuff at the end
						for (int i = enemyBullets.size() - 1; i >= 0; i--) {
							if (enemyBullets.get(i).getPosY() > SCREENY) {
								enemyBullets.remove(i);
							}

							if (enemyBullets.get(i).intersects(heart)) {
								lives--;
								heart.setPosition(SCREENX / 2 - 50 + 19 - 4, SCREENY - 90 + 20);
								player.setPosition(SCREENX / 2 - 50, SCREENY - 90);
								System.out.println("ha ha you succ");
							}
						}

						for (int i = bulletList.size() - 1; i >= 0; i--) {
							if (enemy.intersects(bulletList.get(i))) {
								bulletList.remove(i);
								enemyIter.remove();
								enemyLeft--;
							}
						}
					}
				}

				// render
				gc.clearRect(0, 0, SCREENX, SCREENY);
				heart.render(gc);
				player.render(gc);

				for (Sprite bullet : bulletList) {
					bullet.update(elapsedTime);
					bullet.render(gc);
				}

				for (Sprite eBullet : enemyBullets) {
					eBullet.update(elapsedTime);
					eBullet.render(gc);
				}

				for (Sprite enemy : enemyList) {
					enemy.render(gc);
				}

				String bText = "Bullets: " + bLeft;
				String lText = "Lives: " + lives;
				gc.fillText(lText, 20, 20);
				gc.strokeText(lText, 20, 20);
				gc.fillText(bText, 360, 36);
				gc.strokeText(bText, 360, 36);
				
				if(playAgain) {
					//stop em
					player.setVelocity(0, 0);
					heart.setVelocity(0, 0);
					
					//clear em
					enemyBullets.clear();
					input.clear();
					bulletList.clear();
					enemyList.clear();
					
					//fill em
					createObjs(enemyList, bulletList, numBullets);
					
					//reset em positions and velocitys
					heart.setPosition(SCREENX / 2 - 50 + 19 - 4, SCREENY - 90 + 20);
					player.setPosition(SCREENX / 2 - 50, SCREENY - 90);
					player.setVelocity(0, 0);
					
					//reset em globals
					bIndex = bulletList.size() - 1;
					bLeft = numBullets;
					enemyLeft = 20;
					lives = 3;
					playAgain = false;
				}
			}
		};
		
		GameLoop.start();
		
		replay.setOnAction(e -> {
			window.setScene(game);
			playAgain = true;
			GameLoop.start();
		});

		window.show();
		System.out.println("done");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
