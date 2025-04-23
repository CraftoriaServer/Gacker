package world.craftoria.gacker

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import world.craftoria.gacker.Gacker.Companion.instance
import world.craftoria.gacker.GackerGUI.openGui

class GackerCommand : CommandExecutor {
    override fun onCommand(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>?
    ): Boolean {

        val player = p0 as Player

        Bukkit.getScheduler().runTaskAsynchronously(instance, Runnable {
            val mysql = MySQLManager(
                instance,
                Gacker.mysqlSettings[0],
                Gacker.mysqlSettings[1],
                Gacker.mysqlSettings[2],
                Gacker.mysqlSettings[3],
                Gacker.mysqlSettings[4]
            )

            val departmentList = mutableListOf<Pair<String, String>>()
            val rs = mysql.query("SELECT * FROM gacker_department")
            if (rs != null) {
                while (rs.next()) {
                    val departmentId = rs.getString("id")
                    val departmentName = rs.getString("name")
                    departmentList.add(departmentId to departmentName)
                }
            }
            mysql.close()

            // メインスレッドでGUIを開く
            Bukkit.getScheduler().runTask(instance, Runnable {
                val inv = openGui(departmentList)
                player.openInventory(inv)
            })
        })
        return true
    }
}