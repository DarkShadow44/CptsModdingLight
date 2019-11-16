package yamhaven.easycoloredlights.blocks;

import java.util.List;

import coloredlightscore.src.api.CLApi;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;

public class CLStoneBright extends Block {
    public CLStoneBright() {
        super(Material.glass);
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @SideOnly(Side.CLIENT)
    private IIcon icons[];

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(ModInfo.ID + ":" + BlockInfo.CLStoneBright + i);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < 16; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            // Temporary
            return CLApi.makeRGBLightValue(15, 15, 15);
        } else {
            return CLApi.makeRGBLightValue(CLApi.r[meta] * 2 + 1, CLApi.g[meta] * 2 + 1, CLApi.b[meta] * 2 + 1);
        }
    }
}
