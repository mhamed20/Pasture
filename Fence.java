import java.awt.Point;
import java.util.Collection;
import java.util.MissingResourceException;

import javax.swing.*;

class Fence implements Entity {
	private final ImageIcon image = new ImageIcon("fence.gif");
	private Pasture pasture;
	private String type = "fence";
	private int exist;

	Fence(Pasture pasture) {
		this.pasture = pasture;
	}
	
	public void tick() {
	}

	public ImageIcon getImage() {
		return image;
	}

	// get the type of the entity
	public String getType() {
		return type;
	}

	public void setExist() {
	}

	public int getExist() {
		return exist;
	}

	public boolean isCompatible(Entity otherEntity) {
		return false;
	}
	
	public boolean isTarget(Entity otherEntity) {
		return false;
	}
	
	public Point getOppositePos(Entity otherEntity) 
	{
		return null;
	}

	public Point getFreePosition(int width, int height) throws MissingResourceException {
		Point position = new Point((int) (Math.random() * width), (int) (Math.random() * height));

		int p = position.x + position.y * width;
		int m = height * width;
		int q = 97; // any large prime will do

		for (int i = 0; i < m; i++) {
			int j = (p + i * q) % m;
			int x = j % width;
			int y = j / width;

			position = new Point(x, y);
			boolean free = true;

			Collection<Entity> c = pasture.getEntitiesAt(position);
			if (c != null) {
				for (Entity thisThing : c) {
					if (!isCompatible(thisThing)) {
						free = false;
						break;
					}
				}
			}
			if (free) {
				return position;
			}
		}
		throw new MissingResourceException("There is no free space" + " left in the pasture", "Pasture", "");
	}

}
