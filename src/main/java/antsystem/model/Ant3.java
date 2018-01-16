/*
 * Ant3.java
 *
 * Created on 19 de Janeiro de 2007, 17:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author danilo
 */
public class Ant3 extends Actor{
    private static int RIGHT_ANTENNA = 0;
    private static int LEFT_ANTENNA = 1;
    private static int BODY = 2;
    
    //Constants to say which type of actor was hit
    private static int FOOD = 1000;
    private static int NEST = 1001;
    private int actorTypeHit;
    private boolean [] actorSensors = {false,false,false};
    private double actorDistance;
    FoodSource foundFS;
    //Can be a FoodSource or the NEST
    Actor actorWasHit;
    
    private boolean [] sensors = {false,false,false};
    private int lastSensorHit = -1;
    private boolean carryingFood;
    private int vx, vy, velocity;
    private Direction direction= new Direction();
    private Color normalColor = new Color(255,255,0,100);
    private Color carryingFoodColor;
    private Point oldPosition;
    
    private PheromoneMatrix pheromoneMatrix;
    private PheromoneSniffer sniffer;
    private Shape rightAntenna, leftAntenna2;
    private Shape body;
    
    //array proportionally filled with turns probabilities
    /**
     * Array that will be filled proportionally to the chance that the ant has to turn
     * in each of the directions. It will be affected by the pheromone near and the
     * pheromoneWeight
     */
    private int[] turnProbability = new int[100];
    
    //Probabilities without Pheromone
    //The sum of all probability must be 1
    private int noTurnProbability = 92;
    private int clockwise_45_Probability = 3;
    private int clockwise_90_Probability = 1;
    private int clockwise_135_Probability = 0;
    private int clockwise_180_Probability = 0;
    private int counterclockwise_45_Probability = 3;
    private int counterclockwise_90_Probability = 1;
    private int counterclockwise_135_Probability = 0;
    
    //percentages of pheromone concentration near the ant
    private int inFrontPheromoneConcentration;
    private int in45RightPheromoneConcentration;
    private int in45LeftPheromoneConcentration;
    private int in90RightPheromoneConcentration;
    private int in90LeftPheromoneConcentration;
    private int inBackPheromoneConcentration;
    private int in135RightPheromoneConcentration;
    private int in135LeftPheromoneConcentration;
    
    
    /**
     * Weight that express how much the ant will be driven by the pheromone
     */
    private int pheromoneWeight = 8;
    private int carryingFoodPheromoneWeight=8;
    /**
     * The start value of the sum of pheromone surrounding the ant that makes it
     * to influence the turns probabilities
     */
    private int pheromoneThreshold=15;
    private int carryingFoodPheromoneThreshold=15;
    
    /**
     * The amount of pheromone around.
     * Around here means the sum of the pheromone sniffed in all directions
     * the ant are currently sniffing
     */
    private int pheromoneLevelAround;
    
    private int pheromoneDepositAmount;
    private int carryingFoodPheromoneDepositAmount;
    
    /** Creates a new instance of Ant2 */
    public Ant3() {
        this.setDimension(new Dimension(15,15));
        
        createAnt();
        adjustProbabiities();
        initRadomDirection();
    }
    
    public Ant3(PheromoneMatrix pheromoneMatrix, int velocity){
        this();
        setPheromoneMatrix(pheromoneMatrix);
        setVelocity(velocity);
    }
    
    private void createAnt(){
        double cx = getCenter().getX();
        double cy = getCenter().getY();
        double w = getDimension().getWidth();
        double h = getDimension().getHeight();
        rightAntenna = new Rectangle2D.Double(cx-1,-h/2,2,h/2);
        leftAntenna2 = new Rectangle2D.Double(cx+1,-h/2,2,h/2);
        rightAntenna = AffineTransform.getRotateInstance(Math.toRadians(-30),cx,cy).createTransformedShape(rightAntenna);
        leftAntenna2 = AffineTransform.getRotateInstance(Math.toRadians(30),cx,cy).createTransformedShape(leftAntenna2);
        body = new Ellipse2D.Float(0,0,getDimension().width, getDimension().height);
        setCarryingFood(false);
        
    }
    
    private void adjustProbabiities(){
        boolean considerPheromone = (getPheromoneLevelAround()>getPheromoneThreshold());
        int arrayIdx = 0;
        int endIdx = 0;
        
        int pw = (isCarryingFood())?getCarryingFoodPheromoneWeight():getPheromoneWeight();
        
        //propabillity of no turn
        int total =
                considerPheromone?(getNoTurnProbability() + pw*getInFrontPheromoneConcentration())/(pw+1):
                    getNoTurnProbability();
        
        endIdx = total-1 + arrayIdx;
        Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.NO_TURN);
        arrayIdx = endIdx+1;
        
        //probalbility of turning clockwise 45 degrees
        total = considerPheromone?
            (getClockwise_45_Probability() + pw*getIn45RightPheromoneConcentration())/(pw+1):
            getClockwise_45_Probability();
        
        if (total>0){
            endIdx = total-1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.CLOCKWISE_45);
            arrayIdx = endIdx+1;
        }
        
        //probabillity of turning clockwise 90 degrees
        total = considerPheromone?
            (getClockwise_90_Probability() + pw*getIn90RightPheromoneConcentration())/(pw+1):
            getClockwise_90_Probability();
        if (total>0){
            endIdx = total -1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.CLOCKWISE_90);
            arrayIdx = endIdx+1;
        }
        
        //probabillity of turning clockwise 135 degrees
        total = considerPheromone?
            (getClockwise_135_Probability() + pw*getIn135RightPheromoneConcentration())/(pw+1):
            getClockwise_135_Probability();
        if(total > 0){
            endIdx = total-1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.CLOCKWISE_135);
            arrayIdx = endIdx+1;
        }
        
        //probabillity of turning clockwise 180 degrees
        total = considerPheromone?
            (getClockwise_180_Probability() + pw*getInBackPheromoneConcentration())/(pw+1):
            getClockwise_180_Probability();
        if (total>0){
            endIdx = total-1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.CLOCKWISE_180);
            arrayIdx = endIdx+1;
        }
        
        //probabillity of turning counter clockwise 45 degrees
        total = considerPheromone?
            (getCounterclockwise_45_Probability() + pw*getIn45LeftPheromoneConcentration())/(pw+1):
            getCounterclockwise_45_Probability();
        if (total>0){
            endIdx = total-1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.COUNTERCLOCKWISE_45);
            arrayIdx = endIdx+1;
        }
        
        //probabillity of turning counter clockwise 90 degrees
        total = considerPheromone?
            (getCounterclockwise_90_Probability() + pw*getIn90LeftPheromoneConcentration())/(pw+1):
            getCounterclockwise_90_Probability();
        if (total>0){
            endIdx = total-1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.COUNTERCLOCKWISE_90);
            arrayIdx = endIdx+1;
        }
        
        //probabillity of turning counter clockwise 135 degrees
        total = considerPheromone?
            (getCounterclockwise_135_Probability() + pw*getIn135LeftPheromoneConcentration())/(pw+1):
            getCounterclockwise_135_Probability();
        if (total>0){
            endIdx = total-1 + arrayIdx;
            if(endIdx>=arrayIdx)
                Arrays.fill(turnProbability,arrayIdx,endIdx,Direction.COUNTERCLOCKWISE_135);
            arrayIdx = endIdx+1;
        }
        //System.out.println(Arrays.toString(turnProbability));
        
    }
    
    private void actualPheromoneLevels(){
        float f = sniffer.sniff(getCenter(),direction,PheromoneSniffer.FRONT);
        float r45 = sniffer.sniff(getCenter(),direction,PheromoneSniffer.RIGHT_45);
        float l45 = sniffer.sniff(getCenter(),direction, PheromoneSniffer.LEFT_45);
        
        float total = f+r45+l45;
        setPheromoneLevelAround((int)total);
        //percentages
        //int pN = Math.round((n/total)*100);
        setInFrontPheromoneConcentration(Math.round((f/total)*100));
        setIn45RightPheromoneConcentration(Math.round((r45/total)*100));
        setIn45LeftPheromoneConcentration(Math.round((l45/total)*100));
        
    }
    /**
     * Verifies whether this Ant is carrying food or not. 
     * @return <code>True</code> if it is carrying food.
     */
    public boolean isCarryingFood() {
        return carryingFood;
    }
    
    /**
     * Sets the state of carrying food. 
     * @param carryingFood 
     */
    public void setCarryingFood(boolean carryingFood) {
        this.carryingFood = carryingFood;
    }
    
    /**
     * Chooses a initial direction randomly.
     */
    public void initRadomDirection(){
        Random rdm = new Random();
        int d = 1 + rdm.nextInt(8) ;
        //int d =1;
        switch(d){
            case 1: setDirection(Direction.NORTH);
            break;
            case 2: setDirection(Direction.NORTHEAST);
            transform(AffineTransform.getRotateInstance(Math.toRadians(45),
                    getCenter().getX(), getCenter().getY()));
            break;
            case 3: setDirection(Direction.EAST);
            transform(AffineTransform.getRotateInstance(Math.toRadians(90),
                    getCenter().getX(), getCenter().getY()));
            break;
            case 4: setDirection(Direction.SOUTHEAST);
            transform(AffineTransform.getRotateInstance(Math.toRadians(135),
                    getCenter().getX(), getCenter().getY()));
            break;
            case 5: setDirection(Direction.SOUTH);
            transform(AffineTransform.getRotateInstance(Math.toRadians(180),
                    getCenter().getX(), getCenter().getY()));
            break;
            case 6: setDirection(Direction.SOUTHWEST);
            transform(AffineTransform.getRotateInstance(Math.toRadians(225),
                    getCenter().getX(), getCenter().getY()));
            break;
            case 7: setDirection(Direction.WEST);
            transform(AffineTransform.getRotateInstance(Math.toRadians(270),
                    getCenter().getX(), getCenter().getY()));
            break;
            case 8: setDirection(Direction.NORTHWEST);
            transform(AffineTransform.getRotateInstance(Math.toRadians(315),
                    getCenter().getX(), getCenter().getY()));
            break;
        }
        // adjustVelocity();
    }
    
    /**
     * Makes a turn randomly based on the respective probabilities set in <code>turnProbability</code> array.
     * @return The correct <code> AffineTransform </code> to be used in the graphical environment to perform the correspondent turn.
     */
    protected AffineTransform randomlyTurn(){
        AffineTransform af = null;
        Random rdm = new Random();
        int randomNumber = rdm.nextInt(100) ;
        //probabilidade Hard codded!! Preciso deixar isso gen�rico!
        
        af = direction.turn(turnProbability[randomNumber], getCenter());
//        if (randomNumber < 3 ){
//            af = direction.turn(Direction.CLOCKWISE_45, getCenter());
//        }else if(randomNumber <4){
//            //2%
//            af = direction.turn(Direction.CLOCKWISE_90, getCenter());
//
//        }else if(randomNumber <7){
//            //5%
//            af = direction.turn(Direction.COUNTERCLOCKWISE_45, getCenter());
//
//        }else if(randomNumber < 8){
//            //2%
//            af = direction.turn(Direction.COUNTERCLOCKWISE_90, getCenter());
//
//        }
        
        return af;
    }
    
    private AffineTransform justFollowTheHiestPheromone(){
        return null;
    }
    
    
    boolean hasHit = false;
    int stepsWithoutColision = 0;
    @Override public void act() {
        if (!hasHit){
            if(actorTypeHit==FOOD||actorTypeHit==NEST){
                actorFound();
                //return;
            }
            depositPheromone();
            //randomlyTurn();
            actualPheromoneLevels();
            adjustProbabiities();
            transform(randomlyTurn());
            //oldPosition = getPosition();
            stepForward(1);
            ++stepsWithoutColision;
            //limpando o ultimo sensor colidido para evitar o comportamento de beco sem necessidade
            if(stepsWithoutColision>2){
                lastSensorHit = -1;
            }
            
        }else{
            
            
            
            //Verifico se ao colidir uma antena, se a outra estava colidida na itera�ao anteirior,
            //case afirmativo, a formiga pode estar num "beco", e deve virar 180 graus e sair ..
            //contudo devo limpar o lastSensorHit depois de um passo ou dois sem ter se colidido
            // para nao causar um comportamento de "beco" quando nao se est� no beco.
            stepsWithoutColision=0;
            stepBack(1);
            if((sensors[LEFT_ANTENNA] && sensors[RIGHT_ANTENNA])|| sensors[BODY]) {
                
                double chance = Math.random();
                if(chance<0.5d)
                    transform(direction.turn(Direction.CLOCKWISE_90,getCenter()));
                else
                    transform(direction.turn(Direction.COUNTERCLOCKWISE_90,getCenter()));
                
                lastSensorHit=LEFT_ANTENNA;
            }else if(sensors[LEFT_ANTENNA]){
                if(lastSensorHit==RIGHT_ANTENNA)
                    transform(direction.turn(Direction.CLOCKWISE_180,getCenter()));
                else
                    transform(direction.turn(Direction.CLOCKWISE_45,getCenter()));
                
                lastSensorHit = LEFT_ANTENNA;
            }else if(sensors[RIGHT_ANTENNA]){
                if(lastSensorHit==LEFT_ANTENNA)
                    transform(direction.turn(Direction.CLOCKWISE_180,getCenter()));
                else
                    transform(direction.turn(Direction.COUNTERCLOCKWISE_45,getCenter()));
                lastSensorHit = RIGHT_ANTENNA;
            }
            //setPosition(new Point(getPosition().x+vx, getPosition().y+vy));
            resetSensors();
            hasHit = false;
        }
    }
    
    private void actorFound(){
        if(actorSensors[LEFT_ANTENNA]&&!actorSensors[RIGHT_ANTENNA]){
            transform(direction.turn(Direction.CLOCKWISE_45, getCenter()));
            
            depositPheromone();
            stepForward(2);
        }else if(!actorSensors[LEFT_ANTENNA]&&actorSensors[RIGHT_ANTENNA]){
            transform(direction.turn(Direction.COUNTERCLOCKWISE_45, getCenter()));
            
            depositPheromone(50);
            stepForward(2);
        } else if(actorSensors[LEFT_ANTENNA]&&actorSensors[RIGHT_ANTENNA]){
            
            depositPheromone(100);
            chooseTurnToGetCloserToTheCenter();
        } else if(!actorSensors[LEFT_ANTENNA]&&!actorSensors[RIGHT_ANTENNA]){
            if (getActorDistance()<15){
                //Nao est� mais indo ao centro da comida
                if(actorTypeHit==NEST){
                    foundNest();
                }else if(actorTypeHit==FOOD){
                    foundFood();
                }
                
                actorTypeHit=0;
                
                
               
                
                stepForward(1);
            }else
               chooseTurnToGetCloserToTheCenter();
        }
        setActorDistance(getCenter().distance(actorWasHit.getCenter()));
        resetActorSensors();
    }
    
    private void foundFood(){
        ((FoodSource)actorWasHit).withDraw(5);
        setCarryingFood(true);
        depositPheromone();
        transform(direction.turn(Direction.CLOCKWISE_180, getCenter()));
    }
    private void foundNest(){
        if(isCarryingFood()){
            setCarryingFood(false);
            transform(direction.turn(Direction.CLOCKWISE_180,getCenter()));
            depositPheromone();
            ((Nest)actorWasHit).depositCollectedFood(5);
        }
    }
    
    private void chooseTurnToGetCloserToTheCenter(){
        int choice =0;
        double min;
        double dist1 = 0;
        
        //sem virar
        stepForward(3);
        dist1 = getCenter().distance(actorWasHit.getCenter());
        //voltando ao original
        stepBack(2);
        choice =1;
        min = dist1;
        
        double dist2=0;
        transform(direction.turn(Direction.CLOCKWISE_45, getCenter()));
        stepForward(3);
        dist2 = getCenter().distance(actorWasHit.getCenter());
        //voltando ao original
        stepBack(3);
        transform(direction.turn(Direction.COUNTERCLOCKWISE_45, getCenter()));
        
        if(dist2<min){
            choice =2;
            min = dist2;
        }
        
        double dist3=0;
        transform(direction.turn(Direction.COUNTERCLOCKWISE_45, getCenter()));
        stepForward(3);
        dist3 = getCenter().distance(actorWasHit.getCenter());
        //voltando ao original
        stepBack(3);
        transform(direction.turn(Direction.CLOCKWISE_45, getCenter()));
        
        if(dist3<min){
            choice = 3;
            min =dist3;
        }
        //verificando a melhor das 3 op�oes
        switch(choice){
            case 1: 
            break;
            case 2:
                transform(direction.turn(Direction.CLOCKWISE_45, getCenter()));
                
                break;
            case 3:
                transform(direction.turn(Direction.COUNTERCLOCKWISE_45, getCenter()));
                
                break;
        }
        stepForward(3);
        depositPheromone();
        actorDistance = min;
        
        
    }
    private void stepForward(int numOfSteps) {
        setPosition(new Point(getPosition().x+numOfSteps*vx,
                getPosition().y+numOfSteps*vy));
    }
    
    private void stepBack(int numOfSteps){
        stepForward(-numOfSteps);
    }
    private void resetSensors() {
        
        sensors[LEFT_ANTENNA] = false;
        sensors[RIGHT_ANTENNA] = false;
        sensors[BODY] = false;
    }
    private void resetActorSensors(){
        actorSensors[LEFT_ANTENNA] = false;
        actorSensors[RIGHT_ANTENNA] = false;
        actorSensors[BODY] = false;
    }
    
    private void transform(AffineTransform af){
        if (af == null)
            return;
        body = af.createTransformedShape(body);
        rightAntenna =  af.createTransformedShape(rightAntenna);
        leftAntenna2 =  af.createTransformedShape(leftAntenna2);
        adjustVelocity();
    }
    
    
    
    @Override public void paint(Graphics2D g2d) {
        
        Color oldColor = g2d.getColor();
        
        if(isCarryingFood())
            g2d.setPaint(getCarryingFoodColor());
        else
            g2d.setPaint(getNormalColor());
        
        
        g2d.fill(body);
        g2d.setColor(Color.BLACK);
        g2d.draw(body);
        g2d.fill(rightAntenna);
        g2d.fill(leftAntenna2);
        
        g2d.setColor(Color.RED);
        
        //uncoment the 2 lines below to see the sensor areas around the antennas
        //g2d.draw(rightAntenna.getBounds());
        //g2d.draw(leftAntenna2.getBounds());
        
        //Uncoment this line below to see the Phermone level araound the ant    
        //g2d.drawString(""+getPheromoneLevelAround(),getCenter().x, getCenter().y);
        double y = getPosition().getY();
        double w = getDimension().getWidth() / 2.0d;
        double x = getCenter().getX() - w/2.0d;
        double h = getDimension().getHeight() / 4.0d;
        
        
        //g2d.draw(new Rectangle2D.Double(x,y,w,h));
        g2d.setPaint(oldColor);
        
    }
    
    private void depositPheromone(){
        int x = getCenter().x;
        int y= getCenter().y;
        //int value = pheromoneMatrix.getValue(x,y) + 1;
        //pheromoneMatrix.setValue(x,y,value);
        int depositValue=(isCarryingFood())?getCarryingFoodPheromoneDepositAmount():
            getPheromoneDepositAmount();
        
        pheromoneMatrix.depositPheromone(x,y,depositValue);
        
    }
    private void depositPheromone(int value){
        int x = getCenter().x;
        int y= getCenter().y;
        pheromoneMatrix.depositPheromone(x,y,value);
        
    }
    
    private void adjustVelocity(){
        //para que o caminhar na horizontal seja constante em pixels
        //vide tri�ngulo de pit�goras, com os catetos de mesmo comprimento
        
        
        double normalizer = Math.sqrt(0.5d);
        switch (getDirection()){
            case Direction.NORTH: vx =0; vy=-getVelocity();
            break;
            case Direction.NORTHEAST: vx=(int)(getVelocity()*normalizer); vy=-vx;
            break;
            case Direction.EAST: vx=getVelocity(); vy=0;
            break;
            case Direction.SOUTHEAST: vx=(int)(getVelocity()*normalizer); vy=vx;
            break;
            case Direction.SOUTH: vy=+getVelocity(); vx=0;
            break;
            case Direction.SOUTHWEST: vx=-1*(int)(getVelocity()*normalizer); vy=-vx;
            break;
            case Direction.WEST: vx=-getVelocity(); vy=0;
            break;
            case Direction.NORTHWEST: vx=-1*(int)(getVelocity()*normalizer); vy = vx;
            break;
        }
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
        adjustVelocity();
    }
    
    public void setDirection(int direction) {
        this.direction.setDirection(direction);
        adjustVelocity();
    }
    
    public int getDirection() {
        return direction.getDirection();
    }
    
    public int getVelocity() {
        return velocity;
    }
    
    @Override
    public void setPosition(Point position) {
        //super.setPosition(position);
        double dx = position.getX()-getPosition().getX();
        double dy = position.getY()-getPosition().getY();
        AffineTransform at = AffineTransform.getTranslateInstance(dx,dy);
        transform(at);
        super.setPosition(position);
    }
    
    
    
    /**
     * Set constant for velocity
     * @param velocity in pixels
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
    
    public PheromoneMatrix getPheromoneMatrix() {
        return pheromoneMatrix;
    }
    
    public void setPheromoneMatrix(PheromoneMatrix pheromoneMatrix) {
        this.pheromoneMatrix = pheromoneMatrix;
        sniffer = new  PheromoneSniffer(this.pheromoneMatrix);
        sniffer.setDistance(10);
    }
    
    
    public void actorHit(Actor a, int sensor, double distanceToTheCenter) {
        if(a instanceof FoodSource){
            if (isCarryingFood())
                return;
            actorTypeHit = FOOD;
            
            
        }else if(a instanceof Nest){
            if(!isCarryingFood())
                return;
            actorTypeHit = NEST;
        }
        //setActorDistance(distanceToTheCenter);
        actorWasHit = a;
        actorSensors[sensor]=true;
        
    }
    
    /**
     * Gets the sensor Rectangle
     * @param i which of the sensors
     * @return Returns the correspondent Rectangle2D of the request sensor
     */
    public Rectangle2D getSensor(int i) {
        Rectangle2D sensor = null;
        switch (i){
            case 0: sensor = rightAntenna.getBounds2D();
            break;
            case 1: sensor = leftAntenna2.getBounds2D();
            break;
            case 2: /*sensor = body.getBounds2D();*/
                double y = getPosition().getY();
                double w = getDimension().getWidth() / 2.0d;
                double x = getCenter().getX() - w/2.0d;
                double h = getDimension().getHeight() / 4.0d;
                
                sensor = new Rectangle2D.Double(x,y,w,h);
        }
        return sensor;
    }
    
    /**
     * To be called when the ant hits an obstacle
     * @param sensorIdx The index of the sensor which has been hit
     */
    @Override
    public void hitAnObstacle(int sensorIdx) {
        hasHit = true;
        switch (sensorIdx){
            case 0:
                sensors[LEFT_ANTENNA] = true;
                break;
            case 1: lastSensorHit = RIGHT_ANTENNA;
            sensors[RIGHT_ANTENNA] = true;
            break;
            case 2:
                sensors[BODY] = true;
                break;
        }
    }
    
    
    public int getNumOfSensors(){
        return 2;
    }
    
    public int getNoTurnProbability() {
        return noTurnProbability;
    }
    
    public void setNoTurnProbability(int noTurnProbability) {
        this.noTurnProbability = noTurnProbability;
    }
    
    public int getClockwise_45_Probability() {
        return clockwise_45_Probability;
    }
    
    public void setClockwise_45_Probability(int clockwise_45_Probability) {
        this.clockwise_45_Probability = clockwise_45_Probability;
    }
    
    public int getClockwise_90_Probability() {
        return clockwise_90_Probability;
    }
    
    public void setClockwise_90_Probability(int clockwise_90_Probability) {
        this.clockwise_90_Probability = clockwise_90_Probability;
    }
    
    public int getClockwise_135_Probability() {
        return clockwise_135_Probability;
    }
    
    public void setClockwise_135_Probability(int clockwise_135_Probability) {
        this.clockwise_135_Probability = clockwise_135_Probability;
    }
    
    public int getClockwise_180_Probability() {
        return clockwise_180_Probability;
    }
    
    public void setClockwise_180_Probability(int clockwise_180_Probability) {
        this.clockwise_180_Probability = clockwise_180_Probability;
    }
    
    public int getCounterclockwise_45_Probability() {
        return counterclockwise_45_Probability;
    }
    
    public void setCounterclockwise_45_Probability(int counterclockwise_45_Probability) {
        this.counterclockwise_45_Probability = counterclockwise_45_Probability;
    }
    
    public int getCounterclockwise_90_Probability() {
        return counterclockwise_90_Probability;
    }
    
    public void setCounterclockwise_90_Probability(int counterclockwise_90_Probability) {
        this.counterclockwise_90_Probability = counterclockwise_90_Probability;
    }
    
    public int getCounterclockwise_135_Probability() {
        return counterclockwise_135_Probability;
    }
    
    public void setCounterclockwise_135_Probability(int counterclockwise_135_Probability) {
        this.counterclockwise_135_Probability = counterclockwise_135_Probability;
    }
    
    
    public int getPheromoneWeight() {
        return pheromoneWeight;
    }
    
    public void setPheromoneWeight(int pheromoneWeight) {
        this.pheromoneWeight = pheromoneWeight;
    }
    
    public int getInFrontPheromoneConcentration() {
        return inFrontPheromoneConcentration;
    }
    
    public void setInFrontPheromoneConcentration(int inFrontPheromoneConcentration) {
        this.inFrontPheromoneConcentration = inFrontPheromoneConcentration;
    }
    
    public int getIn45RightPheromoneConcentration() {
        return in45RightPheromoneConcentration;
    }
    
    public void setIn45RightPheromoneConcentration(int in45RightPheromoneConcentration) {
        this.in45RightPheromoneConcentration = in45RightPheromoneConcentration;
    }
    
    public int getIn45LeftPheromoneConcentration() {
        return in45LeftPheromoneConcentration;
    }
    
    public void setIn45LeftPheromoneConcentration(int in45LeftPheromoneConcentration) {
        this.in45LeftPheromoneConcentration = in45LeftPheromoneConcentration;
    }
    
    public int getIn90RightPheromoneConcentration() {
        return in90RightPheromoneConcentration;
    }
    
    public void setIn90RightPheromoneConcentration(int in90RightPheromoneConcentration) {
        this.in90RightPheromoneConcentration = in90RightPheromoneConcentration;
    }
    
    public int getIn90LeftPheromoneConcentration() {
        return in90LeftPheromoneConcentration;
    }
    
    public void setIn90LeftPheromoneConcentration(int in90LeftPheromoneConcentration) {
        this.in90LeftPheromoneConcentration = in90LeftPheromoneConcentration;
    }
    
    public int getInBackPheromoneConcentration() {
        return inBackPheromoneConcentration;
    }
    
    public void setInBackPheromoneConcentration(int inBackPheromoneConcentration) {
        this.inBackPheromoneConcentration = inBackPheromoneConcentration;
    }
    
    public int getIn135RightPheromoneConcentration() {
        return in135RightPheromoneConcentration;
    }
    
    public void setIn135RightPheromoneConcentration(int in135RightPheromoneConcentration) {
        this.in135RightPheromoneConcentration = in135RightPheromoneConcentration;
    }
    
    public int getIn135LeftPheromoneConcentration() {
        return in135LeftPheromoneConcentration;
    }
    
    public void setIn135LeftPheromoneConcentration(int in135LeftPheromoneConcentration) {
        this.in135LeftPheromoneConcentration = in135LeftPheromoneConcentration;
    }
    
    public int getPheromoneThreshold() {
        return pheromoneThreshold;
    }
    
    public void setPheromoneThreshold(int pheromoneThreshold) {
        this.pheromoneThreshold = pheromoneThreshold;
    }
    
    public int getPheromoneLevelAround() {
        return pheromoneLevelAround;
    }
    
    public void setPheromoneLevelAround(int pheromoneLevelAround) {
        this.pheromoneLevelAround = pheromoneLevelAround;
    }
    
    public int getCarryingFoodPheromoneWeight() {
        return carryingFoodPheromoneWeight;
    }
    
    /**
     *
     * @param carryingFoodPheromoneWeight
     */
    public void setCarryingFoodPheromoneWeight(int carryingFoodPheromoneWeight) {
        this.carryingFoodPheromoneWeight = carryingFoodPheromoneWeight;
    }
    
    public int getCarryingFoodPheromoneThreshold() {
        return carryingFoodPheromoneThreshold;
    }
    
    public void setCarryingFoodPheromoneThreshold(int carryingFoodPheromoneThreshold) {
        this.carryingFoodPheromoneThreshold = carryingFoodPheromoneThreshold;
    }
    
    public int getPheromoneDepositAmount() {
        return pheromoneDepositAmount;
    }
    
    public void setPheromoneDepositAmount(int pheromoneDepositAmount) {
        this.pheromoneDepositAmount = pheromoneDepositAmount;
    }
    
    public int getCarryingFoodPheromoneDepositAmount() {
        return carryingFoodPheromoneDepositAmount;
    }
    
    /**
     * Sets the amount of pheromone that will be deposited when the ant is
     * carrying food in each turn.
     * @param carryingFoodPheromoneDepositAmount the amount to be deposited.
     */
    public void setCarryingFoodPheromoneDepositAmount(int carryingFoodPheromoneDepositAmount) {
        this.carryingFoodPheromoneDepositAmount = carryingFoodPheromoneDepositAmount;
    }
    
    public Color getNormalColor() {
        return normalColor;
    }
    
    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
    }
    
    public Color getCarryingFoodColor() {
        return carryingFoodColor;
    }
    
    public void setCarryingFoodColor(Color carryingFoodColor) {
        this.carryingFoodColor = carryingFoodColor;
    }
    
    public double getActorDistance() {
        return actorDistance;
    }
    
    public void setActorDistance(double actorDistance) {
        this.actorDistance = actorDistance;
    }
    
    
    
    
}
