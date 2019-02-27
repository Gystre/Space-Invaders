package game;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.joml.Vector2d;
import org.joml.Vector2f;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite
{
	private Image image;
    private Vector2d position;   
    private Vector2d velocity;
    private float width;
    private float height;
    private int willShoot;

    public Sprite(String filename)
    {
        position = new Vector2d(0, 0);   
        velocity = new Vector2d(0, 0);
        
        try {
			Image i = new Image(new FileInputStream(filename));
			width = (float) i.getWidth();
			height = (float) i.getHeight();
			this.image = i;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }

    public void setPosition(double x, double y)
    {
        position.x = x;
        position.y = y;
    }
    
    public double getPosX() 
    {
    	return position.x;
    }
    
    public double getPosY()
    {
    	return position.y;
    }

    public void setVelocity(float x, float y)
    {
        velocity.x = x;
        velocity.y = y;
    }

    public void addVelocity(float x, float y)
    {
        velocity.x += x;
        velocity.y += y;
    }

    //multiply by time elapsed so sprite doesn't move faster than framerate
    public void update(double time)
    {
        position.x += velocity.x * time;
        position.y += velocity.y * time;
    }

    public void render(GraphicsContext gc)
    {
        gc.drawImage( image, position.x, position.y );
    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(position.x, position.y, width, height);
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects( this.getBoundary() );
    }

	public int getWillShoot() {
		return willShoot;
	}

	public void setWillShoot(int willShoot) {
		this.willShoot = willShoot;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}