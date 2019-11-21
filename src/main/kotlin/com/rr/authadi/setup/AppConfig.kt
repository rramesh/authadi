package com.rr.authadi.setup

import com.rr.authadi.ServiceRunner.Companion.logger
import java.io.FileInputStream
import java.util.*

object AppConfig{
    private val envVars: MutableMap<String, String> by lazy { System.getenv() }
    private val properties: MutableMap<String, String> by lazy { loadConfig() }
    private val envPrefix by lazy { loadEnvironmentVar() }

    private fun loadEnvironmentVar() : String {
        var authadiEnv = envVars.get("AUTHADI_ENV")
        authadiEnv = authadiEnv ?: System.getProperty("authadi.env")
        return authadiEnv ?: ""
    }

    private fun loadConfig() : MutableMap<String, String>{
        val configFileName: String? = System.getProperty("authadi.propertyFile")
        return if (configFileName.isNullOrBlank()) {
            mutableMapOf<String, String>()
        } else {
            load(configFileName)
        }
    }

    private fun load(configFile: String) : MutableMap<String, String> {
        val props = Properties()
        val propsMap = mutableMapOf<String, String>()
        FileInputStream(configFile).use(props::load)
        props.forEach {
            (k,v) -> propsMap.put(k as String, v as String)
        }
        return propsMap
    }

    fun getServicePort() : Int {
        val port = envVars.get("SERVICE_PORT") ?: properties.get("service.port")
        if (port.isNullOrBlank()) return 15436
        return try{
            port.trim().toInt()
        } catch(nfe: NumberFormatException) {
            logger.warn("Warning: Non-numeric service port in service.port property. Defaulting to port 15436")
            15436
        }
    }

    fun dbProperties(prefix: String = envPrefix) : Map<String, String>{
        // Preference given to environment variables
        var delimiter =  if (prefix.isBlank()) "" else "_"
        val dbProps = filterDBProps(envVars, prefix, delimiter)
        if (dbProps.isNotEmpty()) {
            return dbProps
        }
        // Now check from property file
        delimiter =  if (prefix.isBlank()) "" else "."
        return filterDBProps(properties, prefix, delimiter)
    }

    private fun filterDBProps(props: MutableMap<String, String>, prefix: String, delimiter: String) : MutableMap<String, String> {
        val dbProps : MutableMap<String, String> = mutableMapOf<String, String>()
        props.filterKeys {
            it.startsWith("${prefix}${delimiter}DB", ignoreCase = true) }
                .mapKeys { envToProp(it.key, prefix, delimiter) }
                .forEach {(k,v) -> dbProps.put(k,v)}
        return dbProps
    }

    private fun envToProp(key: String, prefix: String, delimiter: String): String {
        return key.toLowerCase()
                .replace("${prefix}${delimiter}", "")
                .replace("_", ".")
    }
}