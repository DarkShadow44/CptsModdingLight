package coloredlightscore.src.asm;

import java.util.Map;

import coloredlightscore.src.asm.transformer.*;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;
import org.apache.logging.log4j.*;

@TransformerExclusions("coloredlightscore.*")
@MCVersion("1.7.10")
@Name("ColoredLightsCore")
@SortingIndex(1001)
public class ColoredLightsCoreLoadingPlugin implements IFMLLoadingPlugin {
    public static LaunchClassLoader CLASSLOADER;
    public static boolean MCP_ENVIRONMENT;

    public static org.apache.logging.log4j.Logger CLLog = LogManager.getLogger("coloredlightscore");

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                              TransformRenderBlocks.class.getName(),
                              TransformTessellator.class.getName(),
                              TransformPlayerInstance.class.getName(), 
                              TransformEntityPlayerMP.class.getName(),
                              TransformGuiIngameForge.class.getName(),
                              TransformOpenGlHelper.class.getName(),
                              TransformRenderingRegistry.class.getName()
                              };
    }

    @Override
    public String getModContainerClass() {
        return ColoredLightsCoreDummyContainer.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        MCP_ENVIRONMENT = !((Boolean) data.get("runtimeDeobfuscationEnabled")).booleanValue();
        NameMapper.getInstance().setObfuscated(!MCP_ENVIRONMENT);
        CLASSLOADER = (LaunchClassLoader) data.get("classLoader");
    }

    @Override
    public String getAccessTransformerClass() {
        return ColoredLightsCoreAccessTransformer.class.getName();
    }
}
