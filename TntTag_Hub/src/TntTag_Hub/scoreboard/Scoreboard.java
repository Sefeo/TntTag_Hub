package TntTag_Hub.scoreboard;

import TntTag_Hub.Main;
import TntTag_Hub.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Scoreboard {

    private Manager manager;

    public Scoreboard(Manager manager) {
        this.manager = manager;
    }

    public static void updateScoreboard(Player p) { // обычный хабовский скорборд
        try {
            PreparedStatement statement = Main.getInstance().getConnection().prepareStatement(
                    "SELECT Money, Win, Game FROM users WHERE UUID = ?"
            );
            statement.setString(1, p.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            result.next();
            int coins = result.getInt("Money");
            int wins = result.getInt("Win");
            int games = result.getInt("Game");
            statement.close();
            result.close();

            ScoreboardManager managerSb = Bukkit.getScoreboardManager(); // это и всё ниже - создание самого скорборда и его полей
            final org.bukkit.scoreboard.Scoreboard board = managerSb.getNewScoreboard();
            final Objective objective = board.registerNewObjective("1", "2");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.BOLD + "§eTNT TAG");

            Score score = objective.getScore("§1");
            score.setScore(11);

            Score score1 = objective.getScore(" Баланс: §e" + coins + "⭐");
            score1.setScore(10);

            Score score2 = objective.getScore(" Побед: §e" + wins);
            score2.setScore(9);

            Score score3 = objective.getScore(" Игр: §e" + games);
            score3.setScore(8);

            Score score4 = objective.getScore("§4");
            score4.setScore(7);

            p.setScoreboard(board);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
