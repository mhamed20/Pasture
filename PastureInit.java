import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class PastureInit extends JFrame implements ChangeListener, ActionListener {
	int totalInitials = 13;
	ImageIcon icon = new ImageIcon("pasture.jpg");
	JLabel pastImg = new JLabel(icon);
	JLabel[] defvs = new JLabel[totalInitials];
	JPanel Initiate = new JPanel();
	JPanel pastureInit = new JPanel();
	JSlider wVal = new JSlider(JSlider.HORIZONTAL, 80, 0);
	JSlider[] values = new JSlider[totalInitials];
	JButton launch = new JButton("Launch");
	Dimension prefferedsize = new Dimension(250, 37);
	String[] vals = { "Width", "Height", "Sheeps", "Wolves", "Plants", "Fences", "Sheep move delay", "Sheep exist",
			"New sheep", "Wolf move delay", "Wolf exist", "New wolf", "New plant" };
	int[] defVals = { 35, 24, 20, 10, 40, 40, 2, 100, 101, 3, 200, 201, 10 };
	int[] initialValues = new int[totalInitials];

	int[] maxsize = { 50, 50, 100, 100, 100, 100, 20, 300, 301, 20, 600, 601, 200 };

	// add components
	public void addInitial(JPanel J, String val, int defVal, int i) {
		JPanel Wl = new JPanel();
		JPanel W = new JPanel();
		JLabel wt = new JLabel(vals[i]);
		values[i] = new JSlider(JSlider.HORIZONTAL, maxsize[i], 0);
		values[i].setValue(defVals[i]);
		values[i].setPaintLabels(true);
		values[i].setPaintTicks(true);
		defvs[i] = new JLabel(Integer.toString(defVals[i]));
		defvs[i].setText(Integer.toString(values[i].getValue()));
		Wl.setLayout(new BorderLayout(15, 15));
		W.setLayout(new BoxLayout(W, BoxLayout.Y_AXIS));
		W.setPreferredSize(prefferedsize);
		W.add(defvs[i]);
		W.add(values[i]);
		Wl.add(wt, BorderLayout.WEST);
		Wl.add(W, BorderLayout.EAST);
		J.add(Wl);
	}

	public void stateChanged(ChangeEvent e) {
		for (int i = 0; i < totalInitials; i++) {
			if (values[i].getValueIsAdjusting()) {
				values[i].repaint();
				defvs[i].setText(Integer.toString(values[i].getValue()));
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == launch) {
			for (int i = 0; i < totalInitials; i++)
				initialValues[i] = Integer.parseInt(defvs[i].getText());
		}

		new Pasture(initialValues);
	}

	int[] getInitials() {
		return initialValues;
	}

	PastureInit() {
		Container ct = this.getContentPane();
		ct.setBackground(Color.WHITE);
		Initiate.setLayout(new GridLayout(14, 0, 0, 0));

		// add components
		for (int i = 0; i < totalInitials; i++) {
			addInitial(Initiate, vals[i], defVals[i], i);
		}
		Initiate.add(launch);
		ct.add(pastImg, BorderLayout.NORTH);
		ct.add(Initiate, BorderLayout.SOUTH);

		for (int i = 0; i < totalInitials; i++)
			values[i].addChangeListener(this);
		launch.addActionListener(this);
	}
}
