package xyz.zimtools.zyod.download;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import xyz.zimtools.zyod.AppConfig;
import xyz.zimtools.zyod.args.Args;
import xyz.zimtools.zyod.assets.info.DownloadInfo;
import xyz.zimtools.zyod.assets.info.DownloadInfoParser;
import xyz.zimtools.zyod.assets.info.NavInfo;
import xyz.zimtools.zyod.assets.info.NavInfoParser;
import xyz.zimtools.zyod.browsers.DriverFactory;
import xyz.zimtools.zyod.download.filters.DownloadFilter;
import xyz.zimtools.zyod.download.filters.FODIDLFilter;
import xyz.zimtools.zyod.download.filters.ZFileDLFilter;
import xyz.zimtools.zyod.fixtures.DownloadDefault;
import xyz.zimtools.zyod.fixtures.GlobalDefault;
import xyz.zimtools.zyod.fixtures.ODDefault;
import xyz.zimtools.zyod.fixtures.asserts.DownloadAssert;
import xyz.zimtools.zyod.od.ODType;
import xyz.zimtools.zyod.od.navigators.NavType;
import xyz.zimtools.zyod.support.NavSupport;

import java.util.Optional;

class DownloaderTest {
    private static final String[] USER_ARGS = {"--driver", "chrome", "--download", "--ddir",
            DownloadDefault.DOWNLOAD_DIR, "--dwait", "5"};
    private static Args args;
    private static RemoteWebDriver driver;
    private static NavInfoParser navInfoParser;
    private static DownloadInfoParser downloadInfoParser;
    private Downloader downloader;

    @BeforeAll
    static void beforeAll() {
        DownloadDefault.cleanDownloadDir();
    }

    @AfterEach
    void afterEach() {
        DownloadDefault.cleanDownloadDir();
        driver.close();
    }

    private void init(String[] extraArgs) {
        args = new Args(GlobalDefault.joinArr(new String[][]{extraArgs, USER_ARGS}));
        driver = DriverFactory.getDriver(args);
        navInfoParser = new NavInfoParser();
        downloadInfoParser = new DownloadInfoParser();
    }

    @Test
    void downloadGoIndex() {
        this.assertDownload(ODDefault.GO_INDEX, ODType.GOINDEX.name(),
                NavType.GoIndex.THUMBNAIL_VIEW.name());
    }

    @Test
    void downloadGDIndex() {
        this.assertDownload(ODDefault.GD_INDEX, ODType.GDINDEX.name(), NavType.GDIndex.MAIN.name());
    }

    @Test
    void downloadFODI() {
        this.init(new String[]{ODDefault.FODI});
        NavInfo navInfo = navInfoParser.getInfo(ODType.FODI.name(), NavType.FODI.MAIN.name());
        DownloadInfo downloadInfo = downloadInfoParser.getInfo(ODType.FODI.name(),
                NavType.FODI.MAIN.name());
        downloadInfo.setExtraTasks(driver -> {
            Optional<WebElement> element = NavSupport.getElement(driver, downloadInfo.getCssDownloadTask());
            element.get().click();
        });
        downloader = new Downloader(driver, args, navInfo, downloadInfo, new FODIDLFilter());
        driver.get(ODDefault.FODI);
        NavSupport.getElements(driver, navInfo.getCssFileSelector()).get(2).click();
        AppConfig.sleep(6000L); //Wait for page elements to load before starting download
        downloader.singleDownload();
        AppConfig.sleep(10000L);
        DownloadAssert.fileExists();
    }

    @Test
    void zfile() {
        this.init(new String[]{ODDefault.ZFILE});
        driver.get(ODDefault.ZFILE);
        NavInfo navInfo = navInfoParser.getInfo(ODType.ZFILE.name(), NavType.ZFile.MAIN.name());
        DownloadInfo downloadInfo = downloadInfoParser.getInfo(ODType.ZFILE.name(),
                NavType.ZFile.MAIN.name());
        downloader = new Downloader(driver, args, navInfo, downloadInfo, new ZFileDLFilter());
        downloader.rightClickSingleDownload();
        AppConfig.sleep(10000L);
        DownloadAssert.fileExists();
    }

    @Test
    void onedriveVercelIndex() {
        this.assertDownload(ODDefault.ONEDRIVE_VERCEL_INDEX, ODType.ONEDRIVE_VERCEL_INDEX.name(),
                NavType.Onedrive_Vercel_Index.MAIN.name());
    }

    @Test
    void alist() {
        init(new String[]{ODDefault.ALIST});
        NavInfo navInfo = navInfoParser.getInfo(ODType.ALIST.name(), NavType.AList.ORIGINAL.name());
        DownloadInfo downloadInfo = downloadInfoParser.getInfo(ODType.ALIST.name(),
                NavType.AList.ORIGINAL.name());
        downloadInfo.setExtraTasks(driver -> {
            AppConfig.sleep(5000L);
            Optional<WebElement> element = NavSupport.getElement(driver,
                    downloadInfo.getCssDownloadTask());
            element.ifPresent(WebElement::click);
        });
        downloader = new Downloader(driver, args, navInfo, downloadInfo);
        driver.get(ODDefault.ALIST);
        NavSupport.getElements(driver, navInfo.getCssFileSelector()).get(3).click();
        downloader.singleDownload();
        AppConfig.sleep(10000L);
        DownloadAssert.fileExists();
    }

    private void assertDownload(String url, String odType, String navType) {
        assertDownload(url, odType, navType, null);
    }

    private void assertDownload(String url, String odType, String navType, DownloadFilter filter) {
        this.init(new String[]{url});
        NavInfo navInfo = navInfoParser.getInfo(odType, navType);
        DownloadInfo downloadInfo = downloadInfoParser.getInfo(odType, navType);
        downloader = new Downloader(driver, args, navInfo, downloadInfo, filter);
        driver.get(url);
        downloader.singleDownload();
        AppConfig.sleep(15000L);
        DownloadAssert.fileExists();
    }
}