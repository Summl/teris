package tetris;

import java.util.Arrays;
import java.util.Random;

/**
 * 4格方块tetromino，包含七个子类T I J L S Z O
 */
public class Tetromino {
	//创建四个格子
	protected Cell[] cells = new Cell[4];
	/** 保存旋转的相对于轴位置状态̬ 0号位置是轴*/
	protected State[] states;
	
	/** 随机生成 4格方块
	 * randomTetromino 随机生成一个四格方块 
	 * 这个方面的返回值是多态的!
	 * */
	public static Tetromino randomTetromino(){
		Random r = new Random();
		int type = r.nextInt(7);//随机产生7个数，分别代表不同的四方格类型
		switch(type){
		case 0: return new T();
		case 1: return new I();
		case 2: return new J();
		case 3: return new L();
		case 4: return new O();
		case 5: return new S();
		case 6: return new Z();
		}
		return null;
	}
	
	public Cell[] getCells() {
		return cells;
	}

	/** 下落 软着陆*/
	public void softDrop(){
		for(int i=0; i<cells.length; i++){
			//使每一个方块下落
			cells[i].moveDown();
		}
	}
	//右移
	public void moveRight(){
		//System.out.println("moveRight()");
		for(int i=0; i<cells.length; i++){
			this.cells[i].moveRight();
		}
	} 
	//左移
	public void moveLeft(){
		for(int i=0; i<cells.length; i++){
			cells[i].moveLeft();
		}
	}
	
	private int index = 100000;
	/** 在 Tetromino 上添加方法 向右旋转 */
	public void rotateRight() {
		//取得变换的下个数据状态state[n]
		index++;//index = 10001
		// index % states.length = 10001 % 4 = 1
		//旋转状态的序号state[index%state.length]
		State s = states[index%states.length];//s1
		// [0] + s1 = [1]
		Cell o = cells[0];//获取当前的轴
		//轴与相对位置的和作为旋转以后的格子位置，旋转以后的数据=(row,col) + state[n]
		cells[1].setRow(o.getRow()+s.row1);
		cells[1].setCol(o.getCol()+s.col1);
		cells[2].setRow(o.getRow()+s.row2);
		cells[2].setCol(o.getCol()+s.col2);
		cells[3].setRow(o.getRow()+s.row3);
		cells[3].setCol(o.getCol()+s.col3);
	}
	/** 在 Tetromino 上添加方法 向左旋转 */
	public void rotateLeft() {
		index--;//index = 10001
		// index % states.length = 10001 % 4 = 1
		State s = states[index%states.length];//s1
		// [0] + s1 = [1]
		Cell o = cells[0];//获取当前的轴
		cells[1].setRow(o.getRow()+s.row1);
		cells[1].setCol(o.getCol()+s.col1);
		cells[2].setRow(o.getRow()+s.row2);
		cells[2].setCol(o.getCol()+s.col2);
		cells[3].setRow(o.getRow()+s.row3);
		cells[3].setCol(o.getCol()+s.col3);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(cells); 
	}
	
	/** Tetromino 类中添加的 内部类 用于记录旋转状态 */
	protected class State{
		//转一圈8个数
		int row0,col0,row1,col1,row2,col2,row3,col3;
        //构造器
		public State(int row0, int col0, int row1, int col1,
				int row2, int col2,
				int row3, int col3) {
			this.row0 = row0;
			this.col0 = col0;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.row3 = row3;
			this.col3 = col3;
		}	  
	}
	
}//Tetromino 类的结束
//7种不同形状的四方格以及旋转过程中所有的排列
//顺时针旋转，向右，向下为正
class T extends Tetromino{
	public T() {
		cells[0] = new Cell(0, 4, Tetris.T);
		cells[1] = new Cell(0, 3, Tetris.T);
		cells[2] = new Cell(0, 5, Tetris.T);
		cells[3] = new Cell(1, 4, Tetris.T);
		states = new State[]{
				new State(0,0, 0,-1, 0,1, 1, 0),
				new State(0,0, -1,0, 1,0, 0,-1),
				new State(0,0, 0,1,  0,-1, -1,0),
				new State(0,0, 1,0, -1,0, 0,1)};
	}
}

class I extends Tetromino{
	public I() {
		cells[0] = new Cell(0, 4, Tetris.I);
		cells[1] = new Cell(0, 3, Tetris.I);
		cells[2] = new Cell(0, 5, Tetris.I);
		cells[3] = new Cell(0, 6, Tetris.I);
		states = new State[]{
				new State(0,0, 0,-1, 0,1, 0,2),
				new State(0,0, -1,0, 1,0,2,0)};
	}
}
class L extends Tetromino {
	public L() {
		cells[0] = new Cell(0, 4, Tetris.L);
		cells[1] = new Cell(0, 3, Tetris.L);
		cells[2] = new Cell(0, 5, Tetris.L);
		cells[3] = new Cell(1, 3, Tetris.L);
		states = new State[]{
				new State(0,0, 0,-1, 0,1, 1,-1 ),
				new State(0,0, -1,0, 1,0, -1,-1),
				new State(0,0, 0,1, 0,-1, -1,1),
				new State(0,0, 1,0, -1,0, 1,1)};	
	}
}

class J extends Tetromino {
	public J() {
		cells[0] = new Cell(0, 4, Tetris.J);
		cells[1] = new Cell(0, 3, Tetris.J);
		cells[2] = new Cell(0, 5, Tetris.J);
		cells[3] = new Cell(1, 5, Tetris.J);
		states = new State[]{
				new State(0,0, 0,-1, 0,1, 1,1),
				new State(0,0, -1,0, 1,0, 1,-1),
				new State(0,0, 0,1, 0,-1, -1,-1),
				new State(0,0, 1,0, -1,0, -1,1 )};
	}
}

class S extends Tetromino {
	public S() {
		cells[0] = new Cell(0, 4, Tetris.S);
		cells[1] = new Cell(0, 5, Tetris.S);
		cells[2] = new Cell(1, 3, Tetris.S);
		cells[3] = new Cell(1, 4, Tetris.S);
		states = new State[]{
			new State(0,0, 0,1, 1,-1, 1,0 ),
			new State(0,0, -1,0, 1,1, 0,1 )};
	}
}

class Z extends Tetromino {
	public Z() {
		cells[0] = new Cell(1, 4, Tetris.Z);
		cells[1] = new Cell(0, 3, Tetris.Z);
		cells[2] = new Cell(0, 4, Tetris.Z);
		cells[3] = new Cell(1, 5, Tetris.Z);
		states = new State[]{
				new State(0,0, -1,-1, -1,0, 0,1 ),
				new State(0,0, -1,1, 0,1, 1,0 )};
	}
}

class O extends Tetromino {
	public O() {
		cells[0] = new Cell(0, 4, Tetris.O);
		cells[1] = new Cell(0, 5, Tetris.O);
		cells[2] = new Cell(1, 4, Tetris.O);
		cells[3] = new Cell(1, 5, Tetris.O);
		states = new State[]{
				new State(0,0, 0,1, 1,0, 1,1 )
				};
	}
}











