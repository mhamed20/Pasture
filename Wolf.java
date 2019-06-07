import javax.swing.*;
import java.awt.*;
import java.io.Console;
import java.util.*;
import java.util.List;

class Wolf extends Animal implements Entity {
	
	Wolf(Pasture pasture) {
		super(pasture, "wolf", pasture.getInitial(4), pasture.getInitial(3), pasture.getInitial(5));
	}

	public boolean isCompatible(Entity otherEntity) {
		if (otherEntity.getType() == "fence" || otherEntity.getType() == "wolf")
			return false;
		return true;
	}
	
	public boolean isTarget(Entity otherEntity)
	{
		return false;
	}
}