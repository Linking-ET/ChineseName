package com.xiaoyumc.chinesename.db;

import com.xiaoyumc.chinesename.ChineseName;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class YamlStorage {

    private static final File FILE = new File(ChineseName.getInstance().getDataFolder(), "nick.yml");
    public static YamlConfiguration cfg;

    static {
        if (!FILE.exists())
            ChineseName.getInstance().saveResource("nick.yml", false); // 空文件
        cfg = YamlConfiguration.loadConfiguration(FILE);
    }

    public static String getName(String uuid) {
        return cfg.getString(uuid);
    }

    public static void setName(String uuid, String name) {
        cfg.set(uuid, name);
        try {
            cfg.save(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}