import java.util.*;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * A pasture contains sheep, wolves, fences, plants, and possibly other
 * entities. These entities move around in the pasture and try to find food,
 * other entities of the same kind and run away from possible enimies.
 */
public class Pasture {

	private int width;
	private int height;

	private int sheeps;
	private int wolves;
	private int fences;
	private int plants;

	private final Set<Entity> world = new HashSet<Entity>();
	private final Map<Point, List<Entity>> grid = new HashMap<Point, List<Entity>>();
	private final Map<Entity, Point> point = new HashMap<Entity, Point>();
	private final Collection<Entity> removed = new ArrayList<Entity>();

	private final PastureGUI gui;
	private int[] init = new int[7];

	/**
	 * Create a new instance of this class and places the entities in it on random
	 * positions.
	 */
	public Pasture(int[] initials) {
		// set the initial values
		setInitials(initials);

		// set initial values
		for (int j = 0; j < 7; j++)
			init[j] = initials[j + 6];

		Engine engine = new Engine(this);
		gui = new PastureGUI(width, height, engine);

		/*
		 * The pasture is surrounded by a fence. Replace Dummy for Fence when you have
		 * created that class
		 */
		for (int i = 0; i < width; i++) {
			addEntity(new Fence(this), new Point(i, 0));
			addEntity(new Fence(this), new Point(i, height - 1));
		}
		for (int i = 1; i < height - 1; i++) {
			addEntity(new Fence(this), new Point(0, i));
			addEntity(new Fence(this), new Point(width - 1, i));
		}

		/*
		 * Now insert the right number of different entities in the pasture.
		 */

		for (int i = 0; i < sheeps; i++) {
			Sheep sheep = new Sheep(this);
			addEntity(sheep, sheep.getFreePosition(width, height));
		}

		for (int i = 0; i < wolves; i++) {
			Wolf wolf = new Wolf(this);
			addEntity(wolf, wolf.getFreePosition(width, height));
		}

		for (int i = 0; i < fences; i++) {
			Fence fence = new Fence(this);
			addEntity(fence, fence.getFreePosition(width, height));
		}

		for (int i = 0; i < plants; i++) {
			Plant plant = new Plant(this);
			addEntity(plant, plant.getFreePosition(width, height));
		}

		gui.update();
	}

	// set initial values
	void setInitials(int[] initials) {
		width = initials[0];
		height = initials[1];
		sheeps = initials[2];
		wolves = initials[3];
		plants = initials[4];
		fences = initials[5];
	}

	// get an initial value
	int getInitial(int in) {
		return init[in];
	}

	public void refresh() {
		gui.update();
	}

	/**
	 * Add a new entity to the pasture.
	 */
	public void addEntity(Entity entity, Point pos) {

		world.add(entity);

		List<Entity> l = grid.get(pos);
		if (l == null) {
			l = new ArrayList<Entity>();
			grid.put(pos, l);
		}
		l.add(entity);

		point.put(entity, pos);

		gui.addEntity(entity, pos);
	}

	public void moveEntity(Entity e, Point newPos) {

		Point oldPos = point.get(e);

		List<Entity> l = grid.get(oldPos);
		if (!l.remove(e))
			throw new IllegalStateException("Inconsistent stat in Pasture");
		/*
		 * We expect the entity to be at its old position, before we move, right?
		 */

		l = grid.get(newPos);
		if (l == null) {
			l = new ArrayList<Entity>();
			grid.put(newPos, l);
		}

		l.add(e);

		point.put(e, newPos);

		gui.moveEntity(e, oldPos, newPos);

	}

	/**
	 * Remove the specified entity from this pasture.
	 */
	public void removeEntity(Entity entity) {

		Point p = point.get(entity);
		world.remove(entity);
		grid.get(p).remove(entity);
		point.remove(entity);
		gui.removeEntity(entity, p);

	}

	/**
	 * Various methods for getting information about the pasture
	 */
	
	 public List<Entity> getEntities() {
	        return new ArrayList<Entity>(world);
	    }

	public Collection<Entity> getEntitiesAt(Point lookAt) {

		Collection<Entity> l = grid.get(lookAt);

		if (l == null) {
			return null;
		} else {
			return new ArrayList<Entity>(l);
		}
	}

	public List<Point> getFreeNeighbours(Entity entity) {
		List<Point> free = new ArrayList<Point>();

		int entityX = getEntityPosition(entity).x;
		int entityY = getEntityPosition(entity).y;

		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {

				Point p = new Point(entityX + x, entityY + y);
				if (freeSpace(p, entity)) {
					free.add(p);
				}
			}
		}

		return free;
	}

	private boolean freeSpace(Point p, Entity e) {

		List<Entity> l = grid.get(p);
		if (l == null)
			return true;
		for (Entity old : l) {
			if (!old.isCompatible(e))
				return false;
		}
		return true;
	}

	public Point getEntityPosition(Entity entity) {
		return point.get(entity);
	}

	public Collection<Entity> getRemovedEntities() {
		return removed;
	}

	public void checkExist(Entity currEntity) {
		if (currEntity.getExist() == 1) {
			removed.add(currEntity);
			removeEntity(currEntity);
		}
	}

	// check the status in the position after move
	public void checkStatus(Entity currEntity) {
		Collection<Entity> entitiesAtPosition = grid.get(getEntityPosition(currEntity));

		if (entitiesAtPosition != null) {
			for (Entity en : entitiesAtPosition) {
				if (en.isTarget(currEntity)) {
					currEntity.setExist();
					removed.add(en);
					removeEntity(en);
					break;
				}
			}
		}
	}

	Point checkNextMove(Entity currEntity) {
		Point Pos = getEntityPosition(currEntity);
		Map<Point, Collection<Entity>> visionField = new HashMap<Point, Collection<Entity>>();
		int[] dir = { -1, 0, 1 };

		// define vision field for the entity

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boolean isNeighbour = true;
				if (i == 0 && j == 0)
					isNeighbour = false;
				if (isNeighbour) {
					Point p = new Point();
					p.x = Pos.x + dir[i];
					p.y = Pos.y + dir[j];

					visionField.put(p, getEntitiesAt(p));
				}
			}
		}

		// check all neighboring points and get next move if possible

		for (Point pt : visionField.keySet()) {
			if (visionField.get(pt) != null) {
				for (Entity en : visionField.get(pt)) {
					if (currEntity.isTarget(en)) {
						return currEntity.getOppositePos(en);
					}

					if (en.isTarget(currEntity)) {
						return getEntityPosition(en);
					}
				}
			}
		}

		return null;
	}

	/** The method for the JVM to run. */
	public static void main(String[] args) {
		PastureInit init = new PastureInit();
		init.setBounds(200, 200, 200, 200);
		init.setVisible(true);
		init.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		init.setLocation(screenSize.width / 2 - init.getWidth() / 2, screenSize.height / 2 - init.getHeight() / 2);
	}

}
