package tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 俄罗斯方块游戏面板（主函数）
 *
 */
public class Tetris extends JPanel {

	/** 正在下落方块 */
	private Tetromino tetromino;
	/** 下一个下落方块 */
	private Tetromino nextOne;
	/** 总行数 20 */
	public static final int ROWS = 20;
	/** 总列数 10 */
	public static final int COLS = 10;
	/** 墙 */
	private Cell[][] wall = new Cell[ROWS][COLS];
	/** 消掉的行数 */
	private int lines;
	/** 分数 */
	private int score;
	/** 每一个格子尺寸 */
	public static final int CELL_SIZE = 26;

	private static Image background;// 背景图片
	public static Image I;
	public static Image J;
	public static Image L;
	public static Image S;
	public static Image Z;
	public static Image O;
	public static Image T;
	static {// 加载静态资源的，加载图片
			// 建议将图片放到 Tetris.java 同包中!
			// 从包中加载图片对象，使用Swing API实现
//		Toolkit toolkit = Toolkit.getDefaultToolkit();
//		background = toolkit.getImage(
//				Tetris.class.getResource("tetris.png"));
//		T = toolkit.getImage(Tetris.class.getResource("T.png"));
//		S = toolkit.getImage(Tetris.class.getResource("S.png"));
//		Z = toolkit.getImage(Tetris.class.getResource("Z.png"));
//		L = toolkit.getImage(Tetris.class.getResource("L.png"));
//		J = toolkit.getImage(Tetris.class.getResource("J.png"));
//		I = toolkit.getImage(Tetris.class.getResource("I.png"));
//		O = toolkit.getImage(Tetris.class.getResource("O.png"));

		// ImageIO.read()默认可能会抛出IOException异常，
		// 因此在加载图片资源时需要使用异常机制尝试捕获和处理异常。
		try {
			background = ImageIO.read(Tetris.class.getResource("tetris.png"));
			T = ImageIO.read(Tetris.class.getResource("T.png"));
			I = ImageIO.read(Tetris.class.getResource("I.png"));
			S = ImageIO.read(Tetris.class.getResource("S.png"));
			Z = ImageIO.read(Tetris.class.getResource("Z.png"));
			L = ImageIO.read(Tetris.class.getResource("L.png"));
			J = ImageIO.read(Tetris.class.getResource("J.png"));
			O = ImageIO.read(Tetris.class.getResource("O.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 添加启动方法
	public void action() {
		// tetromino = Tetromino.randomTetromino();
		// nextOne = Tetromino.randomTetromino();
		// wall[19][2] = new Cell(19,2,Tetris.T);
		// 启动
		startAction();
		repaint();// main方法中调用了action(),而action()方法中多次使用repaint()方法进行页面刷新。
		// 在paint()方法中，实现具体的画图操作，都是使用Graphics的相关函数

		// 使用不同的键位控制对象运动
		KeyAdapter l = new KeyAdapter() {// 接收键盘事件的抽象适配器
			// 键盘监听 按下键盘
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_Q) {// Q键 退出当前的Java进程
					System.exit(0);
				}

				if (gameOver) {// 如果游戏结束
					if (key == KeyEvent.VK_S) {// S键 重新开始
						startAction();
					}
					return;
				}
				// 如果暂停并且按键是[C]就继续动作
				if (pause) {// pause = false
					if (key == KeyEvent.VK_C) {
						continueAction();
					}
					return;
				}
				// 否则处理其它按键
				switch (key) {
				case KeyEvent.VK_RIGHT:
					moveRightAction();
					break;// 右箭头右移
				case KeyEvent.VK_LEFT:
					moveLeftAction();
					break;// 左箭头左移
				case KeyEvent.VK_DOWN:
					softDropAction();
					break;// 下箭头软着陆
				case KeyEvent.VK_UP:
					rotateRightAction();
					break;// 上箭头向右旋转
				case KeyEvent.VK_Z:
					rotateLeftAction();
					break;// Z键向左旋转
				case KeyEvent.VK_SPACE:
					hardDropAction();
					break;// 空格键 硬着陆
				case KeyEvent.VK_P:
					pauseAction();
					break;// P键 暂停
				}
				// 将按键动作与repaint()绑定，实现运动效果
				repaint();
			}
		};
		// 绑定事件到当前面板
		this.requestFocus();
		this.addKeyListener(l);
	}

	// 调用画笔画图
	// 重写父类JPanel方法，重写paint()修改原有绘制方法
	public void paint(Graphics g) {
		// 画背景
		g.drawImage(background, 0, 0, null);
		// 平移绘图坐标系
		g.translate(15, 15);
		// 绘制正在下落的方块
		paintTetromino(g);
		// 画墙
		paintWall(g);
		// 画下一个方块
		paintNextOne(g);
		// 画分数
		paintScore(g);
	}

	public static final int FONT_COLOR = 0x667799;// 设置字体颜色
	public static final int FONT_SIZE = 0x20;// 设置字体大小
	// 设置右边成绩及其他提示信息

	private void paintScore(Graphics g) {
		Font f = getFont();// 获取当前的面板默认字体
		Font font = new Font(f.getName(), Font.BOLD, FONT_SIZE);
		// 设置绘制地点
		int x = 290;// 基线位置
		int y = 162;// 基线位置
		g.setColor(new Color(FONT_COLOR));// 颜色
		g.setFont(font);// 设置字体
		String str = "SCORE:" + this.score;
		g.drawString(str, x, y);
		// 画行数
		y += 56;
		str = "LINES:" + this.lines;
		g.drawString(str, x, y);
		// 画提示
		y += 56;
		str = "[P]Pause";
		if (pause) {
			str = "[C]Continue";
		}
		if (gameOver) {
			str = "[S]Start";
		}
		g.drawString(str, x, y);
	}

	// 下一个四方格子，将每个格子的row、col换算成x、y，然后贴图
	private void paintNextOne(Graphics g) {
		Cell[] cells = nextOne.getCells();
		for (int i = 0; i < cells.length; i++) {// i=0、1、2、3
			Cell c = cells[i];
			// 右边显示下一个4方格子
			int x = (c.getCol() + 10) * CELL_SIZE - 1;// 向右移动10的位置
			int y = (c.getRow() + 1) * CELL_SIZE - 1;// 向下移动1的位置
			g.drawImage(c.getImage(), x, y, null);// 贴图
		}
	}

	// 画当前下落的四方格子
	private void paintTetromino(Graphics g) {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell c = cells[i];
			int x = c.getCol() * CELL_SIZE - 1;// 计算长度
			int y = c.getRow() * CELL_SIZE - 1;// 计算长度
			// g.setColor(new Color(c.getColor()));
			// g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
			g.drawImage(c.getImage(), x, y, null);// 贴图
		}
	}

	// 在 Tetris 类中添加方法 paintWall 画墙
	private void paintWall(Graphics g) {
		for (int row = 0; row < wall.length; row++) {
			// 迭代每一行, i = 0 1 2 ... 19
			Cell[] line = wall[row];// 墙上的每一行
			// line.length = 10
			for (int col = 0; col < line.length; col++) {
				Cell cell = line[col]; // 墙上的每个格子
				int x = col * CELL_SIZE; // 计算长度
				int y = row * CELL_SIZE;// 计算长度
				if (cell == null) {
					// g.setColor(new Color(0));
					// 没格子画框
					// g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				} else {
					// 有格子贴图
					g.drawImage(cell.getImage(), x - 1, y - 1, null);
				}
			}
		}
	}

	/**
	 * 在 Tetris(俄罗斯方块) 类中增加方法 这个方法的功能是：软下落的动作 控制流程 完成功能：如果能够下落就下落，否则就着陆到墙上，
	 * 而新的方块出现并开始落下。
	 */
	public void softDropAction() {
		if (tetrominoCanDrop()) {// 如果能下落
			tetromino.softDrop();// 下落一步
		} else {
			tetrominoLandToWall();// 着陆到墙上
			destroyLines();// 破坏满的行
			checkGameOver();// 检查是否结束
			tetromino = nextOne;
			nextOne = Tetromino.randomTetromino();// 入墙一个方块，出现下一个方块
		}
	}

	/**
	 * 销毁已经满的行，并且计分 1）迭代每一行 2）如果（检查）某行满是格子了 就销毁这行
	 **/
	public void destroyLines() {
		int lines = 0;
		for (int row = 0; row < wall.length; row++) {
			if (fullCells(row)) {
				deleteRow(row);
				lines++;
			}
		}
		// lines = ?
		this.lines += lines;// 0 1 2 3 4
		this.score += SCORE_TABLE[lines];
	}

	// 得分表
	private static final int[] SCORE_TABLE = { 0, 1, 10, 30, 200 };// 消除一行1分，2行10分，3行30分，4行200分

	// 检查当前行的每个格子，如果有null，就返回false，否则返回true
	public boolean fullCells(int row) {
		Cell[] line = wall[row];
		for (int i = 0; i < line.length; i++) {//// 逐行检查
			if (line[i] == null) {// 如果有空格式就不是满行
				return false;
			}
		}
		return true;
	}

	// 消除行
	public void deleteRow(int row) {
		for (int i = row; i >= 1; i--) {// 逐行查找
			// 复制 [i-1] -> [i]
			System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);// 循环将游戏面板中的每一行复制到下一行
		}
		Arrays.fill(wall[0], null);// 第一行用null填充
	}

	/** 检查当前的4格方块能否继续下落 */
	public boolean tetrominoCanDrop() {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			if (row == ROWS - 1) {
				return false;
			} // 到底部就不能下降了
		}
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			if (wall[row + 1][col] != null) {
				return false;// 下方墙上有方块就不能下降了
			}
		}
		return true;
	}

	/** 4格方块着陆到墙上 */
	public void tetrominoLandToWall() {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			wall[row][col] = cell;// 四方格子着陆到墙上的位置
		}
	}

	// 4格方块右移
	public void moveRightAction() {
		// 尝试先向右移动，如果到达边界或重合,右移键失效，修正回来
		tetromino.moveRight();
		if (outOfBound() || coincide()) {
			tetromino.moveLeft();
		}
	}

	// 4格方块左移
	public void moveLeftAction() {
		// 尝试先向左移动，如果到达边界或重合,左移键失效，修正回来
		tetromino.moveLeft();
		if (outOfBound() || coincide()) {
			tetromino.moveRight();
		}
	}

	/** 判断当前下落的4四格方块是否出界 */
	// 迭代正在下落的方块，其中某一个格子列坐标出界，就移回
	private boolean outOfBound() {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];// cell某一个格
			int col = cell.getCol();
			if (col < 0 || col >= COLS) {// 列坐标小于0，大于9
				return true;// 出界了
			}
		}
		return false;
	}

	// 检查正在下落的方块和墙上的方块是否重叠
	private boolean coincide() {
		Cell[] cells = tetromino.getCells();
		// for each 循环、迭代，简化了"数组迭代书写"
		for (Cell cell : cells) {// for each循环
			int row = cell.getRow();
			int col = cell.getCol();
			if (row < 0 || row >= ROWS || col < 0 || col >= COLS || wall[row][col] != null) {
				return true; // 墙上有格子对象，发生重合
			}
		}
		return false;
	}

	/** 向右顺时针旋转动作 */
	public void rotateRightAction() {
		// 旋转之前
		// System.out.println(tetromino);
		tetromino.rotateRight();
		// System.out.println(tetromino);
		// 旋转之后
		if (outOfBound() || coincide()) {
			tetromino.rotateLeft();
		}
	}

	/** 向左逆时针旋转 */
	public void rotateLeftAction() {
		tetromino.rotateLeft();
		if (outOfBound() || coincide()) {
			tetromino.rotateRight();
		}
	}

	// 硬着陆 快速下落
	public void hardDropAction() {
		while (tetrominoCanDrop()) {
			tetromino.softDrop();
		}
		tetrominoLandToWall();
		destroyLines();
		checkGameOver();
		tetromino = nextOne;
		nextOne = Tetromino.randomTetromino();
	}

	private boolean pause;
	private boolean gameOver;
	private Timer timer;

	/** Tetris 类中添加的方法, 用于启动游戏 */
	public void startAction() {
		clearWall();
		tetromino = Tetromino.randomTetromino();
		nextOne = Tetromino.randomTetromino();
		lines = 0;
		score = 0;
		pause = false;
		gameOver = false;
		timer = new Timer();// 创建定时器
		// 定时器的定时执行装置
		timer.schedule(new TimerTask() {
			public void run() {
				// 软着陆动作
				softDropAction();
				// repaint()方法，该方法的作用是调用paint()方法对窗口所有对象进行重画，
				// 也就是利用这个方法在一秒内对窗口进行几十次的重画，才能实现对象的运动
				repaint();
			}
		}, 700, 700);// 第一次执行时间7毫秒后，之后延迟时间也是7毫秒
	}

	private void clearWall() {
		// 将墙的每一行的每个格子清理为null
		for (int row = 0; row < ROWS; row++) {
			Arrays.fill(wall[row], null);
		}
	}

	/** 在Tetris 类中添加方法 暂停 继续 */
	public void pauseAction() {
		timer.cancel(); // ͣ停止定时器
		pause = true;
		repaint();
	}

	public void continueAction() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, 700, 700);// 第一次执行时间7毫秒后，之后延迟时间也是7毫秒
		pause = false;
		repaint();
	}

	/** 在 Tetris 类中添加方法 检查是否结束 */
	public void checkGameOver() {
		if (wall[0][4] == null) {
			return;
		}
		gameOver = true;
		timer.cancel();
		repaint();
	}

	// 添加背景音乐
	public static void music() {
		try {
			Clip bgm = AudioSystem.getClip();
			InputStream is = Tetris.class.getResourceAsStream("bgm.wav");
			InputStream bufferedIn = new BufferedInputStream(is);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			bgm.open(audioStream);// 开始播放
			bgm.start();
			bgm.loop(Clip.LOOP_CONTINUOUSLY);// 重复播放
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
			((Throwable) e1).printStackTrace();
		}
	}

	// 游戏界面
	public static void showgame() {
		JFrame frame = new JFrame();
		Tetris tetris = new Tetris();
		frame.add(tetris);
		frame.setSize(525, 590);
		frame.setResizable(false);
		frame.setUndecorated(false);// true去掉窗口框！
		frame.setTitle("俄罗斯方块");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Location 位置 RelativeTo相对于
		frame.setLocationRelativeTo(null);// 使当前窗口居中
		frame.setVisible(true);// 可见
		tetris.action();// 调用动作
	}

	// 初始化显示界面
	public static void showexplain() {
		JFrame f1 = new JFrame("俄罗斯方块");
		f1.setLocationRelativeTo(null);
		f1.setSize(320, 280);
		f1.setResizable(false);
		// f1.setUndecorated(false);
		f1.setLayout(new BorderLayout());
		TextArea txt1 = new TextArea(" 用户手册：\t\t\t\t\t积分规则：" + " \n\t" + "←左移动\t→右移动\t\t\t连消0行 得0分" + " \n\t"
				+ "↓软着陆\t空格硬着陆\t\t\t连消1行 得1分" + " \n\t" + "↑右旋转\tZ左旋转\t\t\t连消2行 得10分" + " \n\t"
				+ "P暂停\t\tC继续\t\t\t连消3行 得30分" + " \n\t" + "S重新开始\t\t\t\t\t连消4行 得200分" + " \n\t" + "向下滑动滑轮显示积分规则！");
		f1.add(txt1);
		// Font f = getFont();//获取当前的 面板默认字体
		txt1.setFont(new Font("黑体", Font.BOLD, 20));
		JButton btn1 = new JButton("确定");
		f1.add(btn1, BorderLayout.SOUTH);
		f1.setLocationRelativeTo(null);
		f1.setVisible(true);// 可见
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				f1.dispose();

				// 调背景音乐
				music();

				// 游戏窗口
				showgame();
			}
		});
	}

	public static void main(String[] args) {
		showexplain();

	}

}
