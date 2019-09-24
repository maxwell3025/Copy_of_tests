package tests;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class tests extends JFrame implements MouseMotionListener, KeyListener {
	BufferedImage screenbuffer = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
	Graphics2D graphics = screenbuffer.createGraphics();
	BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
				Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				    cursorImg, new Point(0, 0), "blank cursor");
	double fovinv = -2;
	double fov = 0.5;
	double a = 5.2;
	double myx = Math.random() * 10;
	double myy = Math.random() * 10;
	boolean movingmouse = false;
	int[][] mapy = { { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 255, 255, 255, 0, 0 },
			{ 0, 0, 0, 0, 0, 255, 0, 255, 0, 0 }, { 0, 0, 0, 0, 0, 255, 0, 255, 0, 1 },
			{ 0, 0, 0, 0, 0, 255, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

	public tests() {
		setSize(1000, 1000);
		setVisible(true);
		addMouseMotionListener(this);
		addKeyListener(this);
		while (mapy[(int) Math.floor(myx)][(int) Math.floor(myy)] != 0) {
			myx = Math.random() * 10;
			myy = Math.random() * 10;
		}

		this.getContentPane().setCursor(blankCursor);
	}

	public void paint(Graphics g) {
		g.drawImage(screenbuffer, 0, 0, getWidth(), getHeight(), null);
	}

	public double findforx(double x, double y) {
		try {
			return Math.abs(1 / x);
		} catch (ArithmeticException e) {
			return 0;
		}

	}

	public double findfory(double x, double y) {
		try {
			return Math.abs(1 / y);
		} catch (ArithmeticException e) {
			return 0;
		}
	}

	public double findforxinit(double x, double y, double playerx, double playery) {
		double m = 0;
		try {
			m = Math.abs(1 / x);
		} finally {
			m *= difference(x, playerx);
		}
		return m;

	}

	public double findforyinit(double x, double y, double playerx, double playery) {
		double m = 0;
		try {
			m = Math.abs(1 / y);
		} finally {
			m *= difference(y, playery);
		}
		return m;
	}

	public double difference(double x, double playerx) {
		if (x < 0) {
			return playerx - Math.floor(playerx);
		} else {
			return Math.ceil(playerx) - playerx;
		}
	}

	public static int[] atblock(double x, double y, int[][] map) {
		int[] bleh = new int[2];
		try {
			int wallx = (int) Math.round(x);
			int wally = (int) Math.round(y);
			if (Math.abs(wallx - x) < Math.abs(wally - y)) {
				bleh[1] = 1;
				int left = map[wallx][(int) Math.floor(y)];
				int right = map[wallx - 1][(int) Math.floor(y)];
				if (left == 0 && right == 0) {
					bleh[0] = 0;
				} else if (((!(left == 0)) && (!(right == 0)))) {
					bleh[0] = 0;
				} else {
					bleh[0] = Math.max(left, right);
				}
			} else {
				bleh[1] = 0;
				int left = map[(int) Math.floor(x)][wally];
				int right = map[(int) Math.floor(x)][wally - 1];
				if (left == 0 && right == 0) {
					bleh[0] = 0;
				} else if (((!(left == 0)) && (!(right == 0)))) {
					bleh[0] = 0;
				} else {
					bleh[0] = Math.max(left, right);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			bleh[0] = 6;
		}
		return bleh;
	}

	public void update() {
		graphics.clearRect(0, 0, 1000, 1000);
		int screenseg = 0;
		int blocktype = 0;
		for (double b = -1; b < 1; b += 0.002) {
			screenseg++;
			double x = Math.cos(a) + Math.sin(-a) * b * fov;
			double y = Math.sin(a) + Math.cos(-a) * b * fov;
			double stepx = findforx(x, y);
			double stepy = findfory(x, y);
			double mxn = findforxinit(x, y, myx, myy);
			double myn = findforyinit(x, y, myx, myy);
			double mx = 0;
			double my = 0;
			double m = Math.max(mx, my);
			double realx = x * m + myx;
			double realy = y * m + myy;
			graphics.setColor(Color.RED);
			boolean isx;
			int texlev;
			if (my + myn < mx + mxn) {
				my += myn;
				myn = stepy;
			} else {
				mx += mxn;
				mxn = stepx;
			}
			m = Math.max(mx, my);
			realy = y * m + myy;
			realx = x * m + myx;
			for (int c = 0; c < 20; c++) {
				isx = atblock(realx, realy, mapy)[1] == 1;
				if (atblock(realx, realy, mapy)[0] == 0) {
					if (my + myn < mx + mxn) {
						my += myn;
						myn = stepy;
					} else {
						mx += mxn;
						mxn = stepx;
					}
					m = Math.max(mx, my);
					realy = y * m + myy;
					realx = x * m + myx;
				} else {
					blocktype = atblock(realx, realy, mapy)[0];
					if (!isx) {
						texlev = (int) ((realx - Math.floor(realx)) * 255);
					} else {
						texlev = (int) ((realy - Math.floor(realy)) * 255);
					}
					try {
						if (!isx) {
							graphics.setColor(new Color(blocktype, texlev, texlev));
						} else {
							graphics.setColor(new Color(texlev, blocktype, texlev));
						}
					} catch (IllegalArgumentException e) {

					}

					graphics.drawRect(screenseg, 500 - (int) (500 / m), 0, (int) (1000 / m));
					break;
				}
			}

			graphics.setColor(Color.GREEN);
			graphics.fillRect((int) (100 * myx), (int) (100 * myy), 10, 10);

		}
		repaint();
	}

	public static void main(String[] args) {
		tests t = new tests();
		t.update();
	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if (!movingmouse) {
			a += (double) (getWidth() / 2 - e.getX()) / 200;

			try {
				new Robot().mouseMove(getWidth() / 2, getHeight() / 2);
			} catch (AWTException e1) {
			}
		}

		update();
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		try {
			if (e.getKeyCode() == KeyEvent.VK_E) {
				movingmouse = !movingmouse;
				if(!movingmouse){
					

				this.getContentPane().setCursor(blankCursor);
				}
				
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				System.exit(ABORT);
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {
				if (mapy[(int) Math.floor(myx + Math.sin(a) / 16)][(int) Math.floor(myy - Math.cos(a) / 16)] == 0) {
					myx += Math.sin(a) / 16;
					myy -= Math.cos(a) / 16;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				if (mapy[(int) Math.floor(myx - Math.sin(a) / 16)][(int) Math.floor(myy + Math.cos(a) / 16)] == 0) {
					myx -= Math.sin(a) / 16;
					myy += Math.cos(a) / 16;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				if (mapy[(int) Math.floor(myx - Math.cos(a) / 16)][(int) Math.floor(myy - Math.sin(a) / 16)] == 0) {
					myx -= Math.cos(a) / 16;
					myy -= Math.sin(a) / 16;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_W) {
				if (mapy[(int) Math.floor(myx + Math.cos(a) / 16)][(int) Math.floor(myy + Math.sin(a) / 16)] == 0) {
					myx += Math.cos(a) / 16;
					myy += Math.sin(a) / 16;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_O) {
				fovinv += 0.1;
			}

			if (e.getKeyCode() == KeyEvent.VK_P) {
				fovinv -= 0.1;
			}
		} catch (ArrayIndexOutOfBoundsException e1) {
		}
		try {
			fov = 1 / fovinv;
		} catch (ArithmeticException nul) {
			fov = 0;
		}
		update();

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
