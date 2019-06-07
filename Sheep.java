import javax.swing.*;
import java.awt.*;
import java.io.Console;
import java.util.*;
import java.util.List;

class Sheep extends Animal implements Entity {

	Sheep(Pasture pasture) {
		super(pasture, "sheep", pasture.getInitial(1), pasture.getInitial(0), pasture.getInitial(2));
	}

	public boolean isCompatible(Entity otherEntity) {
		if (otherEntity.getType() == "fence" || otherEntity.getType() == "wolf")
			return false;
		return true;
	}
	
	public boolean isTarget(Entity otherEntity)
	{
		if (otherEntity.getType()=="wolf") return true;
		return false;
	}
	
}
