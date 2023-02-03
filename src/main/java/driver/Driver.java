package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class Driver {

    private static WebDriver webDriver;

    private Driver(){}

    public static WebDriver getInstance(){
        if(webDriver == null){
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("debuggerAddress","localhost:9222");
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver(chromeOptions);
        }
        return webDriver;
    }

    public static void main(String[] args) {
        Driver.getInstance();
    }
}