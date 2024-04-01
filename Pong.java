import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.awt.event.KeyEvent;

public class Pong extends JPanel implements KeyListener{
  private Paddle right;
  private Paddle left;
  private Functions functions;
  private Ball gameBall;
  private boolean statusMovePaddle;
  private char keyPressedLetter;
  public int WIDTH = 1024;
  public int HEIGHT = 768;
  public int paddleStep;
  private boolean gameState;

   
//creating a main thread that does graphic calculations 

  public class myRunnable implements Runnable{
    private int boundBoxChangeC = 0;
    private boolean tf;
    //creating a constructor to input a boolean which allows for wrap around or bounce aspect of the game 
    public myRunnable(boolean tell){
      this.tf = tell;
    }

    public void run(){
      while(true){
        
            left.movePaddle();
            right.movePaddle();
            gameBall.moveBall();

            //inhibits the paddles from moving out of screen
            if(left.getYPos() < 1){
              if(left.getYPos() == 0 && keyPressedLetter == 'r'){
                left.changeStep(0);
              }
              left.changeStep(0);
            }

            if(right.getYPos() < 1){
              if(right.getYPos() == 0 && keyPressedLetter == 'u'){
                right.changeStep(0);
              }
              right.changeStep(0);
            }
              
            if(left.getYPos() > 768 - left.getHeight()){
              if(55 == (768 - left.getHeight()) + 1){
                left.changeStep(0);
              }
              left.changeStep(0);
            }
              
            if(right.getYPos() > 768 - right.getHeight()){
              if(55 == (768 - right.getHeight())){
                right.changeStep(0);
              }
              right.changeStep(0);
            }
            
            //checking if the ball goes out of the sides and if the does, restarting the x position 
            //to the middle and then getting rid of y velocity and resettting paddle size to default 
           if(gameBall.getX() > WIDTH || gameBall.getX() < 0){
            if(gameBall.getX() > WIDTH){
              functions.updateRightScore();
              gameBall.changeStepX(1);
            }
            else {
              functions.updateLeftScore();
              gameBall.changeStepX(-1);
            }

            gameBall.changeBallColor(new Color(255,255,255));
            gameBall.changeStepY(0);
            gameBall.resetPosition();
            left.resetHeight();
            right.resetHeight();
           }
              
            
            //checking if the ball hits the top or bottom of screen
            //depending on if it's one or two players, the ball 
            //either bounces off the top/bottom boundaries or wraps around 
            if(gameBall.getY() > HEIGHT){
              if(this.tf){
                gameBall.changeStepY(3);
                gameBall.setYPos(0);
              }
              if(!this.tf){
                gameBall.changeStepY(-3);
              }
              
            }

            if(gameBall.getY() < 0){
              if(this.tf){
                gameBall.changeStepY(-3);
                gameBall.setYPos(HEIGHT);
              }

              if(!this.tf){
                gameBall.changeStepY(3);
              }
             
            }
            //
            functions.hitBox(0);
            boundBoxChangeC++;
        repaint();
        try{
          Thread.sleep(4);
        }
      catch(InterruptedException e){}
    }
  }
}

//creating a second thread for bot calculations
//separte from graphic loading because graphic and bot calculations need to run at the same time 
public class myRunnableTwo implements Runnable{
  int difficulty;
  int speedStep; 
  public myRunnableTwo(int diff){
    this.difficulty = diff;
    //setting the difficulty level 
    if(diff == 1){
      this.speedStep = 1;
     }

     else if(diff == 2){
      this.speedStep = 2;
     }

     else{
      this.speedStep = 5;
     }

  }
  //code for bot 
  public void run(){
    while(true){
      System.out.print("");
    if(gameBall.getY() - left.getYPos() < 55){
      left.changeStep(-this.speedStep);
    }
    if(gameBall.getY() - left.getYPos() > 55){
      left.changeStep(this.speedStep);
    }
    }
  }
}

//depending on the key input, paddle velocity changes 
  @Override
  public void keyTyped(KeyEvent e) {
    char c = e.getKeyChar();
    keyPressedLetter = c;
    this.keyPressedLetter = c;

    if(c == 'r'){
      left.changeStep(-2);
      left.getYPos();
    }

    else if(c == 'u'){
      right.changeStep(-2);
      if(right.getYPos() == 0){
        right.changeStep(0);
      }
      right.getYPos();
    }

    else if(c == 'v'){
      left.changeStep(2);
      left.getYPos();

    }

    else if(c =='n'){
      right.changeStep(2);
      right.getYPos();

    }

    else if(c == 'f'){
      left.changeStep(0);
      left.getYPos();

    }
    else if(c == 'j'){
      right.changeStep(0);
      right.getYPos();
    }
  }


  @Override
  public void keyPressed(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  public Pong(){
    //youtube was used here to gain insight on 
    //what jframe,thread,keylistener does 
    //create paddle, ball, functions object, one/two player option and dimensions of game 
    Scanner myObj = new Scanner(System.in);
    System.out.println("One or Two Players? (A: 1 or 2): ");
    int player = myObj.nextInt();
    this.paddleStep = -2;
    this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
    addKeyListener(this);
    right = new Paddle(1);
    left = new Paddle(2);
    gameBall = new Ball();
    functions = new Functions(right, left, gameBall);

    //game mechanics when only one player is present 
    //creating level setting and chekcing for invalid inputs then starting up the threads 
    if(1 == player){
      Scanner secObj = new Scanner(System.in);
      System.out.println("Choose level (A: EASY = 1, MED = 2, HARD = 3): ");
      int level = secObj.nextInt();
      if(!(level == 1 || level == 2 || level ==3)){
        System.err.println("Error: Not a valid input");
        System.exit(1);
      }
      Thread mainThread = new Thread(new myRunnable(false));
      Thread secondThread = new Thread(new myRunnableTwo(level));
      mainThread.start();
      secondThread.start();
    }
    //game mechanics for two players, only starting up one thread because 
    //bot calculations is not needed
    else if(2 == player){
      Thread mainThread = new Thread(new myRunnable(true));
      mainThread.start();
    }
    //throwing error statement when more than 2 players have been inputted 
    else{
      System.err.print("Invalid: Player number must be 1 or 2");
      System.exit(1);
    }
  }
  public static void main(String[] args){
    JFrame frame = new JFrame("PONG!");
    System.out.println("\033[031mMessage: There is a slight difference in one or two player version!");
    System.out.print("\033[037m");
    Pong game = new Pong();
    frame.setContentPane(game);
    frame.pack();
    frame.setVisible(true);
    //stackoverflow was used here to know that I needed
    //requestFocus method for key input to work 
    game.requestFocus();
    
  }

  //drawing everything that is needed(paddle, ball, scoreboard)
  public void paintComponent(Graphics g) {
    g.setColor(Color.BLACK);
    g.fillRect(0,0,1100,800);
    right.drawPaddle(g);
    left.drawPaddle(g);
    gameBall.drawBall(g);
    g.setColor(Color.WHITE);
    functions.drawScoreBoard(g);

    g.setColor(Color.WHITE);
   



    int height = 10;
    for(int i = 0; i < 10; i++){
      g.fillRect(WIDTH/2, height, 5,35);
      height+=HEIGHT/10;
    }
    } 

  }


  //creating a class for the paddles in game 
   class Paddle{

      private int yPos;
      private int xPos;
      private int side;
      private int step;
      private int height;
      private int ogHeight;
      private int width;
      private Pong mg;

      public Paddle(int position){
        this.side = position;
        this.step = 0;
        this.height = 125;
        this.width = 10;
        this.yPos = (768 / 2) - (this.height / 2);
        this.ogHeight = height;
        this.xPos = 0;
        if(this.side % 2 == 1){
          this.xPos = 1024 - this.width;
        }
        
     }
    //creating a separate class for paddles to make it easier when putting 
    //it in the paintComponent method 
    public void drawPaddle(Graphics g){

      g.setColor(Color.WHITE);
      if(this.side % 2 == 1){
        g.fillRect(1024-this.width, this.yPos,this.width,this.height);
        
      }
      else{
        g.fillRect(0,this.yPos, this.width, this.height);
      }
    }

    //changes the "speed" and direction of the paddles 
    public void changeStep(int s){
      this.step = s;
    }

    //moves the paddle by adding to the y-direction 
    public void movePaddle(){
      this.yPos+=(int)this.step;
      
    }

    //changing the height. height changes as the rally goes on for longer
    public void changeHeight(){
      this.height-=2;
    }
    //getting y position 
    public int getYPos(){
      return this.yPos;
    }
    //getting x position
    public int getXPos(){
      return this.xPos;
    }
    //getting height 
    public int getHeight(){
      return this.height;
    }
    //reseting height to default values 
    public void resetHeight(){
      this.height = this.ogHeight;
    }

  }

  //creating the game ball class
   class Ball{
    private int x;
    private int ogX;
    private int ogY;
    private int y;
    private int r;
    private int stepY;
    private int stepX;
    private double angle;
    private Color ballColor;

    public Ball(){
      this.r = 15;
      this.x = (1028 / 2) - (this.r / 2);
      this.ogX = this.x;
      this.y = (768 / 2) -(this.r / 2);
      this.ogY = this.y;
      this.stepY = 0;
      this.stepX = 1;
      ballColor = new Color(255,255,255);
    }

    //creating a drawball method to draw the ball
    public void drawBall(Graphics g){
      g.setColor(this.ballColor);
      g.fillRect(this.x,this.y,this.r, this.r);
    }
    //creating a method to update the ball 
    public void moveBall(){
      this.x+=this.stepX; 
      this.y+=this.stepY;
      this.angle += 1;
      //this.angle %= 360;
    }
    //changing x and y step length 
    public void changeStepX(int s){
      this.stepX = s;
    }
    public void changeStepY(int s){
      this.stepY = s;
    }
    //resetting ball's position to the default 
    public void resetPosition(){
      this.x = this.ogX;
      this.y = this.ogY;
    }
    //getting x and y positions and width
    public int getX(){
      return this.x;
    }
    public int getY(){
      return this.y;
    }
    public int getWidth(){
      return this.r;
    }
    //changing ball color and overloading 
    public void changeBallColor(){
      this.ballColor = new Color((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256));
    }

    public void changeBallColor(Color qwer){
      this.ballColor = qwer;
    }

    public void setYPos(int yPostion){
      this.y = yPostion;
    }

   }

   //creating functions class that does game mechanics behind the scene 
  class Functions{
    private Paddle r;
    private Paddle l;
    private Ball s;
    public int leftPlayerScore;
    public int rightPlayerScore;
    private String abcde;

    public Functions(){

      this.leftPlayerScore = 0;
      this.rightPlayerScore = 0;
      this.abcde = "S";
    }
    public Functions(Paddle rp, Paddle lp, Ball skeet){

      this.r = rp;
      this.l = lp;
      this.s = skeet;
      this.leftPlayerScore = 0;
      this.rightPlayerScore = 0;
      this.abcde = "S";

    }

    //gets and updates the player's scores 
    public int getLeftPlayerScore(){
      return this.leftPlayerScore;
    }

    public int getRightPlayerScore(){
      return this.rightPlayerScore;
    }

    public void updateLeftScore(){
      this.leftPlayerScore++;
    }
    public void updateRightScore(){
      this.rightPlayerScore++;
    }

    //draws scoreboard 
    public void drawScoreBoard(Graphics g){
      String scoreR = this.rightPlayerScore+"";
      String scoreL = this.leftPlayerScore+"";
      g.setFont(new Font("Serif", Font.BOLD, 100));
      g.drawString(scoreR, 1028/4, 80);
      g.drawString(scoreL, (1028/2) + (1028/4), 80);
    }

 

    //the hitbox that calulates the ball's angles depending on where the ball is hit 
    //maybe could use where the ball hits from the center to find the ball's velocity..? 
    public void hitBox(int boundBoxChange){
      int hitBoxRightY = Math.abs( this.s.getY() - this.r.getYPos());
      int hitBoxLeftY = Math.abs (this.s.getY() - this.l.getYPos());
      if(Math.abs(this.s.getX() - this.r.getXPos()) <= 10){
        if(hitBoxRightY > 5 + boundBoxChange && hitBoxRightY <= 10 + boundBoxChange){
          this.s.changeStepY(-4);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxRightY >  + boundBoxChange && hitBoxRightY <= 25 + boundBoxChange){
          this.s.changeStepY(-3);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxRightY > 25 + boundBoxChange && hitBoxRightY <= 45 + boundBoxChange){
          this.s.changeStepY(-2);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxRightY > 45 + boundBoxChange && hitBoxRightY <= 50 + boundBoxChange){
          this.s.changeStepY(0);
          this.s.changeStepX(-3);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxRightY > 50 - boundBoxChange && hitBoxRightY <= 84 - boundBoxChange){
          this.s.changeStepY(2);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxRightY > 84 - boundBoxChange && hitBoxRightY <= 99 - boundBoxChange){
          this.s.changeStepY(3);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxRightY > 99 - boundBoxChange && hitBoxRightY <= 120 - boundBoxChange){
          this.s.changeStepY(4);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }
      }

      if(Math.abs(this.s.getX() - this.l.getXPos()) <= 10){
        if(hitBoxLeftY > 5 + boundBoxChange && hitBoxLeftY <= 10 + boundBoxChange){
          this.s.changeStepY(-4);
          this.s.changeStepX(-2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }

        if(hitBoxLeftY > 10 + boundBoxChange && hitBoxLeftY <= 25 + boundBoxChange){
          this.s.changeStepY(-3);
          this.s.changeStepX(2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }

        if(hitBoxLeftY > 25 + boundBoxChange && hitBoxLeftY <= 45 + boundBoxChange){
          this.s.changeStepY(-2);
          this.s.changeStepX(2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }

        if(hitBoxLeftY > 45 + boundBoxChange && hitBoxLeftY <= 50 + boundBoxChange){
          this.s.changeStepY(0);
          this.s.changeStepX(3);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }

        if(hitBoxLeftY > 50 - boundBoxChange && hitBoxLeftY <= 84 - boundBoxChange){
          this.s.changeStepY(2);
          this.s.changeStepX(2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }
        if(hitBoxLeftY > 84 - boundBoxChange && hitBoxLeftY <= 99 - boundBoxChange){
          this.s.changeStepY(3);
          this.s.changeStepX(2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;
        }
        if(hitBoxLeftY > 99 - boundBoxChange && hitBoxLeftY <= 120 - boundBoxChange){
          this.s.changeStepY(4);
          this.s.changeStepX(2);
          r.changeHeight();
          l.changeHeight();
          this.s.changeBallColor();
          boundBoxChange-=2;        }
      }
    }

  }

  
