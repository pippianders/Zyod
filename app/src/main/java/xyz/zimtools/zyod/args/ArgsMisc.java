package xyz.zimtools.zyod.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import xyz.zimtools.zyod.args.converters.MillisecondConverter;

public final class ArgsMisc {
    @Parameter(names = "--web-wait", description = "Amount of seconds to wait for browser to " +
            "INITIALLY" +
            " load up each OD before executing Zyod.", validateWith = PositiveInteger.class,
            converter = MillisecondConverter.class)
    private Long webWait = 15000L;

    @Parameter(names = {"-r", "--refresh"}, description = "Refresh the page and try again upon failure.")
    private boolean refreshing;

    @Parameter(names = "--load-wait", description = "Amount of seconds to wait for a page load to" +
            " complete before throwing an error.", validateWith = PositiveInteger.class,
            converter = MillisecondConverter.class)
    private Long loadWait = 30000L;

    public long getWebWait() {
        return webWait;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public long getLoadWait() {
        return loadWait;
    }
}