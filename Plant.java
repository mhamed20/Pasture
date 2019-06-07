import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Random;

import javax.swing.ImageIcon;

class Plant implements Entity {
	private final ImageIcon image = new ImageIcon("plant.gif");
	private Pasture pasture;
	private String type = "plant";
	private int exist;
	private int newPlantDelay;

	Plant(Pasture pasture) {
		this.pasture = pasture;
		
		// initialize
		newPlantDelay = pasture.getInitial(6);
	}

	public void tick() {
		// update
		newPlantDelay--;

		if (newPlantDelay == 0) {
			Point neighbour = getNeighbour(pasture.getFreeNeighbours(this));

			if (neighbour != null) {
				Plant plant = new Plant(pasture);
				pasture.addEntity(plant, neighbour);
				newPlantDelay = pasture.getInitial(6);
			} else
				newPlantDelay = 1;
		}
	}

	public ImageIcon getImage() {
		return image;
	}

	// get entity type
	public String getType() {
		return type;
	}

	public void setExist() {
		exist = exist;
	}

	public int getExist() {
		return exist;
	}

	public boolean isCompatible(Entity otherEntity) {
		if (otherEntity.getType() == "fence" || otherEntity.getType() == "plant")
			return false;
		return true;
	}
	
	public boolean isTarget(Entity otherEntity)
	{
		if (otherEntity.getType()=="sheep") return true;
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

	private Point getNeighbour(List<Point> l) {
		if (l.size() == 0)
			return null;

		Random r = new Random();
		int n = r.nextInt(l.size());
		return l.get(n);
	}

}