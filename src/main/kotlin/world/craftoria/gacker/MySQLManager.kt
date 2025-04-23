package world.craftoria.gacker

import org.bukkit.plugin.java.JavaPlugin
import java.sql.*
import java.util.logging.Level

class MySQLManager(private val plugin: JavaPlugin, private val host: String, private val user: String, private val pass: String, private val port: String, private val db: String) {

    private var connection: Connection? = null
    private var preStatement: PreparedStatement? = null
    private var statement: Statement? = null

    init {
        connect()
    }

    fun connect(): Boolean {
        return try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            connection = DriverManager.getConnection(
                "jdbc:mysql://$host:$port/$db?user=$user&password=$pass&useSSL=false"
            )

            if (!isConnected()) {
                plugin.logger.log(Level.SEVERE, "Could not connect to MySQL server! MySQLManager.java:connect()")
                false
            } else {
                plugin.logger.log(Level.INFO, "Connected to MySQL server.")
                true
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Could not connect to MySQL server! MySQLManager.java:connect()\n${e.message}")
            false
        }
    }

    fun commit() {
        try {
            connection?.commit()
        } catch (e: SQLException) {
            plugin.logger.log(Level.SEVERE, "Could not commit! MySQLManager.java:commit()\n${e.message}")
        }
    }

    fun execute(sql: String, placeholder: HashMap<Int, String>): Boolean {
        return try {
            if (!isConnected()) {
                plugin.logger.log(Level.SEVERE, "'connection' was null! MySQLManager.java:execute()")
                return false
            }

            preStatement = connection?.prepareStatement(sql)
            if (preStatement == null) {
                plugin.logger.log(Level.SEVERE, "Statement could not be prepared! MySQLManager:execute()")
                return false
            }

            for (i in 1..placeholder.size) {
                preStatement?.setString(i, placeholder[i])
            }

            preStatement?.execute()
            commit()
            true
        } catch (e: SQLException) {
            plugin.logger.log(Level.SEVERE, "Could not execute! MySQLManager.java:execute()\n${e.message}")
            false
        }
    }

    fun query(sql: String): ResultSet? {
        return try {
            if (!isConnected()) {
                plugin.logger.log(Level.SEVERE, "'connection' was null! MySQLManager.java:query()")
                return null
            }

            statement = connection?.createStatement()
            if (statement == null) {
                plugin.logger.log(Level.SEVERE, "Statement could not be prepared! MySQLManager:query()")
                return null
            }

            val rs = statement?.executeQuery(sql)
            rs
        } catch (e: SQLException) {
            plugin.logger.log(Level.SEVERE, "Could not query! MySQLManager.java:query()\n${e.message}")
            null
        }
    }

    fun query(sql: String, placeholder: HashMap<Int, String>): ResultSet? {
        return try {
            if (!isConnected()) {
                plugin.logger.log(Level.SEVERE, "'connection' was null! MySQLManager.java:query()")
                return null
            }

            preStatement = connection?.prepareStatement(sql)
            if (preStatement == null) {
                plugin.logger.log(Level.SEVERE, "Statement could not be prepared! MySQLManager:query()")
                return null
            }

            for (i in 1..placeholder.size) {
                preStatement?.setString(i, placeholder[i])
            }

            val rs = preStatement?.executeQuery()
            rs
        } catch (e: SQLException) {
            plugin.logger.log(Level.SEVERE, "Could not query! MySQLManager.java:query()\n${e.message}")
            null
        }
    }

    fun isConnected(): Boolean {
        return connection != null
    }

    fun close() {
        try {
            preStatement?.close()
            connection?.close()
        } catch (e: SQLException) {
            plugin.logger.log(Level.SEVERE, "Could not close the connection! MySQLManager.java:close()\n${e.message}")
        }
    }
}