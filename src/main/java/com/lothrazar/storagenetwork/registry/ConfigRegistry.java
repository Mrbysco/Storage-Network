package com.lothrazar.storagenetwork.registry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigRegistry {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  private static BooleanValue LOGSPAM;
  private static IntValue REFRESHTICKS;
  public static IntValue EXCHANGEBUFFER;
  private static BooleanValue RELOADONCHUNK;
  private static ConfigValue<List<String>> IGNORELIST;
  public static IntValue ITEMRANGE;
  public static IntValue RECIPEMAXTAGS;
  static {
    initConfig();
  }

  private static void initConfig() {
    COMMON_BUILDER.comment("General settings").push(StorageNetworkMod.MODID);
    LOGSPAM = COMMON_BUILDER.comment("Enable very spammy logs.  Sometimes useful for debugging. ").define("logSpam", false);
    RELOADONCHUNK = COMMON_BUILDER.comment(
        "\r\n If this is true, reload network when a chunk unloads, this keeps your network always up to date.  It has been reported that this cause lag and chunk load issues on servers, "
            + "so disable if you have any problems. ")
        .define("reloadNetworkWhenUnloadChunk", false);
    REFRESHTICKS = COMMON_BUILDER.comment("\r\n How often to auto-refresh a network (one second is 20 ticks)").defineInRange("autoRefreshTicks", 20, 2, 4096);
    List<String> list = new ArrayList<String>();
    list.add("extrautils2:playerchest");
    IGNORELIST = COMMON_BUILDER.comment("\r\n Disable these blocks from ever being able to connect to the network, they will be treated as a non-inventory.").define("NotallowedBlocks",
        list);
    EXCHANGEBUFFER = COMMON_BUILDER.comment("\r\n How many itemstacks from the network are visible to external connections through the storagenetwork:exchange.  "
        + "Too low and not all items can pass through, too large and there will be packet/buffer overflows.")
        .defineInRange("exchangeBufferSize", 1024, 1, 5000);
    ITEMRANGE = COMMON_BUILDER.comment("\r\n Maximum range of the Storage Remote and Crafting Remote.   -1 means unlimited.")
        .defineInRange("remoteMaxRange", -1, -1, Integer.MAX_VALUE / 256);
    RECIPEMAXTAGS = COMMON_BUILDER.comment("\r\n When matching items to recipes in the JEI + button, this is the maximum number of tags to serialize over the network when on a server.  Reduce if you get errors relating to Packet Sizes being too large (Minecraft 1.12.2 had this hardcoded at 5).")
        .defineInRange("jeiMaximumRecipeTags", 64, 5, 128);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public ConfigRegistry(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }

  public boolean logspam() {
    return LOGSPAM.get();
  }

  public boolean doReloadOnChunk() {
    return RELOADONCHUNK.get();
  }

  public int refreshTicks() {
    return REFRESHTICKS.get();
  }

  public List<String> ignorelist() {
    return IGNORELIST.get();
  }
}
