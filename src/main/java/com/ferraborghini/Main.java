package com.ferraborghini;

import com.ferraborghini.cliutils.CliUtils;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args){
        CommandLine comm = null;
        Options ops = new CliUtils().getOps();
        try {
            comm = new DefaultParser().parse(ops, args);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("param parse error：[" + Arrays.asList(args).toString() + "]");
            throw new IllegalArgumentException();
        }

        if (comm.getOptions().length == 0) {
            logger.info("No any param to specify.");
            return;
        }
        if (comm.hasOption("h")) {// help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", ops);
        }
        if (comm.hasOption("s")) {// 执行命令
            logger.info("s");
        }
        if (comm.hasOption("D")) {// 传递参数
            Properties props = comm.getOptionProperties("D");
        }
    }
}
