package xyz.zimtools.zyod.scrapers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.remote.RemoteWebDriver;
import xyz.zimtools.zyod.args.Args;
import xyz.zimtools.zyod.assets.Directory;
import xyz.zimtools.zyod.assets.ODUrl;
import xyz.zimtools.zyod.assets.info.NavInfoParser;
import xyz.zimtools.zyod.browsers.DriverFactory;
import xyz.zimtools.zyod.fixtures.GlobalDefault;
import xyz.zimtools.zyod.fixtures.ODDefault;
import xyz.zimtools.zyod.fixtures.asserts.ScraperAssert;
import xyz.zimtools.zyod.od.ODType;
import xyz.zimtools.zyod.od.navigators.NavType;
import xyz.zimtools.zyod.scrapers.filters.OneDriveVercelIndexScrapeFilter;
import xyz.zimtools.zyod.scrapers.filters.ScrapeFilter;

import java.util.List;
import java.util.stream.Stream;

class TextScraperTest {
    private static final String[] MAIN_ARGS = {"--headless", "-r"};
    private static NavInfoParser parser;
    private static RemoteWebDriver driver;
    private ODScraper scraper;

    @BeforeAll
    static void beforeAll() {
        parser = new NavInfoParser();
    }

    @AfterAll
    static void afterAll() {
        driver.close();
    }

    private void init(String url, String odType, String navType, ScrapeFilter filter) {
        Args args = new Args(GlobalDefault.joinArr(new String[][]{new String[]{url}, MAIN_ARGS}));
        driver = DriverFactory.getDriver(args);
        driver.get(url);
        scraper = new TextScraper(driver, args, parser.getInfo(odType, navType), filter);
        scraper.scrape(List.of(), new Directory(1, new ODUrl(url)));
    }

    @ParameterizedTest
    @MethodSource("getParams")
    void scrapeTest(String url, ODType odType, String navType, int dirCount, int fileCount,
                    ScrapeFilter filter) {
        this.init(url, odType.name(), navType, filter);
        ScraperAssert.resourceCount(dirCount, scraper.getDirs().size(), "directories", url);
        ScraperAssert.resourceCount(fileCount, scraper.getFiles().size(), "files", url);
    }

    private static Stream<Arguments> getParams() {
        return Stream.of(
                Arguments.of(ODDefault.ONEDRIVE_VERCEL_INDEX, ODType.ONEDRIVE_VERCEL_INDEX,
                        NavType.Onedrive_Vercel_Index.LIST_VIEW.name(), 10, 0,
                        new OneDriveVercelIndexScrapeFilter()),
                Arguments.of(ODDefault.FODI, ODType.FODI, NavType.FODI.MAIN.name(), 4, 0, null)
        );
    }
}