package com.darkshadow44.lightoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkshadow44.lightoverhaul.interfaces.IExtendedBlockStorageMixin;

import coloredlightscore.src.api.CLApi;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

@Mixin(ExtendedBlockStorage.class)
public abstract class ExtendedBlockStorageMixin implements IExtendedBlockStorageMixin {

    @Shadow
    private byte[] blockLSBArray;

    @Shadow
    private NibbleArray blocklightArray;

    @Shadow
    private NibbleArray skylightArray;

    public NibbleArray rColorArray;
    public NibbleArray gColorArray;
    public NibbleArray bColorArray;
    public NibbleArray rColorArray2;
    public NibbleArray gColorArray2;
    public NibbleArray bColorArray2;
    public NibbleArray rColorArraySun;
    public NibbleArray gColorArraySun;
    public NibbleArray bColorArraySun;

    public void setRedColorArray(NibbleArray array) {
        this.rColorArray = array;
    }

    public void setGreenColorArray(NibbleArray array) {
        this.gColorArray = array;
    }

    public void setBlueColorArray(NibbleArray array) {
        this.bColorArray = array;
    }

    public void setRedColorArray2(NibbleArray array) {
        this.rColorArray2 = array;
    }

    public void setGreenColorArray2(NibbleArray array) {
        this.gColorArray2 = array;
    }

    public void setBlueColorArray2(NibbleArray array) {
        this.bColorArray2 = array;
    }

    public void setRedColorArraySun(NibbleArray array) {
        this.rColorArraySun = array;
    }

    public void setGreenColorArraySun(NibbleArray array) {
        this.gColorArraySun = array;
    }

    public void setBlueColorArraySun(NibbleArray array) {
        this.bColorArraySun = array;
    }

    public NibbleArray getRedColorArray() {
        return this.rColorArray;
    }

    public NibbleArray getGreenColorArray() {
        return this.gColorArray;
    }

    public NibbleArray getBlueColorArray() {
        return bColorArray;
    }

    public NibbleArray getRedColorArray2() {
        return this.rColorArray2;
    }

    public NibbleArray getGreenColorArray2() {
        return this.gColorArray2;
    }

    public NibbleArray getBlueColorArray2() {
        return bColorArray2;
    }

    public NibbleArray getRedColorArraySun() {
        return this.rColorArraySun;
    }

    public NibbleArray getGreenColorArraySun() {
        return this.gColorArraySun;
    }

    public NibbleArray getBlueColorArraySun() {
        return bColorArraySun;
    }

    /***
     * @author darkshadow44
     * @reason TODO
     */
    @Inject(at = @At("RETURN"), method = { "<init>" })
    public void init(CallbackInfo callbackInfo) {
        this.rColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.gColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.bColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.rColorArray2 = new NibbleArray(this.blockLSBArray.length, 4);
        this.gColorArray2 = new NibbleArray(this.blockLSBArray.length, 4);
        this.bColorArray2 = new NibbleArray(this.blockLSBArray.length, 4);
        this.rColorArraySun = new NibbleArray(this.blockLSBArray.length, 4);
        this.gColorArraySun = new NibbleArray(this.blockLSBArray.length, 4);
        this.bColorArraySun = new NibbleArray(this.blockLSBArray.length, 4);
    }

    /***
     * @author darkshadow44
     * @reason TODO
     */
    @Overwrite
    public void setExtBlocklightValue(int x, int y, int z, int value) {
        int r = (value >> CLApi.bitshift_r) & CLApi.bitmask;
        int g = (value >> CLApi.bitshift_g) & CLApi.bitmask;
        int b = (value >> CLApi.bitshift_b) & CLApi.bitmask;
        int normal = Math.max(Math.max(r, g), b);
        normal = Math.min(15, normal);

        this.blocklightArray.set(x, y, z, value);
        this.rColorArray.set(x, y, z, r & 0xF);
        this.gColorArray.set(x, y, z, g & 0xF);
        this.bColorArray.set(x, y, z, b & 0xF);

        this.rColorArray2.set(x, y, z, r >> 4);
        this.gColorArray2.set(x, y, z, g >> 4);
        this.bColorArray2.set(x, y, z, b >> 4);
    }

    /***
     * @author darkshadow44
     * @reason TODO
     */
    @Overwrite
    public int getExtBlocklightValue(int x, int y, int z) {
        int normal = this.blocklightArray.get(x, y, z);
        int r = this.rColorArray.get(x, y, z);
        int g = this.gColorArray.get(x, y, z);
        int b = this.bColorArray.get(x, y, z);
        r |= this.rColorArray2.get(x, y, z) << 4;
        g |= this.gColorArray2.get(x, y, z) << 4;
        b |= this.bColorArray2.get(x, y, z) << 4;

        if (r == 0 && g == 0 && b == 0) {
            r = g = b = normal;
        }
        normal = Math.max(Math.max(r, g), b);
        normal = Math.min(15, normal);

        int ret = normal;
        ret |= r << CLApi.bitshift_r;
        ret |= g << CLApi.bitshift_g;
        ret |= b << CLApi.bitshift_b;

        return ret;
    }

    /***
     * @author darkshadow44
     * @reason TODO
     */
    @Overwrite
    public void setExtSkylightValue(int x, int y, int z, int value) {
        int r = (value >> CLApi.bitshift_sun_r) & CLApi.bitmask_sun;
        int g = (value >> CLApi.bitshift_sun_g) & CLApi.bitmask_sun;
        int b = (value >> CLApi.bitshift_sun_b) & CLApi.bitmask_sun;
        int normal = Math.max(Math.max(r, g), b);

        this.skylightArray.set(x, y, z, normal);
        this.rColorArraySun.set(x, y, z, r);
        this.gColorArraySun.set(x, y, z, g);
        this.bColorArraySun.set(x, y, z, b);
    }

    /***
     * @author darkshadow44
     * @reason TODO
     */
    @Overwrite
    public int getExtSkylightValue(int x, int y, int z) {
        int normal = this.skylightArray.get(x, y, z);
        int r = this.rColorArraySun.get(x, y, z);
        int g = this.gColorArraySun.get(x, y, z);
        int b = this.bColorArraySun.get(x, y, z);

        if (r == 0 && g == 0 && b == 0) {
            r = g = b = normal;
        }
        normal = Math.max(Math.max(r, g), b);

        int ret = normal;
        ret |= r << CLApi.bitshift_sun_r;
        ret |= g << CLApi.bitshift_sun_g;
        ret |= b << CLApi.bitshift_sun_b;

        return ret;
    }
}
