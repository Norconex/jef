import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

    public static void main(String[] args) {
        System.out.println("HELLO!");
        
        Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        //log.debug(marker, format, arg);
    }
    
}
