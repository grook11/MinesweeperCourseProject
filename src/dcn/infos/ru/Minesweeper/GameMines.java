package dcn.infos.ru.Minesweeper;

//библиотека для работы с окном
import java.awt.*;

//библиотека для обработки событий при нажатии мыши
import java.awt.event.*;

//библиотка для реализации графического интерфейса 
import javax.swing.*;

//пакет для работы с коллекциями
import java.util.*;

//JFrame реализует оконное отображение
class GameMines extends JFrame {

	final String TITLE_OF_PROGRAM = "Minesweeper";
	final String SIGN_OF_FLAG = "f";
	final int BLOCK_SIZE = 30;// рамер ячейки
	final int FIELD_SIZE = 9;// размер поля- 9x9(в ячейках)
	final int FIELD_DX = 6;
	final int FIELD_DY = 38+27;
	final int START_LOCATION = 200;// начальное положение окна программы
	final int MOUSE_BUTTON_LEFT = 1;
	final int MOUSE_BUTTON_RIGHT = 3;
	final int NUMBER_OF_MINES = 10;// количество мин

	// массив цветов для обознаяения цифр, указывающих на количество мин
	final int[] COLOR_OF_NUMBERS = { 0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0 };

	Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];// массив ячеек
	Random random = new Random();
	int countOpenedCells;// количество открытых ячеек
	boolean winFlag;// переманная для обозначения конца
					// игры(0-проигрыш,1-победа)
	boolean bangMine;// переманная для обозначения взрыва мины(1-подрыв на мине)
	int bangX, bangY;// координаты для обозначения ячейки, на которой подорвался
						// игрок

	public static void main(String[] args) {
		GameMines Minesweeper = new GameMines();
	}

	GameMines() {// конструктор по-умолчанию
		//setTitle(TITLE_OF_PROGRAM);// создание окна программы
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// стартовое положение окна и его размеры
		setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX,
				FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
		setResizable(false);// отключение изменения размера окна
		Canvas canvas = new Canvas();// создание формы внутри окна
		canvas.setBackground(Color.white);// установка цвета заднего фона
		// Иницализатор нажатия мыши
		canvas.addMouseListener(new MouseAdapter() {
			//Переопределяем метод mouseReleased -действие при нажатии мыши после отпускания кнопки
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				//Получает относительные координаты клика мыши(находи ячейку, на которую кликнули)
				int x = e.getX() / BLOCK_SIZE; 
				int y = e.getY() / BLOCK_SIZE;
				
				if (!bangMine && !winFlag) {//условие(мина не взорвана и вы ещё не победили)
					if (e.getButton() == MOUSE_BUTTON_LEFT) //переопределение действия при нажатии левой кнопки мыши
						if (field[y][x].isNotOpen()) {//условие(ячейка не открыта)
							//openCells(x, y);
							field[y][x].open();//открытие ячейки
							//Проверка на победу(если все ячейки открыты и при этом вы не взорвались - победа)
							winFlag = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES;														
							//Запоминание координат, где взорвалась мина
							if (bangMine) {
								bangX = x;
								bangY = y;
							}
						}
					if (e.getButton() == MOUSE_BUTTON_RIGHT) //переопределение действия при нажатии правой кнопки мыши
						field[y][x].inverseFlag(); //установка флага на предположительное положение мины

					//if (bangMine || winFlag) timeLabel.stopTimer(); // game over
					canvas.repaint();//обновление рабочей формы
				}
			}
		});
        add(BorderLayout.CENTER, canvas);//центральное расположение формы
        //add(BorderLayout.SOUTH, timeLabel);
        setVisible(true);//устанавливает окно видимым
        initField();
	}

	// Метод иницализирующий игровое поле
	void initField() {
		int x, y, countMines = 0;
        //Заполнение всех ячеик объектами Cell
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();
        //Расставляет мины по полю в случайных ячейках
        while (countMines < NUMBER_OF_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());//Предотвращение повторного минирования одной и той же ячейки
            field[y][x].mine();
            countMines++;
        }
        //счетчик мин вокруг ячейки и установка этого значения в ячейку
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
		private boolean isOpen;//индикатор открытия ячейки
		private boolean isMine;//индикатор бомбы в ячейки
		private boolean isFlag;//индикатор флага над ячейкой
		private int countBombNear;//количество бомб вблизи ячейки
		
		//метод открытия ячейки
		void open(){
			isOpen=true;
			bangMine=isMine;
			if(!isMine)
				countOpenedCells++;
		}
		
		//установка мины в ячейку
		void mine(){
			isMine=true;
		}
		
		//записывет в ячейку количество соседних бомб
		void setCountBomb(int count){
			countBombNear=count;
		}
		
		//возвращает количество соседних бомб
		int getCountBomb(){
			return countBombNear;
		}
		
		//Проверка - открыта ли ячейка
		boolean isNotOpen(){
			return !isOpen;
		}
		
		//Проверка - заминирована ли ячейка
		boolean isMined(){	
			return  isMine;
		}
		
		//Инвертирование флага
		void inverseFlag(){
			isFlag=!isFlag;
		}
		//Метод для рисования бомбы в ячейке
        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }
        //Метод для рисование в ячейки числа ближайших бомб
        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }
        //Метод для определения, объктов, которые требуется нарисовать в ячейках
        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);//рисование ячеек(деление серой формы на квадраты)
            if (!isOpen) {//Если ячейка не была открыта
            	//открытие ячеек с бомбами при проигрыше или выиграше
                if ((bangMine || winFlag) && isMine) paintBomb(g, x, y, Color.black);
                else {
                	//пометка неоткрытых ячеек серыми блоками
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                }
            } else//если ячейка открыта
            	//если в ней бомба, помечаем эту бомбу(красным цветом, если мы подорвались именно на ней, в противном случае - серым цветом)
                if (isMine) paintBomb(g, x, y, bangMine? Color.red : Color.black);
                else
                	//если бомбы нет, пишем в ячейку кол-во ближайших бомб, если они есть
                    if (countBombNear > 0)
                        paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }
    }
	

	// Игровая форма
	class Canvas extends JPanel {
		//переопределяем метод paint класса JPanel
	        public void paint(Graphics g) {
	            super.paint(g);
	            //отрисовываем объект в каждой ячейке
	            for (int x = 0; x < FIELD_SIZE; x++)
	                for (int y = 0; y < FIELD_SIZE; y++) 
	                	field[y][x].paint(g, x, y);
	        }
	}

}