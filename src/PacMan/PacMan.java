package PacMan;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;


public class PacMan extends JPanel {
    private static final int TILE_SIZE = 26;
    private static final int ROWS = 31;
    private static final int COLUMNS = 28;

    Direction mDirection = Direction.RIGHT;

    List<Direction> directions = Arrays.asList(Direction.values());


    private Image foodPacMan;
    private Image monster1;
    private Image monster2;
    private Image monster3;
    private Image monster4;
    private Image ScoreRed;
    private Image mUpPacmanImage;
    private Image mUpPacmanImage2;
    private Image mDownPacmanImage;
    private Image mDownPacmanImage2;
    private Image mRightPacmanImage;
    private Image mRightPacmanImage2;
    private Image mLeftPacmanImage;
    private Image mLeftPacmanImage2;

    public int scorForPrice = 0;
    public int countLife = 3;
    private int[][] maze;
    // מיקום התחלתי של Monster + PacMan
    private int pacmanRow = 18, pacmanCol = 13;
    private boolean isMouthOpen = true;
    private int[][] monsteresLocation = {{14, 16, 0}, {14, 15, 0}, {14, 14, 0}, {14, 13, 0}};
    private Timer monsterTime;
    private Timer pacmanTimer;


    public PacMan() {
        initMaze(false);
        setBoard();
        loadImages();
        initListeners();
        resetBoard();
        runMonster();
        runPacmen();

    }

    private void resetBoard() {
        pacmanRow = 23;
        pacmanCol = 13;

        monsteresLocation = new int[][]{{14, 16, 0}, {14, 15, 0}, {14, 14, 0}, {14, 13, 0}};

        mDirection = Direction.RIGHT;
        // רענון המסך
        repaint();
    }

    private void setBoard() {
        setPreferredSize(new Dimension(COLUMNS * TILE_SIZE, ROWS * TILE_SIZE));
        setFocusable(true);
    }

    private void runMonster() {
        if (monsterTime == null) monsterTime = new Timer(150, e -> {
            moveMonster(0);
            moveMonster(1);
            moveMonster(2);
            moveMonster(3);
            if (checkCollisionWithMonsters()) {
                resetBoard(); // אם קוראת התנגשות מתבצעת איפוס למשחק
            }
            repaint();
        }
        );
        monsterTime.start();


    }

    private void runPacmen() {
        pacmanTimer = new Timer(200, e -> {
            switch (mDirection) {
                case UP -> movePacman(KeyEvent.VK_UP);
                case DOWN -> movePacman(KeyEvent.VK_DOWN);
                case LEFT -> movePacman(KeyEvent.VK_LEFT);
                case RIGHT -> movePacman(KeyEvent.VK_RIGHT);
            }
            repaint();
        });
        pacmanTimer.start();


    }


    private void initListeners() {
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                movePacman(e.getKeyCode()); // תזוזת פאקמן
                repaint(); // רענון המסך
            }
        });
    }

    private void loadImages() {
        foodPacMan = new ImageIcon("src/Scor/Food_Pacmen.png").getImage();
        monster1 = new ImageIcon("src/Monster/monster_pink.png").getImage();
        monster2 = new ImageIcon("src/Monster/monster_blue.png").getImage();
        monster3 = new ImageIcon("src/Monster/monster_red.png").getImage();
        monster4 = new ImageIcon("src/Monster/monster_yellow.png").getImage();
        ScoreRed = new ImageIcon("src/Scor/IMG-20241121-WA0003.jpg").getImage();

        mUpPacmanImage = new ImageIcon("src/Image-Pac_Man/Up_Open.png").getImage();
        mUpPacmanImage2 = new ImageIcon("src/Image-Pac_Man/Up_Close.png").getImage();
        mDownPacmanImage = new ImageIcon("src/Image-Pac_Man/DownOpen.png").getImage();
        mDownPacmanImage2 = new ImageIcon("src/Image-Pac_Man/Down_Close.png").getImage();
        mRightPacmanImage = new ImageIcon("src/Image-Pac_Man/Right_open.png").getImage();
        mRightPacmanImage2 = new ImageIcon("src/Image-Pac_Man/Right_close.png").getImage();
        mLeftPacmanImage = new ImageIcon("src/Image-Pac_Man/Left_Open.png").getImage();
        mLeftPacmanImage2 = new ImageIcon("src/Image-Pac_Man/Left_Clos.png").getImage();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paintMaze(g);

        drawMonster(g);

        drawScore(g);

        drawLife(g);

        drawPacMan(g);

        if (countLife == 0) {
            gameOver();

        }
    }

    private void gameOver() {
        if (monsterTime != null) monsterTime.stop();
        if (pacmanTimer != null) pacmanTimer.stop();

        countLife--;
        int choice = JOptionPane.showConfirmDialog(this, "Game Over! Your score is: " + scorForPrice,
                "Restart the game?", YES_NO_OPTION);
        if (choice == YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        countLife = 3;
        scorForPrice = 0;
        initMaze(true);
        resetBoard();
        monsterTime.start();
        pacmanTimer.start();
    }

    private void drawLife(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("", Font.BOLD, 18));
        g.drawString("Life: " + countLife, 500, 20); // ציור מספר החיים
    }


    private void drawPacMan(Graphics g) {
        Image pacmanImage;
        switch (mDirection) {
            case UP:
                pacmanImage = isMouthOpen ? mUpPacmanImage : mUpPacmanImage2;
//                todo: up sound
                break;
            case DOWN:
                pacmanImage = isMouthOpen ? mDownPacmanImage : mDownPacmanImage2;
                break;
            case RIGHT:
                pacmanImage = isMouthOpen ? mRightPacmanImage : mRightPacmanImage2;
                break;
            case LEFT:
                pacmanImage = isMouthOpen ? mLeftPacmanImage : mLeftPacmanImage2;
                break;
            case null:
                pacmanImage = mDownPacmanImage2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mDirection);
        }
        g.drawImage(pacmanImage, pacmanCol * TILE_SIZE, pacmanRow * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
        isMouthOpen = !isMouthOpen;
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("", Font.BOLD, 18));
        g.drawString("Score: " + scorForPrice, 10, 20); // ניקוד
    }

    private void drawMonster(Graphics g) {
        g.drawImage(monster1, monsteresLocation[0][1] * TILE_SIZE, monsteresLocation[0][0] * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
        g.drawImage(monster2, monsteresLocation[1][1] * TILE_SIZE, monsteresLocation[1][0] * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
        g.drawImage(monster3, monsteresLocation[2][1] * TILE_SIZE, monsteresLocation[2][0] * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
        g.drawImage(monster4, monsteresLocation[3][1] * TILE_SIZE, monsteresLocation[3][0] * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
    }

    private void paintMaze(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (maze[row][col] == 1) { // קיר
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);


                if (maze[row][col] == 2 && foodPacMan != null) {
                    g.drawImage(foodPacMan, col * TILE_SIZE + TILE_SIZE / 4, row * TILE_SIZE + TILE_SIZE / 4, TILE_SIZE / 2, TILE_SIZE / 2, null);
                }
                if (maze[row][col] == 4 && ScoreRed != null) {
                    g.drawImage(ScoreRed, col * TILE_SIZE + TILE_SIZE / 4, row * TILE_SIZE + TILE_SIZE / 4, TILE_SIZE / 2, TILE_SIZE / 2, null);

                }
            }
        }
    }

    /**
     * @param row המיקום בשורה שבה אני רוצה להיות
     * @param col המיקום בעמודה שבה אני רוצה להיות
     * @return מחזיר לי אם אני יכול לזוז לשם
     */
    private boolean isCanMove(int row, int col) {
        // שהפקמן בתוך בלוח
        return row >= 0 && row < ROWS && col >= 0 && col < COLUMNS && maze[row][col] != 1;
    }


    // פונקציה להזזת המפלצות
    public void moveMonster(int monster) {

        int monsterCol = monsteresLocation[monster][1];
        int monsterRow = monsteresLocation[monster][0];
        Direction monsterDirection = Direction.values()[monsteresLocation[monster][2]];
        int left = monsterCol - 1;
        int right = monsterCol + 1;
        int up = monsterRow - 1;
        int down = monsterRow + 1;
        Collections.shuffle(directions);

        switch (monsterDirection) {
            case LEFT: {
                for (int i = 0; i < directions.size(); i++) {
                    switch (directions.get(i)) {
                        case UP: {
                            if (up >= 0 && up < ROWS && maze[up][monsterCol] != 1 && monsterInPlace(up, monsterCol)) {
                                monsteresLocation[monster][2] = Direction.UP.ordinal();
                                monsteresLocation[monster][0]--;
                                return;
                            }
                        }
                        case LEFT:
                            if (maze[monsterRow][monsterCol] == 8) {
                                monsteresLocation[monster][2] = Direction.LEFT.ordinal();
                                monsteresLocation[monster][1] = 27;
                                return;
                            }
                            if (left >= 0 && left < COLUMNS && maze[monsterRow][left] != 1 && monsterInPlace(monsterRow, left)) {
                                monsteresLocation[monster][2] = Direction.LEFT.ordinal();
                                monsteresLocation[monster][1]--;
                                return;
                            }
                        case DOWN:
                            if (down >= 0 && down < ROWS && maze[down][monsterCol] != 1 && monsterInPlace(down, monsterCol)) {
                                monsteresLocation[monster][2] = Direction.DOWN.ordinal();
                                monsteresLocation[monster][0]++;
                                return;
                            }
                    }
                }
                //בדיקה לכיון נוסף במקרה שחסום מ-4 כיוונים
                if (right >= 0 && right < COLUMNS && maze[monsterRow][right] != 1 && monsterInPlace(monsterRow, right)) {
                    monsteresLocation[monster][1]++;
                    monsteresLocation[monster][2] = Direction.RIGHT.ordinal();
                }
            }
            case RIGHT: {
                for (int i = 0; i < directions.size(); i++) {
                    switch (directions.get(i)) {
                        case UP: {
                            if (up >= 0 && up < ROWS && maze[up][monsterCol] != 1 && monsterInPlace(up, monsterCol)) {
                                monsteresLocation[monster][2] = Direction.UP.ordinal();
                                monsteresLocation[monster][0]--;
                                return;
                            }
                        }
                        case RIGHT:
                            if (maze[monsterRow][monsterCol] == 9) {
                                monsteresLocation[monster][2] = Direction.RIGHT.ordinal();
                                monsteresLocation[monster][1] = 0;
                                return;
                            }
                            if (right >= 0 && right < COLUMNS && maze[monsterRow][right] != 1 && monsterInPlace(monsterRow, right)) {
                                monsteresLocation[monster][2] = Direction.RIGHT.ordinal();
                                monsteresLocation[monster][1]++;
                                return;
                            }
                        case DOWN:
                            if (down >= 0 && down < ROWS && maze[down][monsterCol] != 1 && monsterInPlace(down, monsterCol)) {
                                monsteresLocation[monster][2] = Direction.DOWN.ordinal();
                                monsteresLocation[monster][0]++;
                                return;
                            }
                    }
                }
                //בדיקה לכיון נוסף במקרה שחסום מ-4 כיוונים
                if (left >= 0 && left < COLUMNS && maze[monsterRow][left] != 1 && monsterInPlace(monsterRow, left)) {
                    monsteresLocation[monster][1]--;
                    monsteresLocation[monster][2] = Direction.LEFT.ordinal();
                }
            }
            case UP: {
                for (int i = 0; i < directions.size(); i++) {
                    switch (directions.get(i)) {
                        case RIGHT: {
                            if (right >= 0 && right < COLUMNS && maze[monsterRow][right] != 1 && monsterInPlace(monsterRow, right)) {
                                monsteresLocation[monster][2] = Direction.RIGHT.ordinal();
                                monsteresLocation[monster][1]++;
                                return;
                            }
                        }
                        case LEFT:
                            if (left >= 0 && left < COLUMNS && maze[monsterRow][left] != 1 && monsterInPlace(monsterRow, left)) {
                                monsteresLocation[monster][2] = Direction.LEFT.ordinal();
                                monsteresLocation[monster][1]--;
                                return;
                            }
                        case UP:
                            if (up >= 0 && up < ROWS && maze[up][monsterCol] != 1 && monsterInPlace(up, monsterCol)) {
                                monsteresLocation[monster][2] = Direction.UP.ordinal();
                                monsteresLocation[monster][0]--;
                                return;
                            }
                    }
                }
                //בדיקה לכיון נוסף במקרה שחסום מ-4 כיוונים
                if (down >= 0 && down < ROWS && maze[down][monsterCol] != 1 && monsterInPlace(down, monsterCol)) {
                    monsteresLocation[monster][0]++;
                    monsteresLocation[monster][2] = Direction.DOWN.ordinal();
                }
            }
            case DOWN: {
                for (int i = 0; i < directions.size(); i++) {
                    switch (directions.get(i)) {

                        case RIGHT: {
                            if (right >= 0 && right < COLUMNS && maze[monsterRow][right] != 1 && monsterInPlace(monsterRow, right)) {
                                monsteresLocation[monster][2] = Direction.RIGHT.ordinal();
                                monsteresLocation[monster][1]++;
                                return;
                            }
                        }
                        case LEFT:
                            if (left >= 0 && left < COLUMNS && maze[monsterRow][left] != 1 && monsterInPlace(monsterRow, left)) {
                                monsteresLocation[monster][2] = Direction.LEFT.ordinal();
                                monsteresLocation[monster][1]--;
                                return;
                            }
                        case DOWN:
                            if (down >= 0 && down < ROWS && maze[down][monsterCol] != 1 && monsterInPlace(down, monsterCol)) {
                                monsteresLocation[monster][2] = Direction.DOWN.ordinal();
                                monsteresLocation[monster][0]++;
                                return;
                            }
                    }

                }
                //בדיקה לכיון נוסף במקרה שחסום מ-4 כיוונים
                if (up >= 0 && up < ROWS && maze[up][monsterCol] != 1 && monsterInPlace(up, monsterCol)) {
                    monsteresLocation[monster][0]--;
                    monsteresLocation[monster][2] = Direction.UP.ordinal();
                }
            }
        }

    }


    private boolean monsterInPlace(int row, int col) {

        return (monsteresLocation[0][0] != row && monsteresLocation[0][1] != col)
                || monsteresLocation[1][0] == row && monsteresLocation[1][1] == col ||
                monsteresLocation[2][0] != row && monsteresLocation[2][1] != col
                || monsteresLocation[3][0] == row && monsteresLocation[3][1] == col;


    }

    private boolean checkCollisionWithMonsters() {

        // בדיקה אם פקמן נוגע במפלצת כלשהי
        if (collision(pacmanRow, monsteresLocation[0][0], pacmanCol, monsteresLocation[0][1]) ||
                collision(pacmanRow, monsteresLocation[1][0], pacmanCol, monsteresLocation[1][1]) ||
                collision(pacmanRow, monsteresLocation[2][0], pacmanCol, monsteresLocation[2][1]) ||
                collision(pacmanRow, monsteresLocation[3][0], pacmanCol, monsteresLocation[3][1])) {

            countLife--;
//            System.out.println("התנגשות זוהתה! חיים נותרו: " + countLife);
//            System.out.println(" פסילה ");
            return true;

        }
        return false;

    }


    public boolean collision(int pacmanRow, int monsterRow, int pacmanCol, int monsterCol) {
        int x = Math.abs(pacmanRow - monsterRow);
        int y = Math.abs(pacmanCol - monsterCol);
//        System.out.println("x:" + x + "  " + " y:" + y);
        return x < 1 && y < 1;
    }

    // פונקציה להזזת Pac-Man
    public void movePacman(int keyCode) {
        int newRow = pacmanRow;
        int newCol = pacmanCol;

        // כיון תנועת הלחצנים במקלדת לפי מיקום האינדקס של (i ו j בלולאה )
        switch (keyCode) {
            case KeyEvent.VK_UP:
                newRow--;
                mDirection = Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
                newRow++;
                mDirection = Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
                newCol--;
                mDirection = Direction.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                newCol++;
                mDirection = Direction.RIGHT;
                break;
        }
        if (checkCollisionWithMonsters()) {

            resetBoard();
            System.out.println("פסילה לוגית 1");
            // Countlife--; יכול לפסול פעמיים

        } else {

            if (newCol < 0) newCol = COLUMNS - 1;
            else if (newCol >= COLUMNS) newCol = 0;

            if (isCanMove(newRow, newCol)) {
                pacmanRow = newRow;
                pacmanCol = newCol;
            }
            if (maze[pacmanRow][pacmanCol] == 2) {
                maze[pacmanRow][pacmanCol] = 0;
                scorForPrice += 10;
            }
            if (maze[pacmanRow][pacmanCol] == 4) {
                maze[pacmanRow][pacmanCol] = 0;
                scorForPrice += 50;

            }
        }
        isFoodFinish();
    }

    private void isFoodFinish() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length - 1; j++) {
                if (maze[i][j] == 2 || maze[i][j] == 4)
                    return;
            }
        }
        repaint();

        if (monsterTime != null) monsterTime.stop();
        if (pacmanTimer != null) pacmanTimer.stop();

        int choice = JOptionPane.showConfirmDialog(this, "Your score is: " + scorForPrice + "\n" + "You want to continue?",
                "FINISH", YES_NO_OPTION);
        if (choice == YES_OPTION) {
            restartGame();

        } else {
            System.exit(0);
        }

    }

    private void initMaze(boolean reset) {
        if (maze == null || reset) maze = new int[][]{        // Block = 1,Coins = 2,BigCoins = 3;
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 4, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 4, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1},
                {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1},
                {8, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 9},
                {1, 1, 1, 1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 1},
                {1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1},
                {1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1},
                {1, 4, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 4, 1},
                {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
                {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},};


    }
}


