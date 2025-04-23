package world.craftoria.gacker

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import world.craftoria.gacker.Gacker.Companion.instance
import world.craftoria.gacker.GackerGUI.gradeGui
import java.util.UUID

class GackerListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        Bukkit.getScheduler().runTaskAsynchronously(instance, Runnable {
            val mysql = MySQLManager(
                instance,
                Gacker.mysqlSettings[0],
                Gacker.mysqlSettings[1],
                Gacker.mysqlSettings[2],
                Gacker.mysqlSettings[3],
                Gacker.mysqlSettings[4]
            )

            val rs = mysql.query("SELECT * FROM gacker WHERE uuid = ?", hashMapOf(1 to player.uniqueId.toString()))

            if (rs == null) {
                player.sendMessage("§b自分の学部学科を登録しよう！ : /gacker register")
                return@Runnable
            }


        })
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val view = event.view

        if (event.clickedInventory == null || event.clickedInventory != view.topInventory) return

        val title = LegacyComponentSerializer.legacySection().serialize(view.title())

        val item = event.currentItem ?: return
        if (item.type == Material.AIR) return
        val meta = item.itemMeta ?: return
        val displayName = meta.displayName() ?: return

        when (title) {
            "学科登録" -> {
                event.isCancelled = true

                val departmentId = meta.lore()?.getOrNull(0)?.toString()?.replace("§7ID: ", "") ?: return
                val departmentName = displayName.toString().replace("§f§l", "")

                player.closeInventory()
                player.openInventory(gradeGui())
                player.sendMessage("§a学科を登録しました : $departmentName")

                DepartmentSession.tempData[player.uniqueId] = departmentId
            }

            "学年登録" -> {
                event.isCancelled = true

                val grade = displayName.toString()
                val departmentId = DepartmentSession.tempData[player.uniqueId] ?: run {
                    player.sendMessage("§c学科情報が見つかりません。先に学科を選択してください。")
                    return
                }

                player.closeInventory()

                Bukkit.getScheduler().runTaskAsynchronously(instance, Runnable {
                    val mysql = MySQLManager(
                        instance,
                        Gacker.mysqlSettings[0],
                        Gacker.mysqlSettings[1],
                        Gacker.mysqlSettings[2],
                        Gacker.mysqlSettings[3],
                        Gacker.mysqlSettings[4]
                    )

                    mysql.execute(
                        "INSERT INTO gacker (mcid, uuid, department_id, grade) VALUES (?, ?, ?, ?)",
                        hashMapOf(
                            1 to player.name,
                            2 to player.uniqueId.toString(),
                            3 to departmentId,
                            4 to grade
                        )
                    )
                    mysql.close()
                })

                player.sendMessage("§a学年を登録しました : $grade")
                player.sendMessage("§a次回ログイン時より有効になります")
            }
        }
    }
}

object DepartmentSession {
    val tempData = mutableMapOf<UUID, String>()
}