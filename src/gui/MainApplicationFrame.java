package gui;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.*;

import log.Logger;
import org.json.simple.parser.ParseException;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private ClosingHandler closingHandler = new ClosingHandler();
    private LoadingHandler loadingHandler = new LoadingHandler();
    private StatesKeeper keeper;
    
    public MainApplicationFrame() throws IOException {
        keeper = new StatesKeeper(new File("framesProperties.txt"));
        setLocationRelativeTo(null);
        setContentPane(desktopPane);

        addWindow(createLogWindow());
        addWindow(createGameWindow());
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try
                {
                    keeper.save();
                }
                catch (IOException ex)
                {
                    System.out.println(ex.toString());
                }
                closingHandler.handleClosing();
            }
        });
        try
        {
            if (keeper.canLoad()) {
                loadingHandler.handleLoading(keeper);
            }
        }
        catch(IOException | ParseException e)
        {
            Logger.error(e.toString());
        }

    }

    protected GameWindow createGameWindow()
    {
        GameWindow gameWindow = new GameWindow(keeper);
        gameWindow.setSize(400, 400);
        gameWindow.setAlignmentX(GameWindow.CENTER_ALIGNMENT);
        return gameWindow;
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource(), keeper);
        logWindow.setLocation(10,10);
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu visualModeMenu = CreateVisualModeMenu();
        JMenu testMenu = CreateTestMenu();
        menuBar.add(visualModeMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private JMenu CreateVisualModeMenu()
    {
        
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        
        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    private JMenu CreateTestMenu()
    {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);
        return testMenu;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }
    }
}
