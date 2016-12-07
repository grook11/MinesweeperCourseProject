package dcn.infos.ru.Minesweeper;

//���������� ��� ������ � �����
import java.awt.*;

//���������� ��� ��������� ������� ��� ������� ����
import java.awt.event.*;

//��������� ��� ���������� ������������ ���������� 
import javax.swing.*;

//����� ��� ������ � �����������
import java.util.*;

//JFrame ��������� ������� �����������
class GameMines extends JFrame {

	final String TITLE_OF_PROGRAM = "Minesweeper";
	final String SIGN_OF_FLAG = "f";
	final int BLOCK_SIZE = 30;// ����� ������
	final int FIELD_SIZE = 9;// ������ ����- 9x9(� �������)
	final int FIELD_DX = 6;
	final int FIELD_DY = 38+27;
	final int START_LOCATION = 200;// ��������� ��������� ���� ���������
	final int MOUSE_BUTTON_LEFT = 1;
	final int MOUSE_BUTTON_RIGHT = 3;
	final int NUMBER_OF_MINES = 10;// ���������� ���

	// ������ ������ ��� ����������� ����, ����������� �� ���������� ���
	final int[] COLOR_OF_NUMBERS = { 0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0 };

	Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];// ������ �����
	Random random = new Random();
	int countOpenedCells;// ���������� �������� �����
	boolean winFlag;// ���������� ��� ����������� �����
					// ����(0-��������,1-������)
	boolean bangMine;// ���������� ��� ����������� ������ ����(1-������ �� ����)
	int bangX, bangY;// ���������� ��� ����������� ������, �� ������� ����������
						// �����

	public static void main(String[] args) {
		GameMines Minesweeper = new GameMines();
	}

	GameMines() {// ����������� ��-���������
		//setTitle(TITLE_OF_PROGRAM);// �������� ���� ���������
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// ��������� ��������� ���� � ��� �������
		setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX,
				FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
		setResizable(false);// ���������� ��������� ������� ����
		Canvas canvas = new Canvas();// �������� ����� ������ ����
		canvas.setBackground(Color.white);// ��������� ����� ������� ����
		// ������������ ������� ����
		canvas.addMouseListener(new MouseAdapter() {
			//�������������� ����� mouseReleased -�������� ��� ������� ���� ����� ���������� ������
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				//�������� ������������� ���������� ����� ����(������ ������, �� ������� ��������)
				int x = e.getX() / BLOCK_SIZE; 
				int y = e.getY() / BLOCK_SIZE;
				
				if (!bangMine && !winFlag) {//�������(���� �� �������� � �� ��� �� ��������)
					if (e.getButton() == MOUSE_BUTTON_LEFT) //��������������� �������� ��� ������� ����� ������ ����
						if (field[y][x].isNotOpen()) {//�������(������ �� �������)
							//openCells(x, y);
							field[y][x].open();//�������� ������
							//�������� �� ������(���� ��� ������ ������� � ��� ���� �� �� ���������� - ������)
							winFlag = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES;														
							//����������� ���������, ��� ���������� ����
							if (bangMine) {
								bangX = x;
								bangY = y;
							}
						}
					if (e.getButton() == MOUSE_BUTTON_RIGHT) //��������������� �������� ��� ������� ������ ������ ����
						field[y][x].inverseFlag(); //��������� ����� �� ����������������� ��������� ����

					//if (bangMine || winFlag) timeLabel.stopTimer(); // game over
					canvas.repaint();//���������� ������� �����
				}
			}
		});
        add(BorderLayout.CENTER, canvas);//����������� ������������ �����
        //add(BorderLayout.SOUTH, timeLabel);
        setVisible(true);//������������� ���� �������
        initField();
	}

	// ����� ��������������� ������� ����
	void initField() {
		int x, y, countMines = 0;
        //���������� ���� ����� ��������� Cell
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();
        //����������� ���� �� ���� � ��������� �������
        while (countMines < NUMBER_OF_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());//�������������� ���������� ����������� ����� � ��� �� ������
            field[y][x].mine();
            countMines++;
        }
        //������� ��� ������ ������ � ��������� ����� �������� � ������
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {                    
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);
                }
	}

	class Cell {
		private boolean isOpen;//��������� �������� ������
		private boolean isMine;//��������� ����� � ������
		private boolean isFlag;//��������� ����� ��� �������
		private int countBombNear;//���������� ���� ������ ������
		
		//����� �������� ������
		void open(){
			isOpen=true;
			bangMine=isMine;
			if(!isMine)
				countOpenedCells++;
		}
		
		//��������� ���� � ������
		void mine(){
			isMine=true;
		}
		
		//��������� � ������ ���������� �������� ����
		void setCountBomb(int count){
			countBombNear=count;
		}
		
		//���������� ���������� �������� ����
		int getCountBomb(){
			return countBombNear;
		}
		
		//�������� - ������� �� ������
		boolean isNotOpen(){
			return !isOpen;
		}
		
		//�������� - ������������ �� ������
		boolean isMined(){	
			return  isMine;
		}
		
		//�������������� �����
		void inverseFlag(){
			isFlag=!isFlag;
		}
		//����� ��� ��������� ����� � ������
        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }
        //����� ��� ��������� � ������ ����� ��������� ����
        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }
        //����� ��� �����������, �������, ������� ��������� ���������� � �������
        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);//��������� �����(������� ����� ����� �� ��������)
            if (!isOpen) {//���� ������ �� ���� �������
            	//�������� ����� � ������� ��� ��������� ��� ��������
                if ((bangMine || winFlag) && isMine) paintBomb(g, x, y, Color.black);
                else {
                	//������� ���������� ����� ������ �������
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                }
            } else//���� ������ �������
            	//���� � ��� �����, �������� ��� �����(������� ������, ���� �� ����������� ������ �� ���, � ��������� ������ - ����� ������)
                if (isMine) paintBomb(g, x, y, bangMine? Color.red : Color.black);
                else
                	//���� ����� ���, ����� � ������ ���-�� ��������� ����, ���� ��� ����
                    if (countBombNear > 0)
                        paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }
    }
	

	// ������� �����
	class Canvas extends JPanel {
		//�������������� ����� paint ������ JPanel
	        public void paint(Graphics g) {
	            super.paint(g);
	            //������������ ������ � ������ ������
	            for (int x = 0; x < FIELD_SIZE; x++)
	                for (int y = 0; y < FIELD_SIZE; y++) 
	                	field[y][x].paint(g, x, y);
	        }
	}

}