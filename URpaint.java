//Name: Khoa Nguyen
//NetID: knguy42
//Last date of modification: 04/04/2023

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class URpaint extends JFrame implements ChangeListener {
	
	private Canvas theCanvas;
	private JButton circleButton;
	private JButton squareButton;
	private JButton paletteButton;
	private JButton clearButton;
	private JLabel red, green, blue;
	private JSlider rSlider, gSlider, bSlider;
	private Color color = Color.BLACK;

	public URpaint() {
// creating the canvas
		super("Big Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 800);
		theCanvas = new Canvas();
		theCanvas.setFocusable(true);
		add(theCanvas);

//adding buttons and sliders
		circleButton = new JButton("Circle");
		circleButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			theCanvas.isCircle = true;
			}
		});

		squareButton = new JButton("Square");
		squareButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			theCanvas.isCircle = false;
			}
		});

		paletteButton = new JButton("Palette");
		paletteButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			BufferedImage background = new BufferedImage(1200, 800, ABORT);
			Graphics2D g2d = background.createGraphics();
			g2d.setColor(new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue()));
			g2d.fillRect(0, 0, background.getWidth(), background.getHeight());
		}
		});

		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		/* doesnt work. 
			theCanvas.remove(theCanvas);
			theCanvas = new Canvas();
			SwingUtilities.updateComponentTreeUI(theCanvas);
			*/
		}
		});

		rSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		rSlider.setMajorTickSpacing(255);
		rSlider.setPaintLabels(true);
		red = new JLabel();
		red.setText("R");
        gSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		gSlider.setMajorTickSpacing(255);
		gSlider.setPaintLabels(true);
		green = new JLabel();
		green.setText("G");
        bSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		bSlider.setMajorTickSpacing(255);
		bSlider.setPaintLabels(true);
		blue = new JLabel();
		blue.setText("B");
		
        rSlider.addChangeListener(this);
        gSlider.addChangeListener(this);
        bSlider.addChangeListener(this);

// creating control pannel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));
		JPanel subpanel1 = new JPanel();
		subpanel1.add(circleButton);
		subpanel1.add(squareButton);
		subpanel1.add(paletteButton);
		subpanel1.add(clearButton);
		JPanel subpanel2 = new JPanel();
		subpanel2.add(red);
		subpanel2.add(rSlider);
		subpanel2.add(green);
        subpanel2.add(gSlider);
		subpanel2.add(blue);
        subpanel2.add(bSlider);
		controlPanel.add(subpanel1);
		controlPanel.add(subpanel2);

		getContentPane().add(theCanvas);
		getContentPane().add(controlPanel, "North");
		setVisible(true);
}

	@Override
	public void stateChanged(ChangeEvent e) {
		theCanvas.changeColor(new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue()));
	}
	}
	
// paint panel
	class Canvas extends JPanel {
		
		private CustomMouseEvent cme;
		private CustomMouseMotionEvent cmme;
		private int radius;
		private int[] X, Y;
		private boolean[] shapes;
		boolean isCircle;
		Color[] colors;
		private int size;
		private int drag;
		Color color = Color.BLACK;
		int curActive = -1;
		Color bgColor;
		
		public Canvas() {
			
			cme = new CustomMouseEvent();
			addMouseListener(cme);
			cmme = new CustomMouseMotionEvent();
			addMouseMotionListener(cmme);
			
			setFocusable(true);
			X = new int[700];
			Y = new int[700];
			shapes = new boolean[700];
			colors = new Color[700];

			radius = 15;
			
			size = 0;

		}
		
		public void changeColor(Color c) {
			color = c;
			if (drag >= 0) {
				colors[drag] = color;
			}
			repaint();
		} 

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int i = 0; i < size; i++) {
				
				if (shapes[i]) {
					if (drag == i) {
						g.setColor(Color.YELLOW);
						g.fillOval(X[i]-radius/2, Y[i]-radius/2, radius+5, radius+5);
					}
					g.setColor(colors[i]);
					g.fillOval(X[i]-radius/2, Y[i]-radius/2, radius, radius);
				}
				else {
					if (drag == i) {
						g.setColor(Color.YELLOW);
						g.fillRect(X[i]-radius/2, Y[i]-radius/2, radius+5, radius+5);
					}
					g.setColor(colors[i]);
					g.fillRect(X[i]-radius/2, Y[i]-radius/2, radius, radius);
				}
			}
		}

		public boolean contained(int x, int y, int originX, int originY) {
			if (Math.pow(x-originX, 2)+Math.pow(y-originY, 2) <= 100) {
				return true;
			}
			return false;
		}
		
		public int checkExists(int x, int y) {
			for (int i = 0; i < size; i++) {
				if (contained(x, y, X[i], Y[i])) {
					return i;
				}
			}
			return -1;
		}		

		public class CustomMouseEvent implements MouseListener {

			public void mouseClicked(MouseEvent e) {
				System.out.println("Click detected at: (" + e.getX() + ", " + e.getY() + ")");
				if (checkExists(e.getX(), e.getY()) == -1) {
					X[size] = e.getX();
					Y[size] = e.getY();
					shapes[size] = isCircle;
					colors[size] = new Color(color.getRGB());
					size++;
					repaint();
				}
			}

			public void mousePressed(MouseEvent e) {
				drag = checkExists(e.getX(), e.getY());
			}

			public void mouseReleased(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
						
			}

			public void mouseExited(MouseEvent e) {
				
			}
		}
		
		public class CustomMouseMotionEvent implements MouseMotionListener {

			public void mouseDragged(MouseEvent e) {
				X[drag] = e.getX();
				Y[drag] = e.getY();
				repaint();
			}

			public void mouseMoved(MouseEvent e) {
				
			}
			
		}
	
	public static void main(String[] args) {
		URpaint s = new URpaint();
		s.setVisible(true);
		}
	}


