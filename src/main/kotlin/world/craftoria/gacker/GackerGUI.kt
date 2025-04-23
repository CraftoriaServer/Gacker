package world.craftoria.gacker

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GackerGUI {
    val gradeList = listOf<String>("B1", "B2", "B3", "B4", "M1", "M2", "D1", "D2", "D3")
    fun openGui(depList: MutableList<Pair<String, String>>): Inventory {
        val inv = Bukkit.createInventory(null, 54, Component.text("学科登録"))

        depList.forEachIndexed { index, (id, name) ->
            if (index >= inv.size) return@forEachIndexed

            val item = ItemStack(Material.PAPER)
            val meta = item.itemMeta

            meta?.displayName(Component.text("§f§l$name"))
            meta?.lore(listOf(Component.text("§7ID: $id")))

            item.itemMeta = meta
            inv.setItem(index, item)
        }

        return inv
    }

    fun gradeGui(): Inventory {
        val inv = Bukkit.createInventory(null, 54, Component.text("学年登録"))

        gradeList.forEachIndexed { index, id ->
            if (index >= inv.size) return@forEachIndexed

            val item = ItemStack(Material.PAPER)
            val meta = item.itemMeta

            meta?.displayName(Component.text("§f§l$id"))
            meta?.lore(listOf(Component.text("§7ID: $id")))

            item.itemMeta = meta
            inv.setItem(index, item)
        }

        return inv
    }
}