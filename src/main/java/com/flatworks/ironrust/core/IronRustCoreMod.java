package com.flatworks.ironrust.core;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

/**
 * @author sjx233
 */
@TransformerExclusions("com.flatworks.ironrust.core")
@MCVersion("1.10.2")
public class IronRustCoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.flatworks.ironrust.core.IronRustClassTransformer" };
    }
    
    @Override
    public String getModContainerClass() {
        return "com.flatworks.ironrust.core.IronRustModContainer";
    }
    
    @Override
    public String getSetupClass() {
        return null;
    }
    
    @Override
    public void injectData(Map<String, Object> data) {
    }
    
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
