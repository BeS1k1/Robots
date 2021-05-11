package log;

public final class Logger
{
    private static final LogWindowSource defaultLogSource;
    static {
        defaultLogSource = new LogWindowSource(100);
    }
    
    private Logger()
    {
    }

    public static void trace(String strMessage)
    {
        defaultLogSource.append(LogLevel.Trace, strMessage);
    }

    public static void debug(String strMessage)
    {
        defaultLogSource.append(LogLevel.Debug, strMessage);
    }


    public static void info(String strMessage)
    {
        defaultLogSource.append(LogLevel.Info, strMessage);
    }

    public static void warning(String strMessage)
    {
        defaultLogSource.append(LogLevel.Warning, strMessage);
    }
    
    public static void error(String strMessage)
    {
        defaultLogSource.append(LogLevel.Error, strMessage);
    }

    public static void fatal(String strMessage)
    {
        defaultLogSource.append(LogLevel.Fatal, strMessage);
    }

    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}
