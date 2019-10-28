package com.rr.authadi.setup

import java.io.FileInputStream
import java.util.*

object AppConfig{
    private lateinit var separator : String
    val properties by lazy { loadConfig() }
    private fun loadConfig() : Map<String, String>{
        val configFileName: String? = System.getProperty("authadi.propertyFile")
        if(configFileName.isNullOrBlank()) {
            this.separator = "_"
            return System.getenv()
        }
        return load(configFileName)
    }

    private fun load(configFile: String) : Map<String, String> {
        val props = Properties()
        val propsMap = mutableMapOf<String, String>()
        FileInputStream(configFile).use(props::load)
        props.forEach {
            (k,v) -> propsMap.put(k as String, v as String)
        }
        this.separator = "."
        return propsMap
    }

    fun dbProperties(prefix: String? = "") : Map<String, String>{
        val dbProps : MutableMap<String, String> = mutableMapOf<String, String>()
        /*
        Initialization of separator within properties filter is very important
        as the value is determined whether configuration is loaded through file
        or through environment variables; and this is applicable only when a
        prefix is passed, example when running tests
         */
        val sanePrefix : String = prefix ?: ""
        properties.filterKeys {
                this.separator = if (sanePrefix.isBlank()) "" else this.separator
                it.startsWith("${sanePrefix}${separator}db", ignoreCase = true) }
                .mapKeys { envToProp(it.key) }
                .forEach { (k, v) -> dbProps.put(k,v)}
        return dbProps
    }

    private fun envToProp(key: String): String {
        return key.toLowerCase().replace("_", ".")
    }
}