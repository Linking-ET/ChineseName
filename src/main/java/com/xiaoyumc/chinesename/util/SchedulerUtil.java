package com.xiaoyumc.chinesename.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * 跨平台调度器工具类 - 自动检测并适配 Folia/Spigot
 */
public final class SchedulerUtil {
    
    private static Boolean isFolia = null;
    private static Object regionScheduler = null;
    private static Method runMethod = null;
    private static Method entitySchedulerMethod = null;
    
    /**
     * 检测当前是否为 Folia 服务端
     */
    public static boolean isFolia() {
        if (isFolia != null) return isFolia;
        
        try {
            // 通过检查是否存在 Folia 特有的类来判断
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
        return isFolia;
    }
    
    /**
     * 初始化 Folia 反射（如果需要）
     */
    private static void initFoliaReflection(Plugin plugin) {
        if (!isFolia()) return;
        
        try {
            // 获取 Server 的 getGlobalRegionScheduler 方法
            Method getSchedulerMethod = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
            regionScheduler = getSchedulerMethod.invoke(plugin.getServer());
            
            // 获取 RegionScheduler 的 run 方法
            Class<?> regionSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            runMethod = regionSchedulerClass.getMethod("run", Plugin.class, Runnable.class);
            
            // 获取 EntityScheduler
            Class<?> entityClass = Class.forName("org.bukkit.entity.Entity");
            entitySchedulerMethod = entityClass.getMethod("getScheduler");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 异步执行任务（自动适配 Folia/Spigot）
     */
    public static void runAsync(Plugin plugin, Runnable task) {
        if (isFolia()) {
            if (regionScheduler == null) initFoliaReflection(plugin);
            
            try {
                // Folia: 使用 RegionScheduler 异步执行
                runMethod.invoke(regionScheduler, plugin, task);
            } catch (Exception e) {
                // 回退到直接执行
                task.run();
            }
        } else {
            // Spigot/Paper: 使用 Bukkit 调度器
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
    
    /**
     * 同步执行任务（在主线程中）
     */
    public static void runSync(Plugin plugin, Runnable task) {
        if (isFolia()) {
            if (regionScheduler == null) initFoliaReflection(plugin);
            
            try {
                // Folia: 使用 RegionScheduler 同步执行
                runMethod.invoke(regionScheduler, plugin, (Runnable) () -> {
                    // 已经在正确的线程中
                    task.run();
                });
            } catch (Exception e) {
                task.run();
            }
        } else {
            // Spigot/Paper: 使用 Bukkit 调度器
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * 延迟执行任务（同步）
     */
    public static void runLater(Plugin plugin, Runnable task, long delayTicks) {
        if (isFolia()) {
            if (regionScheduler == null) initFoliaReflection(plugin);
            
            try {
                // Folia: 使用 RegionScheduler 延迟执行
                Class<?> scheduledTaskClass = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
                Method runAtFixedRateMethod = regionScheduler.getClass()
                    .getMethod("runAtFixedRate", Plugin.class, Runnable.class, long.class, long.class);
                runAtFixedRateMethod.invoke(regionScheduler, plugin, task, delayTicks, -1L);
            } catch (Exception e) {
                task.run();
            }
        } else {
            // Spigot/Paper: 使用 Bukkit 调度器
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
    
    /**
     * 为实体执行任务（自动选择实体所在的区域线程）
     */
    public static void runOnEntity(Plugin plugin, Entity entity, Runnable task) {
        if (isFolia()) {
            try {
                if (entitySchedulerMethod == null) initFoliaReflection(plugin);
                
                // Folia: 使用 EntityScheduler
                Object entityScheduler = entitySchedulerMethod.invoke(entity);
                Method runMethod = entityScheduler.getClass().getMethod("run", Plugin.class, Runnable.class);
                runMethod.invoke(entityScheduler, plugin, task);
            } catch (Exception e) {
                task.run();
            }
        } else {
            // Spigot/Paper: 直接在主线程执行
            task.run();
        }
    }
}
