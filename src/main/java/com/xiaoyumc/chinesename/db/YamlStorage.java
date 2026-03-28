package com.xiaoyumc.chinesename.db;

import com.xiaoyumc.chinesename.ChineseName;
import com.xiaoyumc.chinesename.util.SchedulerUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class YamlStorage {

    private static final File FILE = new File(ChineseName.getInstance().getDataFolder(), "nick.yml");
    public static YamlConfiguration cfg;

    static {
        if (!FILE.exists())
            try {
                FILE.createNewFile();          // 立即创建空文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        cfg = YamlConfiguration.loadConfiguration(FILE);
    }

    public static String getName(String uuid) {
        return cfg.getString(uuid);
    }

    public static void setName(String uuid, String name) {
        cfg.set(uuid, name);
        // 异步保存 YAML 文件，避免阻塞主线程（Folia 兼容性）
        SchedulerUtil.runAsync(ChineseName.getInstance(), () -> {
            try {
                cfg.save(FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}