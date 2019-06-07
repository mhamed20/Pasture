import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Random;

import javax.swing.ImageIcon;

abstract public class Animal implements Entity {
	private ImageIcon image;
	protected Pasture pasture;
	protected String type;
	protected final int mDInit;
	protected final int nADInit;
	protected final int existInit;
	protected int moveDelay;
	protected int newAnimalDelay;
	protected int exist;
	protected int ents;
	protected int dirx, diry;

	public Animal(Pasture pasture, String typ, int exs, int moveD, int nAD) {
		this.pasture = pasture;
		image = new ImageIcon(typ + ".gif");
		type = typ;
		existInit = exs;
		mDInit = moveD;
		nADInit = nAD;

		// Initialize
		initialize("exist");
		initialize("move delay");
		initialize("new animal delay");
	}

	// initialize
	private void initialize(String init) {
		if (init == "exist")
			exist = existInit;
		if (init == "move delay")
			moveDelay = mDInit;
		if (init == "new animal delay")
			newAnimalDelay = nADInit;
	}

	public void tick() {

		// update
		exist--;
		moveDelay--;
		newAnimalDelay--;

		action();
	}

	// check action
	public void action() {

		// get next position and move entity if possible
		if (moveDelay == 0) {
			Point nextPos = getNextMove();
			if (nextPos != null) {
				pasture.moveEntity(this, nextPos);
			}
			initialize("move delay");
		}

		if (newAnimalDelay == 0 && ents > 0) {
			Point neighbour = getNeighbour(pasture.getFreeNeighbours(this));

			if (neighbour != null) {		
				pasture.addEntity(getNewAnimal(), neighbour);
				initialize("new animal delay");
			}
		}
	}
	
	public Animal getNewAnimal()
	{
		if (type == "sheep") return new Sheep(pasture);
		else return new Wolf(pasture);		
	}

	// check next move
	private Point getNextMove() {
		Point neighbour = new Point();

		// check if there is a possible move in the neighborhood of the entity
		neighbour = pasture.checkNextMove(this);
		if (neighbour != null) {
			// change direction if the move is possible
			setDirection(pasture.getEntityPosition(this), neighbour);
			return neighbour;
		}

		// continue moving in the same direction
		neighbour = contMove();
		if (neighbour != null)
			return neighbour;

		// get a random move
		List<Point> freeNeighbours = pasture.getFreeNeighbours(this);
		neighbour = getNeighbour(freeNeighbours);
		if (neighbour != null) {
			// change direction if the move is possible
			setDirection(pasture.getEntityPosition(this), neighbour);
			return neighbour;
		}

		return null;
	}

	// get next position in the same direction
	private Point contMove() {
		Point nextPosition = new Point();

		if (dirx != 0 || diry != 0) {
			Point currPosition = pasture.getEntityPosition(this);
			nextPosition.x = currPosition.x + dirx;
			nextPosition.y = currPosition.y + diry;

			List<Point> avail = pasture.getFreeNeighbours(this);

			if (avail.contains(nextPosition))
				return nextPosition;
		}
		return null;
	}

	// change direction
	private void setDirection(Point previous, Point next) {
		dirx = next.x - previous.x;
		diry = next.y - previous.y;
	}

	// get the type of the entity
	public String getType() {
		return type;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setExist() {
		int v;
		if (type=="sheep") v = 4; 
		else v = 20;
		ents++;
		exist = exist + v;
	}

	public int getExist() {
		return exist;
	}

	abstract public boolean isCompatible(Entity otherEntity);
	
	abstract public boolean isTarget(Entity otherEntity);	
	
	// get the opposite position of the other entity's position which is in the
    // neighborhood of the current entity
	public Point getOppositePos(Entity otherEntity) 
	{
	Point entityPos = pasture.getEntityPosition(this);
	Point otherEnPos = pasture.getEntityPosition(otherEntity);
	Point OppPos = new Point();
	
	int diffx = entityPos.x - otherEnPos.x;
	int diffy = entityPos.y - otherEnPos.y;
	
	OppPos.x = entityPos.x + diffx;
	OppPos.y = entityPos.y + diffy;

	Collection<Entity> m = pasture.getEntitiesAt(OppPos);

	if (m != null) {
		for (Entity en : m) {
			if (!this.isCompatible(en))
				return null;
		}
	} else
		return null;

	return OppPos;
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
