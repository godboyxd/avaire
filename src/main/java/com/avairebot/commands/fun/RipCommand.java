package com.avairebot.commands.fun;

import com.avairebot.AvaIre;
import com.avairebot.Constants;
import com.avairebot.Statistics;
import com.avairebot.contracts.commands.Command;
import com.avairebot.factories.MessageFactory;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RipCommand extends Command {

    public RipCommand(AvaIre avaire) {
        super(avaire);
    }

    @Override
    public String getName() {
        return "RIP Command";
    }

    @Override
    public String getDescription() {
        return "Pay your respects";
    }

    @Override
    public List<String> getUsageInstructions() {
        return Collections.singletonList("`:command` - Pay your respects");
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("rip");
    }

    @Override
    public List<String> getMiddleware() {
        return Collections.singletonList("throttle:user,1,5");
    }

    @Override
    public boolean onCommand(Message message, String[] args) {
        Statistics.addRespects();

        try {
            avaire.getDatabase().newQueryBuilder(Constants.STATISTICS_TABLE_NAME)
                .update(statement -> statement.setRaw("respects", "`respects` + 1"));
        } catch (SQLException ex) {
            return false;
        }

        MessageFactory.makeEmbeddedMessage(message.getChannel(), Color.decode("#2A2C31"))
            .setDescription(String.format("**%s** has paid their respects.", message.getMember().getEffectiveName()))
            .setFooter(String.format("%s Today, %s Overall", Statistics.getRespects(), getTotalRespects()))
            .queue();

        return true;
    }

    private int getTotalRespects() {
        try {
            return avaire.getDatabase().newQueryBuilder(Constants.STATISTICS_TABLE_NAME).get().first()
                .getInt("respects", Statistics.getRespects()) + 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }
}