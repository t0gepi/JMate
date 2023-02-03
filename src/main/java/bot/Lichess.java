package bot;


import driver.Driver;
import gui.components.button.HotkeyButton;
import gui.panel.SettingsPanel;
import io.engine.CLI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;




public class Lichess implements Runnable{

    private final Logger LOGGER = LoggerFactory.getLogger(Lichess.class);
    private final StartStopListener startStopListener;
    private final SettingsPanel settingsPanel;
    public static boolean hotkeyReady = false;


    // Information that does not change during game
    private static final String[] boardx = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] boardy = new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};

    private final Boolean[][] squaresWithPieces = new Boolean[][]{
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false},
            {true,true,null,null,null,null,false,false}
    };
    private String lastMove = null;

    private final List<String> moveHistory = new ArrayList<>();
    private boolean myKingHasntMoved = true;
    private boolean enemyKingHasntMoved = true;

    public Lichess(StartStopListener startStopListener, SettingsPanel settingsPanel){
        this.startStopListener = startStopListener;
        this.settingsPanel = settingsPanel;
    }


    // static methods

    private static boolean isMyColorWhite(){
        return Driver.getInstance().findElement(By.className("cg-wrap")).getAttribute("class").contains("white"); //get the initial board orientation
    }

    private static boolean isMyClockRunning(){
        return Driver.getInstance().findElements(By.cssSelector("div.rclock.rclock-bottom.running")).size() != 0;    //bottom clock running
    }

    private static boolean hasOngoingGame() {
        if(Driver.getInstance().findElements(By.cssSelector("div.rclock.rclock-bottom.running")).size() != 0         //bottom clock running
                || Driver.getInstance().findElements(By.cssSelector("div.rclock.rclock-top.running")).size() != 0    //top clock running
                || Driver.getInstance().findElements(By.cssSelector("div.follow-up")).size() == 0)                   //"revanche" and "new opponent" buttons container
        {
            return true;
        }
        return false;
    }

    public static double getSquareLength(){
        String[] style = Driver.getInstance().findElement(By.tagName(("cg-container"))).getAttribute("style").split("\\s+");
        String stringBoardLength =  style[1];
        stringBoardLength = stringBoardLength.replace("px;","");
        double boardLength = Double.parseDouble(stringBoardLength);
        return boardLength/8;
    }

    private static boolean isPromotionMove(String move){
        // e.g. e7e8q
        return move.length() == 5;
    }

    private static void promote(String move){
        String piece = switch (move.substring(4)) {
            case "q" -> "queen";
            case "b" -> "bishop";
            case "r" -> "rook";
            case "n" -> "knight";
            default -> null;
        };

        WebElement promotionPanel = new WebDriverWait(Driver.getInstance(), Duration.ofMillis(500))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("promotion-choice")));
        for(WebElement e : promotionPanel.findElements(By.tagName("square"))){
            if(e.findElement(By.tagName("piece")).getAttribute("class").contains(piece)){
                e.click();
                return;
            }
        }
    }



    // non-static methods
    private void doMove(String move) {
        String from_x = move.substring(0,1);
        String from_y = move.substring(1,2);
        String to_x = move.substring(2,3);
        String to_y = move.substring(3,4);
        double squareLength = getSquareLength();
        boolean iAmWhite = isMyColorWhite();

        double from_x_px = 0;
        for(int i = 0; i < 8; i++){
            if(boardx[i].equals(from_x)){
                from_x_px = iAmWhite ? i*squareLength : (7-i)*squareLength;
                break;
            }
        }

        double from_y_px = 0;
        for(int i = 0; i < 8; i++){
            if(boardy[i].equals(from_y)){
                from_y_px = iAmWhite ? i*squareLength : (7-i)*squareLength;
                break;
            }
        }

        double to_x_px = 0;
        for(int i = 0; i < 8; i++){
            if(boardx[i].equals(to_x)){
                to_x_px = iAmWhite ? i*squareLength : (7-i)*squareLength;
                break;
            }
        }

        double to_y_px = 0;
        for(int i = 0; i < 8; i++){
            if(boardy[i].equals(to_y)){
                to_y_px = iAmWhite ? i*squareLength : (7-i)*squareLength;
                break;
            }
        }


        double xOffset = to_x_px - from_x_px;
        double yOffset = to_y_px - from_y_px;

        double finalFrom_x_px = from_x_px;
        double finalFrom_y_px = from_y_px;

        Optional<WebElement> fromElement = Driver.getInstance().findElement(By.tagName("cg-board")).findElements(By.cssSelector("piece"))
                .stream()
                .filter(e ->{
                    String[] fromxy = e.getAttribute("style").replace("transform: ", "")
                            .replace("translate(", "")
                            .replace(")", "")
                            .replace(",", "")
                            .replace("px", "")
                            .replace(";", "")
                            .split("\\s+");
                    return Math.abs(finalFrom_x_px - Double.parseDouble(fromxy[0])) < 1 && Math.abs(finalFrom_y_px - Double.parseDouble(fromxy[1])) < 1;
                })
                .findFirst();

        if(startStopListener.isStop()){ //scanning the html for elements takes the most time. therefore check if isStop==true afterwards.
            return;
        }

        Actions actions = new Actions(Driver.getInstance());
        Action dragAndDrop = actions.clickAndHold(fromElement.get()).moveByOffset((int)xOffset, (int)yOffset).release().build();
        dragAndDrop.perform();

        lastMove = move;
        moveHistory.add(lastMove);

        if(iAmWhite){
            boolean didntCastle = true;
            if(from_x_px/squareLength == 4 && from_y_px/squareLength == 7){
                if(myKingHasntMoved){
                    if(to_y_px/squareLength==7){
                        if(to_x_px/squareLength == 6){
                            //update boolboard right side castle as white
                            squaresWithPieces[7][0] = null;
                            squaresWithPieces[4][0] = null;
                            squaresWithPieces[6][0] = true;
                            squaresWithPieces[5][0] = true;
                            didntCastle = false;
                        }
                        if(to_x_px/squareLength == 2){
                            //update boolboard left side castle as white
                            squaresWithPieces[0][0] = null;
                            squaresWithPieces[4][0] = null;
                            squaresWithPieces[2][0] = true;
                            squaresWithPieces[3][0] = true;
                            didntCastle = false;
                        }
                    }
                    myKingHasntMoved = false;
                }
            }
            if(didntCastle){
                squaresWithPieces[(int)Math.round(to_x_px/squareLength)][7-(int)Math.round(to_y_px/squareLength)] = true;
                squaresWithPieces[(int)Math.round(from_x_px/squareLength)][7-(int)Math.round(from_y_px/squareLength)] = null;
            }
            String myMove = isMyColorWhite() ? boardx[(int)Math.round(from_x_px/squareLength)] + boardy[(int)Math.round(from_y_px/squareLength)] + boardx[(int)Math.round(to_x_px/squareLength)] + boardy[(int)Math.round(to_y_px/squareLength)]
                    : boardx[7-(int)Math.round(from_x_px/squareLength)] + boardy[7-(int)Math.round(from_y_px/squareLength)] + boardx[7-(int)Math.round(to_x_px/squareLength)] + boardy[7-(int)Math.round(to_y_px/squareLength)];
            System.out.println("I moved " + myMove);
        }
        else{
            boolean didntCastle = true;
            if(from_x_px/squareLength == 3 && from_y_px/squareLength == 7){
                if(myKingHasntMoved){
                    if(to_y_px/squareLength==7){
                        if(to_x_px/squareLength == 5){
                            //update boolboard right side castle as black
                            squaresWithPieces[0][7] = null;
                            squaresWithPieces[4][7] = null;
                            squaresWithPieces[2][7] = false;
                            squaresWithPieces[3][7] = false;
                            didntCastle = false;
                        }
                        if(to_x_px/squareLength == 1){
                            //update boolboard left side castle as black
                            squaresWithPieces[7][7] = null;
                            squaresWithPieces[4][7] = null;
                            squaresWithPieces[6][7] = false;
                            squaresWithPieces[5][7] = false;
                            didntCastle = false;
                        }
                    }
                    myKingHasntMoved = false;
                }
            }
            if(didntCastle){
                squaresWithPieces[7-(int)Math.round(to_x_px/squareLength)][(int)Math.round(to_y_px/squareLength)] = false;
                squaresWithPieces[7-(int)Math.round(from_x_px/squareLength)][(int)Math.round(from_y_px/squareLength)] = null;
            }
            String myMove = isMyColorWhite() ? boardx[(int)Math.round(from_x_px/squareLength)] + boardy[(int)Math.round(from_y_px/squareLength)] + boardx[(int)Math.round(to_x_px/squareLength)] + boardy[(int)Math.round(to_y_px/squareLength)]
                    : boardx[7-(int)Math.round(from_x_px/squareLength)] + boardy[7-(int)Math.round(from_y_px/squareLength)] + boardx[7-(int)Math.round(to_x_px/squareLength)] + boardy[7-(int)Math.round(to_y_px/squareLength)];
            System.out.println("I moved " + myMove);
        }
        if(isPromotionMove(move))
            promote(move);
    }





    private void fetchLastMove() {

        double squareLength = getSquareLength();

        List<WebElement> lastMoves = Driver.getInstance().findElements(By.cssSelector("square.last-move"));

        if(startStopListener.isStop()){ //scanning the html for elements takes the most time. therefore check if isStop==true afterwards.
            return;
        }

        if(lastMoves.size() != 2)
            return;

        String[] to_xy = lastMoves.get(0).getAttribute("style").replace("transform: ", "")
                .replace("translate(", "")
                .replace(")", "")
                .replace(",", "")
                .replace("px", "")
                .replace(";", "")
                .split("\\s+");

        int to_x = (int) Math.round(Double.parseDouble(to_xy[0]) / squareLength);
        int to_y = (int) Math.round(Double.parseDouble(to_xy[1]) / squareLength);

        String[] from_xy = lastMoves.get(1).getAttribute("style").replace("transform: ", "")
                .replace("translate(", "")
                .replace(")", "")
                .replace(",", "")
                .replace("px", "")
                .replace(";", "")
                .split("\\s+");

        int from_x = (int) Math.round(Double.parseDouble(from_xy[0]) / squareLength);
        int from_y = (int) Math.round(Double.parseDouble(from_xy[1]) / squareLength);

        boolean didntCastle = true;

        if(isMyColorWhite()){
            if(startStopListener.isStop()){ //scanning the html for elements takes the most time. therefore check if isStop==true afterwards.
                return;
            }
            if(from_x == 4 & from_y == 0){
                if(enemyKingHasntMoved){
                    if(to_x == 7 && to_y == 0){
                        // update boolboard right side castle black
                        squaresWithPieces[7][7] = null;
                        squaresWithPieces[4][7] = null;
                        squaresWithPieces[5][7] = false;
                        squaresWithPieces[6][7] = false;
                        didntCastle = false;
                        from_x = 4;
                        to_x = 6;
                        from_y = 0;
                        to_y = 0;
                        System.out.println("BLACK CASTLED RIGHT SIDE");
                    }
                    if(to_x == 0 && to_y == 0){
                        // update boolboard left side castle black
                        squaresWithPieces[0][7] = null;
                        squaresWithPieces[4][7] = null;
                        squaresWithPieces[2][7] = false;
                        squaresWithPieces[3][7] = false;
                        didntCastle = false;
                        from_x = 4;
                        to_x = 2;
                        from_y = 0;
                        to_y = 0;
                        System.out.println("BLACK CASTLED LEFT SIDE");
                    }
                    enemyKingHasntMoved = false;
                }
            }
            if(didntCastle && squaresWithPieces[to_x][7- to_y] != null && squaresWithPieces[to_x][7- to_y] == false){
                int tmp = from_x;
                from_x = to_x;
                to_x = tmp;
                tmp = from_y;
                from_y = to_y;
                to_y = tmp;
            }
        }
        else{
            if(startStopListener.isStop()){ //scanning the html for elements takes the most time. therefore check if isStop==true afterwards.
                return;
            }
            if(from_x == 3 && from_y == 0){
                if(enemyKingHasntMoved){
                    if(to_x == 7 && to_y == 0){
                        // update boolboard right side castle white
                        squaresWithPieces[0][0] = null;
                        squaresWithPieces[4][0] = null;
                        squaresWithPieces[2][0] = true;
                        squaresWithPieces[3][0] = true;
                        didntCastle = false;
                        from_x = 3;
                        to_x = 5;
                        from_y = 0;
                        to_y = 0;
                        System.out.println("WHITE CASTLED RIGHT SIDE");
                    }
                    if(to_x == 0 && to_y == 0){
                        // update boolboard left side castle white
                        squaresWithPieces[7][0] = null;
                        squaresWithPieces[4][0] = null;
                        squaresWithPieces[6][0] = true;
                        squaresWithPieces[5][0] = true;
                        didntCastle = false;
                        from_x = 3;
                        to_x = 1;
                        from_y = 0;
                        to_y = 0;
                        System.out.println("WHITE CASTLED LEFT SIDE");
                    }
                    enemyKingHasntMoved = false;
                }
            }
            if(didntCastle && squaresWithPieces[7- to_x][to_y] != null && squaresWithPieces[7- to_x][to_y]){
                int tmp = from_x;
                from_x = to_x;
                to_x = tmp;
                tmp = from_y;
                from_y = to_y;
                to_y = tmp;
            }
        }

        String fetchedLastMove = isMyColorWhite() ? boardx[from_x] + boardy[from_y] + boardx[to_x] + boardy[to_y]
                : boardx[7- from_x] + boardy[7- from_y] + boardx[7- to_x] + boardy[7- to_y];

        if(fetchedLastMove != lastMove)
        {
            lastMove = fetchedLastMove;
            moveHistory.add(lastMove);

            if(didntCastle){
                if(isMyColorWhite()){
                    squaresWithPieces[to_x][7- to_y] = false;
                    squaresWithPieces[from_x][7- from_y] = null;
                }
                else{
                    squaresWithPieces[7- to_x][to_y] = true;
                    squaresWithPieces[7- from_x][from_y] = null;
                }
            }
            System.out.println("Opponent played " + fetchedLastMove);
        }

    }





    @Override
    public void run() {
        LOGGER.info("Starting engine...");
        try {
            CLI.startNewProcess();
        } catch (IOException e) {
            LOGGER.error("Error while starting the engine");
            e.printStackTrace();
            return;
        }
        LOGGER.info("Engine ready");


        // waiting for game to be found
        LOGGER.info("Waiting for game");
        while(true){
            if(startStopListener.isStop()){
                return;
            }
            if(Driver.getInstance().findElements(By.cssSelector("div.rclock.rclock-top")).size() != 0){
                break;
            }
            System.out.println("test");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Game found!");


        // waiting for opponent to do first move
        boolean once = true;
        while(!startStopListener.isStop() && !isMyClockRunning() && !isMyColorWhite() && lastMove == null && hasOngoingGame()){
            if(startStopListener.isStop()){
                return;
            }
            if(once){
                LOGGER.info("Waiting for Opponents move1...");
                once = false;
            }
            fetchLastMove();
        }

        // Going into gameLoop
        try {
            gameLoop();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private void gameLoop() throws IOException, InterruptedException {
        if (moveHistory.size() == 0)
            fetchLastMove();

        String bestMove = CLI.getBestMoveForDepth(moveHistory, settingsPanel.getDepthSlider().getValue());
        doMove(bestMove);
        LOGGER.info("Waiting for Opponent to do a move...");

        while (!startStopListener.isStop() && hasOngoingGame()) {
            if(startStopListener.isStop()){
                return;
            }
            Thread.sleep(10);
            if (isMyClockRunning()) {
                Thread.sleep(10);
                fetchLastMove();
                //TODO: pass the arrowsSwitch as another parameter to the CLI function so it knows if it has to draw and update the bestmoves with arrows or not
                hotkeyReady = true;
                bestMove = CLI.getBestMoveForDepth(moveHistory, settingsPanel.getDepthSlider().getValue());

                if(startStopListener.isStop()){
                    return;
                }
                if(HotkeyButton.wasPressed){
                    doMove(bestMove);
                    HotkeyButton.wasPressed=false;
                }
                else if(settingsPanel.getAutoplaySwitch().isSelected()){
                    doMove(bestMove);
                }
                hotkeyReady = false;
                LOGGER.info("Waiting for Opponent to do a move...");
            }
        }

        LOGGER.info("Killing engine process...");
        CLI.killProcess();
        while (CLI.isProcessAlive()) {
            Thread.sleep(25);
        }
        LOGGER.info("engine process killed.");

    }





}
