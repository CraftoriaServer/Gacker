package world.craftoria.gacker

import org.bukkit.plugin.java.JavaPlugin

class Gacker : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        instance = this
        getCommand("gacker")?.setExecutor(GackerCommand())
        server.pluginManager.registerEvents(GackerListener(), this)

        loadConfig()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun loadConfig() {
        saveDefaultConfig()

        val mysqlHost = config.getString("mysql.host")!!
        val mysqlUser = config.getString("mysql.user")!!
        val mysqlPassword = config.getString("mysql.pass")!!
        val mysqlPort = config.getString("mysql.port")!!
        val mysqlDatabase = config.getString("mysql.db")!!

        mysqlSettings.add(mysqlHost)
        mysqlSettings.add(mysqlUser)
        mysqlSettings.add(mysqlPassword)
        mysqlSettings.add(mysqlPort)
        mysqlSettings.add(mysqlDatabase)
    }

    companion object {
        lateinit var instance: Gacker

        val mysqlSettings: MutableList<String> = mutableListOf()
    }
}
