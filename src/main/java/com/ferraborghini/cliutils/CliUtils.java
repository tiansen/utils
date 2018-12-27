package com.ferraborghini.cliutils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

@Setter
@Getter
public class CliUtils {
    private Options ops;

    public CliUtils(){
        Options options = new Options();
        OptionGroup optionGroup = new OptionGroup();
        Option D = Option.builder("D").argName("property=value").numberOfArgs(2).valueSeparator('=')
                .desc("use value for given property").build();// 这里numberOfArgs指定了后跟两个参数，且valueSeparator指定了连接符是=，这样CLI可以自动帮我们解析键值对
//        options.addOption(D);
        optionGroup.addOption(D);
        options.addOptionGroup(optionGroup);
        options.addOption("h", "help", false, "print options' information");
        options.addOption("d", "database", true, "name of a database");
        options.addOption("t", true, "name of a table");


        this.ops = options;
    }
}
