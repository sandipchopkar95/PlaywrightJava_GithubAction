package baseTest;

import com.microsoft.playwright.Page;
import factory.PlaywrightFactory;
import org.testng.annotations.*;

import java.util.Properties;

import static factory.SessionManagement.clearSessionFile;

public class BaseTest {
    protected PlaywrightFactory playwrightFactory;
    protected Properties prop;

    @Parameters({"browser", "headless"})
    @BeforeSuite
    public void initialize_Browser_And_Save_Session(@Optional("chrome") String browser, @Optional("false") String headless) {
        playwrightFactory = new PlaywrightFactory();
        prop = playwrightFactory.init_prop();
        if (browser == null || browser.isEmpty()) {
            browser = prop.getProperty("browser", "chrome");
        }
        if (headless == null || headless.isEmpty()) {
            headless = prop.getProperty("headless", "false");
        }
        boolean headlessMode = Boolean.parseBoolean(headless);
        // Pre-store session using shared BrowserContext
        Page sessionPage = playwrightFactory.initBrowser(browser, headlessMode);
        sessionPage.context().browser().close();
    }

    @Parameters({"browser", "headless"})
    @BeforeMethod
    public void initialize_Browser_With_Session(@Optional("chrome") String browser, @Optional("false") String headless) {
        playwrightFactory = new PlaywrightFactory();
        prop = playwrightFactory.init_prop();
        if (browser == null || browser.isEmpty()) {
            browser = prop.getProperty("browser", "chrome");
        }
        if (headless == null || headless.isEmpty()) {
            headless = prop.getProperty("headless", "false");
        }
        boolean headlessMode = Boolean.parseBoolean(headless);
        playwrightFactory = new PlaywrightFactory();
        playwrightFactory.initBrowser(browser, headlessMode);
    }

    protected Page getPage() {
        return PlaywrightFactory.getPage();
    }

    @AfterMethod
    public void tearDown() {
        try {
            if (PlaywrightFactory.getPage() != null) {
                getPage().waitForTimeout(3000);
                try {
                    PlaywrightFactory.getPage().close();
                } catch (Exception e) {
                    System.out.println("Page already closed: " + e.getMessage());
                }
            }
            if (PlaywrightFactory.getBrowserContext() != null) {
                try {
                    PlaywrightFactory.getBrowserContext().close();
                } catch (Exception e) {
                    System.out.println("BrowserContext already closed: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error during tearDown: " + e.getMessage());
        }
    }

    @AfterSuite
    public void clearSession() {
        clearSessionFile();
    }
}
