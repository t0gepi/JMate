package io.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.EnumMap;

/**
 * Used to communicate with the commandline interface of the engine.
 * The engine is started as a separate process and the communication is done via stdin and stdout.
 */
public class CLI {

    private static final Logger logger;
    private static Process process;
    private static BufferedReader processReader;
    private static OutputStreamWriter processWriter;
    private static EnumMap<Engine,String> engine;
    private static Engine activeEngine;

    private CLI(){}

    static{
        logger = LoggerFactory.getLogger(CLI.class);
        engine = new EnumMap<>(Engine.class);
        engine.put(Engine.Stockfish, "io/engine/stockfish/stockfish.exe");
        engine.put(Engine.FairyStockfish, "io/engine/stockfish/fairystockfish.exe");
        engine.put(Engine.Lc0, "io/engine/lc0/lc0.exe");
    }


    public static void start(){
        if(process != null)
            process.destroy();
        try {
            process = Runtime.getRuntime().exec(engine.get(activeEngine));
        } catch (IOException e) {
            logger.error("Error while starting engine " + activeEngine.toString(), e);
        }
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        processWriter = new OutputStreamWriter(process.getOutputStream());
    }

    public static void stop(){
        if(process != null){
            process.destroy();
            logger.info("Engine " + activeEngine.toString() + " stopped");
        }
    }


    public static void setEngine(Engine engine){
        activeEngine = engine;
    }


}
