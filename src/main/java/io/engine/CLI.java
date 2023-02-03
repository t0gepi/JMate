package io.engine;

import com.google.errorprone.annotations.Var;
import config.ConfigManager;
import gui.components.button.HotkeyButton;
import gui.frame.JMate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 *  The Command Line Interface (CLI) reads and writes to the engine.
 */
public final class CLI {

    private static Process process;
    private static BufferedReader processReader;
    private static OutputStreamWriter processWriter;

    private static Opening opening;
    private static Variant variant;

    private CLI(){}

    public static void startNewProcess() throws IOException {
        killProcess();
        String engine = ConfigManager.getProperty("engine");
        String ENGINE_PATH = String.format(JMate.CONTENT_ROOT_DIR + "/engine/%s/%s.exe", engine, engine);
        process = Runtime.getRuntime().exec(ENGINE_PATH);
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        processWriter = new OutputStreamWriter(process.getOutputStream());

        variant = Variant.getByName(ConfigManager.getProperty("variant"));
        opening = Opening.getByName(ConfigManager.getProperty("opening"));

        //TODO: if variant != chess write "setoption name UCI_Variant value <variantname>"

    }

    public static boolean isProcessAlive(){
        return process != null && process.isAlive();
    }

    public static void killProcess(){
        if(process != null)
            process.destroy();
    }

    public static void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBestMoveForDepth(List<String> moveHistory, int depth) throws IOException {
        //TODO: if a preferred opening is selected, try to play it for the first 4 moves. Create some Opening data for first 4 moves.
        if(moveHistory.size() != 0){
            sendCommand("ucinewgame\n");
            StringBuilder command = new StringBuilder();
            command.append("position startpos moves ");
            for(String move : moveHistory){         // building a large string like "position startpos move e2e4 e7e5" is easier than calculating the FEN String
                command.append(move).append(" ");
            }
            sendCommand(command + "\n");
            sendCommand(String.format("go depth %s\n", depth));
        }
        else{
            sendCommand("ucinewgame\n");
            sendCommand(String.format("position startpos \ngo depth %d\n",depth));
        }
        return waitForBestMove();
    }


    private static String waitForBestMove() throws IOException {
        String line = processReader.readLine();
        System.out.println(line);
        while(!line.startsWith("bestmove")){
            if(HotkeyButton.wasPressed){
                sendCommand("stop\n");
            }
            line = processReader.readLine();
            System.out.println(line);
        }
        return line.split("\\s+")[1];
    }

}