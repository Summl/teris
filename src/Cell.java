package tetris;

import java.awt.Image;

/**
 * 格子 每一个小格子，就有所在的行 列 和图片
 */
public class Cell {
	private int row;// 行数
	private int col;// 列数
	// private int color;
	private Image image;// 格子的贴图

	public Cell() {
	}

	// 格子的构造方法，用来初始化Cell对象
	public Cell(int row, int col, Image image) {
		super();
		this.row = row;
		this.col = col;
		this.image = image;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void moveRight() {// 右移
		col++;
		// System.out.println("Cell moveRight()" + col);
	}

	public void moveLeft() {// 左移
		col--;
	}

	public void moveDown() {// 下移
		row++;
	}

	@Override
	public String toString() {
		return "[" + row + "," + col + "]";
	}

}
