package coloredlightscore.src.asm;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import coloredlightscore.fmlevents.ChunkDataEventHandler;
import coloredlightscore.network.PacketHandler;
import coloredlightscore.src.api.CLApi;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

public class ColoredLightsCoreDummyContainer extends DummyModContainer {
    public ChunkDataEventHandler chunkDataEventHandler;

    // This is picked up and replaced by the build.gradle
    public static final String version = "@VERSION@";

    // Reference to atomicstryker.dynamiclights.client.DynamicLights
    public static Object dynamicLights;

    // Reference to atomicstryker.dynamiclights.client.DynamicLights.getLightValue
    public static Method getDynamicLight = null;

    public ColoredLightsCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "lightoverhaulcore";
        meta.name = "Light Overhaul Core";
        meta.version = version;
        meta.logoFile = "/mod_ColoredLightCore.logo.png";
        meta.url = "https://www.curseforge.com/minecraft/mc-mods/light-overhaul";
        meta.credits = "";
        meta.authorList = Arrays.asList("heaton84", "Murray65536", "Kovu", "Biggerfisch", "CptSpaceToaster", "DarkShadow44");
        meta.description = "The coremod for Light Overhaul";
        meta.useDependencyInformation = true;
        chunkDataEventHandler = new ChunkDataEventHandler();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);

        return true;
    }

    static void setSkyColor() {
        try {
            Field field = EnumSkyBlock.class.getDeclaredFields()[2];
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);

            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            int color = (15 << CLApi.bitshift_sun_r) | (15 << CLApi.bitshift_sun_g) | (15 << CLApi.bitshift_sun_b) | 15;
            field.set(EnumSkyBlock.Sky, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {

        CLLog = evt.getModLog();

        CLLog.info("Starting up ColoredLightsCore");

        // Spin up network handler
        PacketHandler.init();

        // Hook into chunk events
        MinecraftForge.EVENT_BUS.register(chunkDataEventHandler);

        setSkyColor();
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt) {
        // Inject RGB values into vanilla blocks
        Blocks.lava.lightValue = CLApi.makeRGBLightValue(15, 10, 0);

        Blocks.flowing_lava.lightValue = CLApi.makeRGBLightValue(15, 10, 0);
        Blocks.torch.lightValue = CLApi.makeRGBLightValue(14, 13, 10);
        Blocks.fire.lightValue = CLApi.makeRGBLightValue(15, 13, 0);
        Blocks.lit_redstone_ore.lightValue = CLApi.makeRGBLightValue(9, 0, 0);
        Blocks.redstone_torch.lightValue = CLApi.makeRGBLightValue(7, 0, 0);
        Blocks.portal.lightValue = CLApi.makeRGBLightValue(6, 3, 11);
        Blocks.lit_furnace.lightValue = CLApi.makeRGBLightValue(13, 12, 10);
        Blocks.powered_repeater.lightValue = CLApi.makeRGBLightValue(9, 0, 0);

        Object thisShouldBeABlock;
        int l;
        Iterator<Block> blockRegistryInterator = GameData.getBlockRegistry().iterator();
        while (blockRegistryInterator.hasNext()) {
            thisShouldBeABlock = blockRegistryInterator.next();
            if (thisShouldBeABlock instanceof Block) {
                l = ((Block) thisShouldBeABlock).lightValue;
                if ((l > 0) && (l <= 0xF)) {
                    CLLog.info(((Block) thisShouldBeABlock).getLocalizedName() + "has light:" + l + ", but no color");
                    ((Block) thisShouldBeABlock).lightValue = (l << 15) | (l << 10) | (l << 5) | l; // copy vanilla brightness into each color component to make it white/grey.
                }
            }
        }

        Class<?> DynamicLightsClazz = null;
        try {
            DynamicLightsClazz = Class.forName("atomicstryker.dynamiclights.client.DynamicLights");
            Field instanceField = DynamicLightsClazz.getDeclaredField("instance");
            instanceField.setAccessible(true);
            dynamicLights = instanceField.get(null);
        } catch (ClassNotFoundException e) {
            CLLog.info("Dynamic Lights not found");
        } catch (NoSuchFieldException e) {
            CLLog.error("Missing field named \"instance\" in DynamicLights. Did versions change?");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            CLLog.error("We were denied access to the field \"instance\" in DynamicLights :(  Did versions change?");
            e.printStackTrace();
        }

        if (DynamicLightsClazz != null && dynamicLights != null) {
            CLLog.info("Hey DynamicLights... What's the plan?");

            try {
                getDynamicLight = DynamicLightsClazz.getDeclaredMethod("getLightValue", IBlockAccess.class, Block.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            } catch (NoSuchMethodException e) {
                CLLog.error("Missing method named \"getLightValue\" in DynamicLightsClazz. Did versions change?");
                e.printStackTrace();
            }
        }
    }
}