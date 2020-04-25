package cat.indiketa.degiro.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author indiketa
 */
public enum DLog {

    DEGIRO,
    HTTP;

    private final Logger logger;

    private DLog() {
        this.logger = LoggerFactory.getLogger(name());
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String message, Throwable t) {
        logger.debug(message, t);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void warn(String message, Throwable t) {
        logger.warn(message, t);
    }
    
    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, Throwable t) {
        logger.info(message, t);
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public void fatal(String message) {
        logger.error(message);
    }

    public void fatal(String message, Throwable t) {
        logger.error(message, t);
    }

    public boolean isFatalEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void trace(String message) {
        logger.trace(message);
    }

    public void trace(String message, Throwable t) {
        logger.trace(message, t);
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

}
